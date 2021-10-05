package coza.opencollab.sakai.impl.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.authz.api.SecurityAdvisor;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.coursemanagement.api.CourseManagementAdministration;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.coursemanagement.api.exception.IdNotFoundException;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;
import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SitePage;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserAlreadyDefinedException;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.user.api.UserIdInvalidException;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiproject.user.api.UserPermissionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import coza.opencollab.sakai.api.dao.ITSCourseDao;
import coza.opencollab.sakai.api.dao.ITSEnrollmentDao;
import coza.opencollab.sakai.api.dao.ITSLecturerDao;
import coza.opencollab.sakai.api.model.ITSCourse;
import coza.opencollab.sakai.api.model.ITSEnrollment;
import coza.opencollab.sakai.api.model.ITSLecturer;
import coza.opencollab.sakai.api.service.ITSIntegratorService;
import coza.opencollab.sakai.cm.SISClient;
import coza.opencollab.sakai.impl.GhanaITSIntegrationClient;
import coza.opencollab.sakai.impl.TestGhanaITSIntegrationClient;
import lombok.extern.slf4j.Slf4j;

/**
 * ITSIntegratorServiceImpl - ITSIntegratorService implementation
 * 
 * @author JC Gillman
 *
 */
@Slf4j
public class ITSIntegratorServiceImpl implements ITSIntegratorService, ApplicationContextAware {

	private UserDirectoryService userDirectoryService;
	private ServerConfigurationService serverConfigurationService;
	private AuthzGroupService authzGroupService;
	private SessionManager sessionManager;
	private SiteService siteService;
	private SecurityService securityService;
	private CourseManagementService cmService;
	private CourseManagementAdministration cmAdmin;

	private ApplicationContext applicationContext;
	private SISClient client;

	private ITSCourseDao courseDao;
	private ITSEnrollmentDao enrollmentDao;
	private ITSLecturerDao lecturerDao;

	private static final String BLANK_SPACE = " ";
	private static final String HYPHEN = "-";

	private static final String SEMESTER_S = "S";
	private static final String SEMESTER_SEM = "SEM";
	private static final String STUDENT_ROLE = "Student";
	private static final String INSTRUCTOR_ROLE = "Instructor";
	
	private static final String TOOL_OVERVIEW_TITEL = "Overview";

	private static final String TOOL_ID_SYNOPTIC_ANNOUNCEMENT = "sakai.synoptic.announcement";
	private static final String TOOL_ID_SUMMARY_CALENDAR = "sakai.summary.calendar";
	private static final String TOOL_ID_SYNOPTIC_CHAT = "sakai.synoptic.chat";
	private static final String TOOL_ID_SYNOPTIC_MESSAGECENTER = "sakai.synoptic.messagecenter";

	private final static List<String> DEFAULT_TOOL_ID_MAP;
	static {
		DEFAULT_TOOL_ID_MAP = new ArrayList<String>();
		DEFAULT_TOOL_ID_MAP.add("sakai.announcements");
		DEFAULT_TOOL_ID_MAP.add("sakai.siteinfo");
		DEFAULT_TOOL_ID_MAP.add("sakai.resources");
		DEFAULT_TOOL_ID_MAP.add("sakai.syllabus");
		DEFAULT_TOOL_ID_MAP.add("sakai.lessonbuildertool");
		DEFAULT_TOOL_ID_MAP.add("sakai.schedule");
		DEFAULT_TOOL_ID_MAP.add("sakai.forums");
		DEFAULT_TOOL_ID_MAP.add("sakai.assignment.grades");
		DEFAULT_TOOL_ID_MAP.add("sakai.samigo");
		DEFAULT_TOOL_ID_MAP.add("sakai.rubrics");
		DEFAULT_TOOL_ID_MAP.add("sakai.gradebookng");
		DEFAULT_TOOL_ID_MAP.add("sakai.messages");
		DEFAULT_TOOL_ID_MAP.add("sakai.sitestats");
		DEFAULT_TOOL_ID_MAP.add("sakai.poll");
		DEFAULT_TOOL_ID_MAP.add("sakai.dropbox");
		DEFAULT_TOOL_ID_MAP.add("sakai.chat");
		DEFAULT_TOOL_ID_MAP.add("sakai.commons");
		DEFAULT_TOOL_ID_MAP.add("sakai.mailtool");
		DEFAULT_TOOL_ID_MAP.add("sakai.mailbox");
	}
	
	private final static Map<String, String> TOOLS_WITH_SYNOPTIC_ID_MAP;
	static
	{
		TOOLS_WITH_SYNOPTIC_ID_MAP = new HashMap<String, String>();
		TOOLS_WITH_SYNOPTIC_ID_MAP.put("sakai.announcements", TOOL_ID_SYNOPTIC_ANNOUNCEMENT);
		TOOLS_WITH_SYNOPTIC_ID_MAP.put("sakai.schedule", TOOL_ID_SUMMARY_CALENDAR);
		TOOLS_WITH_SYNOPTIC_ID_MAP.put("sakai.chat", TOOL_ID_SYNOPTIC_CHAT);

		TOOLS_WITH_SYNOPTIC_ID_MAP.put("sakai.messages", TOOL_ID_SYNOPTIC_MESSAGECENTER);
		TOOLS_WITH_SYNOPTIC_ID_MAP.put("sakai.forums", TOOL_ID_SYNOPTIC_MESSAGECENTER);
		TOOLS_WITH_SYNOPTIC_ID_MAP.put("sakai.messagecenter", TOOL_ID_SYNOPTIC_MESSAGECENTER);
	}
	
	public ITSIntegratorServiceImpl() {
	}

	public void init() {
	}

	/**
	 * Synchronizes Ghana ITS course / enrollment / lecturers data
	 */
	@Override
	public void syncWithITS() {
		syncITSCourses();
		syncITSEnrollments();
		syncITSLecturers();
	}

	/**
	 * Get ITS course data from client and insert into its_course table
	 */
	public void syncITSCourses() {

		// Get all course from client, no duplicates
		List<ITSCourse> clientCourses = getClient().getITSCoursesData().stream().distinct().collect(Collectors.toList());

		// Get all existing course from Database for current & next year
		List<ITSCourse> itsCourses = courseDao.getITSCoursesForCurrentAndNextAcadYear(Calendar.getInstance().get(Calendar.YEAR));

		// Insert all new courses from client that does not exist in DB
		if (itsCourses.isEmpty()) {
			clientCourses.forEach(clientCourse -> {
				updateCourse(clientCourse);
			});
		} else {
			// Insert only new courses that has not already been inserted
			clientCourses.stream().filter(course -> !itsCourses.contains(course)).forEach(clientCourse -> {
				updateCourse(clientCourse);
			});
		}
	}

	private void updateCourse(ITSCourse clientCourse) {
		try {
			courseDao.updateITSCourse(clientCourse);
			log.info("ITS Course updated: " + clientCourse);
		} catch (Exception e) {
			log.error("ITS - syncITSCourses Exception" + e.getMessage(), e);
		}
	}

	/**
	 * Gets ITS enrollment data from client and insert into its_enrollment table,
	 * adding and removing enrollments based on ITS data
	 */
	public void syncITSEnrollments() {

		// Get all enrollments from client, no duplicates
		List<ITSEnrollment> clientEnrollments = getClient().getITSEnrollmentsData().stream().distinct().collect(Collectors.toList());

		// Get all existing enrollments from Database for current & next year and no Sakai site
		// id
		List<ITSEnrollment> itsEnrollments = enrollmentDao
				.getITSEnrollmentsForCurrentAndNextAcadYear(Calendar.getInstance().get(Calendar.YEAR));

		List<ITSEnrollment> added = clientEnrollments.stream().filter(
				enrollment1 -> itsEnrollments.stream().noneMatch(enrollment2 -> enrollment2.equals(enrollment1)))
				.collect(Collectors.toList());
		added.forEach(clientEnrollment -> {
			try {
				enrollmentDao.updateITSEnrollment(clientEnrollment);
				log.info("ITS Enrollment added: " + clientEnrollment);
			} catch (Exception e) {
				log.error("ITS - syncITSEnrollments Exception" + e.getMessage(), e);
			}
		});
	}

	/**
	 * Gets ITS lecturer data from client and insert into its_lecturer table
	 */
	public void syncITSLecturers() {

		// Get all Lecturers from client, no duplicates
		List<ITSLecturer> clientLecturers = getClient().getITSLecturersData().stream().distinct().collect(Collectors.toList());

		// Get all existing Lecturers from Database for current & next year
		List<ITSLecturer> itsLecturers = lecturerDao
				.getITSLecturersForCurrentAndNextAcadYear(Calendar.getInstance().get(Calendar.YEAR));

		// Insert all new Lecturers from client that does not exist in DB
		if (itsLecturers.isEmpty()) {
			clientLecturers.forEach(clientLecturer -> {
				updateLecturer(clientLecturer);
			});
		} else {
			clientLecturers.stream().filter(lecturer -> !itsLecturers.contains(lecturer)).forEach(clientLecturer -> {
				updateLecturer(clientLecturer);
			});
		}
	}

	private void updateLecturer(ITSLecturer clientLecturer) {
		try {
			lecturerDao.updateITSLecturer(clientLecturer);
			log.info("ITS Lecturer updated: " + clientLecturer);
		} catch (Exception e) {
			log.error("ITS - syncITSLecturers Exception" + e.getMessage(), e);
		}
	}

	/**
	 * Updating ITS courses
	 */
	public void updateITSCourses() {

		// Get all existing course from Database for current year and no Sakai site id
		List<ITSCourse> itsCourses = courseDao.getITSCoursesForCurrentAndNextAcadYear(Calendar.getInstance().get(Calendar.YEAR));

		if (!itsCourses.isEmpty()) {

			// Become admin in order to add sites
			SecurityAdvisor yesMan = new SecurityAdvisor() {
				public SecurityAdvice isAllowed(String userId, String function, String reference) {
					return SecurityAdvice.ALLOWED;
				}
			};

			try {

				loginToSakai();
				securityService.pushAdvisor(yesMan);

				itsCourses.forEach(course -> {
					createSakaiSite(course);
				});

			} catch (Exception e) {
				log.error(e.getMessage(), e);
			} finally {
				securityService.popAdvisor(yesMan);
				logoutFromSakai();
			}
		}
	}

	/**
	 * Updating ITS student Enrollments
	 */
	public void updateITSEnrollments() {

		// Get all Enrollments from the ITS enrollments table for current year, order
		// list by Sakai site Id
		List<ITSEnrollment> itsEnrollments = enrollmentDao
				.getITSEnrollmentsForCurrentAndNextAcadYear(Calendar.getInstance().get(Calendar.YEAR));

		if (!itsEnrollments.isEmpty()) {

			// Build a Map with Sakai site Ids as Key, and a list of enrolled student
			// numbers as Value
			Map<String, List<ITSEnrollment>> enrollmentMap = new HashMap<String, List<ITSEnrollment>>();
			for (Iterator iterator = itsEnrollments.iterator(); iterator.hasNext();) {
				ITSEnrollment itsEnrollment = (ITSEnrollment) iterator.next();
				String sakaiSiteId = generateSiteNameId(itsEnrollment);
				if (StringUtils.isBlank(sakaiSiteId))
					continue;
				List<ITSEnrollment> studEnrollList = enrollmentMap.get(sakaiSiteId);
				if (studEnrollList != null) {
					studEnrollList.add(itsEnrollment);
				} else {
					studEnrollList = new ArrayList<ITSEnrollment>();
					studEnrollList.add(itsEnrollment);
				}
				enrollmentMap.put(sakaiSiteId, studEnrollList);
			}

			// Cheating to become admin in order to add sites
			SecurityAdvisor yesMan = new SecurityAdvisor() {
				public SecurityAdvice isAllowed(String userId, String function, String reference) {
					return SecurityAdvice.ALLOWED;
				}
			};

			try {

				loginToSakai();
				securityService.pushAdvisor(yesMan);				

				Iterator<Entry<String, List<ITSEnrollment>>> iterator = enrollmentMap.entrySet().iterator();
				while (iterator.hasNext()) {
					Entry<String, List<ITSEnrollment>> entry = iterator.next();

					String sakaiSiteId = entry.getKey();
					List<ITSEnrollment> siteEnrollments = entry.getValue();
					ITSEnrollment enrollment = siteEnrollments.get(0);

					// For each Sakai site (Key), get enrolled students from the Map and create a
					// Sakai site if it does not exist yet
					Site site = getSakaiSiteForEnrollment(sakaiSiteId, enrollment);
					if (site == null) {
						log.warn("No Site found or could be created for enrollment: " + enrollment);
						continue;
					}
					// Update enrollments for site, adding/removing students
					updateSakaiCourseEnrollment(site, siteEnrollments);
				}

			} catch (Exception e) {
				log.error(e.getMessage(), e);
			} finally {
				securityService.popAdvisor(yesMan);
				logoutFromSakai();
			}
		}
	}

	/**
	 * Adding/Removing Sakai Course site student enrollments
	 * 
	 * @param site
	 * @param siteEnrollments
	 */
	private void updateSakaiCourseEnrollment(Site site, List<ITSEnrollment> siteEnrollments) {

		//Create studentId list from ITSEnrollment list Object 
		List<String> itsEnrolledStudentNumbers = siteEnrollments.stream().distinct()
				.map(enrollment -> Integer.toString(enrollment.getIAGSTNO())).collect(Collectors.toList());

		//Create Map from ITSEnrollment list Object with studentId (Key), enrollment (Value) 
		Map<Integer, ITSEnrollment> itsUserMap = siteEnrollments
				.stream().collect(Collectors.toMap(ITSEnrollment::getIAGSTNO, itsEnrollment -> itsEnrollment, (existingValue, newValue) -> newValue));

		//Get all users in site with Student role
		Set<String> students = site.getUsersHasRole(STUDENT_ROLE);

		//Get all new ITS Students to be added to site
		List<String> added = itsEnrolledStudentNumbers.stream()
				.filter(student1 -> students.stream().noneMatch(student2 -> student2.equals(student1)))
				.collect(Collectors.toList());
		
		//Add Students to site
		added.forEach(studentNum -> {
			try {
				ITSEnrollment enrollment = itsUserMap.get(Integer.valueOf(studentNum));
				if(enrollment != null) {
					//If user was added successfully, update enrollment with SakaiSiteId
					if(addUserToCourseSite(site, Integer.toString(enrollment.getIAGSTNO()), enrollment.getOtherNames(),
							enrollment.getSurname(), enrollment.getEmail(), enrollment.getPin(), STUDENT_ROLE)) {
						enrollment.setSakaiSiteId(site.getId());
						enrollmentDao.updateITSEnrollment(enrollment);
					}
				}
			} catch (Exception e) {
				log.error("ITS - updateSakaiCourseEnrollment Exception" + e.getMessage(), e);
			}				
		});
	}

	/**
	 * Looks up Sakai Site, if the site does not exist, one will be created
	 * 
	 * @param sakaiSiteId
	 * @param enrollment
	 * @return
	 */
	private Site getSakaiSiteForEnrollment(String sakaiSiteId, ITSEnrollment enrollment) {
		Site site = null;
		try {
			site = siteService.getSite(sakaiSiteId);
		} catch (IdUnusedException e) {

			ITSCourse course = courseDao.findCourseForParams(enrollment.getAcadYear(), enrollment.getCourseCode(),
					enrollment.getCampusCode(), enrollment.getSemCode());
			if (course == null) {
				log.warn(
						"ITS - Could not find the ITS course for Site " + sakaiSiteId + " and enrollemt " + enrollment);
			} else {
				createSakaiSite(course);

				try {
					site = siteService.getSite(course.getSakaiSiteId());
				} catch (IdUnusedException e1) {
					log.error(
							"ITS - Could not find the ITS course for Site " + sakaiSiteId + " and enrollemt " + enrollment);
				}
			}
		}
		return site;
	}

	/**
	 * Updating ITS lecturers
	 */
	public void updateITSLecturers() {

		// Get all Lecturers from the ITS Lecturers table for current year, order list
		// by Sakai site Id
		List<ITSLecturer> itsLecturers = lecturerDao
				.getITSLecturersForCurrentAndNextAcadYear(Calendar.getInstance().get(Calendar.YEAR));

		if (!itsLecturers.isEmpty()) {

			// Build a Map with Sakai site Ids as Key, and a list of Course site staff
			// numbers as Value
			Map<String, List<ITSLecturer>> lecturerMap = new HashMap<String, List<ITSLecturer>>();
			for (Iterator iterator = itsLecturers.iterator(); iterator.hasNext();) {
				ITSLecturer itsLecturer = (ITSLecturer) iterator.next();
				String sakaiSiteId = generateSiteNameId(itsLecturer);
				if (StringUtils.isBlank(sakaiSiteId))
					continue;
				List<ITSLecturer> lecturerList = lecturerMap.get(sakaiSiteId);
				if (lecturerList != null) {
					lecturerList.add(itsLecturer);
				} else {
					lecturerList = new ArrayList<ITSLecturer>();
					lecturerList.add(itsLecturer);
				}
				lecturerMap.put(sakaiSiteId, lecturerList);
			}

			//Build a Map with student number as Key, student email address as Value
			Map<String, String> staffNumEmailMap = new HashMap<String, String>();
			String staffNum = null;
			for (Iterator iterator = itsLecturers.iterator(); iterator.hasNext();) {
				ITSLecturer itsLecturer = (ITSLecturer) iterator.next();
				staffNum = Integer.toString(itsLecturer.getSTAFFNO());
				String email = staffNumEmailMap.get(staffNum);
				if (email == null) {
					staffNumEmailMap.put(staffNum, itsLecturer.getEmail());
				}
			}

			// Cheating to become admin in order to add sites
			SecurityAdvisor yesMan = new SecurityAdvisor() {
				public SecurityAdvice isAllowed(String userId, String function, String reference) {
					return SecurityAdvice.ALLOWED;
				}
			};

			try {

				loginToSakai();
				securityService.pushAdvisor(yesMan);

				Iterator<Entry<String, List<ITSLecturer>>> iterator = lecturerMap.entrySet().iterator();
				while (iterator.hasNext()) {
					Entry<String, List<ITSLecturer>> entry = iterator.next();

					String sakaiSiteId = entry.getKey();
					List<ITSLecturer> siteLecturers = entry.getValue();
					ITSLecturer lecturer = siteLecturers.get(0);

					// For each Sakai site (Key), get lecturers from the Map and create a Sakai site
					// if it does not exist yet
					Site site = getSakaiSiteForLecturer(sakaiSiteId, lecturer);
					if (site == null) {
						log.warn("No Site found or could be created for lecturer: " + lecturer);
						continue;
					}
					// Update Lecturers for site, adding/removing lecturers
					updateSakaiCourseLecturer(site, siteLecturers);
				}

			} catch (Exception e) {
				log.error(e.getMessage(), e);
			} finally {
				securityService.popAdvisor(yesMan);
				logoutFromSakai();
			}
		}
	}

	/**
	 * Adding/Removing Sakai Course site Lecturers
	 * 
	 * @param site
	 * @param siteLecturers
	 */
	private void updateSakaiCourseLecturer(Site site, List<ITSLecturer> siteLecturers) {

		//Get list of ITS lecturers staff numbers
		List<String> itsStaffNumbers = siteLecturers.stream().map(lecturer -> Integer.toString(lecturer.getSTAFFNO()))
				.collect(Collectors.toList());
		
		Map<Integer, ITSLecturer> itsUserMap = siteLecturers
				.stream().collect(Collectors.toMap(ITSLecturer::getSTAFFNO, itsLecturer -> itsLecturer, (existingValue, newValue) -> newValue));
				
		//Get all users in site with Instructor role
		Set<String> instructors = site.getUsersHasRole(INSTRUCTOR_ROLE);

		//Get all new ITS Instructors to be added to site
		List<String> added = itsStaffNumbers.stream()
				.filter(instructor1 -> instructors.stream().noneMatch(instructor2 -> instructor2.equals(instructor1)))
				.collect(Collectors.toList());

		//Add Instructors to site
		added.forEach(staffNum -> {
			
			try {
				ITSLecturer lecturer = itsUserMap.get(Integer.valueOf(staffNum));
				if(lecturer != null) {
					if(addUserToCourseSite(site, Integer.toString(lecturer.getSTAFFNO()), lecturer.getOtherNames(),
							lecturer.getSurname(), lecturer.getEmail(), lecturer.getPin(), INSTRUCTOR_ROLE)) {
						lecturer.setSakaiSiteId(site.getId());
						lecturerDao.updateITSLecturer(lecturer);

						//Publish Sakai site if an Instructor has been added
						if(!site.isPublished()) {
							site.setPublished(true);
							try {
								siteService.save(site);
							} catch (IdUnusedException e) {
								log.error("ITS updateSakaiCourseLecturer.IdUnusedException : Sakai site: " + site.getId(), e);
							} catch (PermissionException e) {
								log.error("ITS updateSakaiCourseLecturer.PermissionException : Sakai site: " + site.getId(), e);
							}
						}
					}
				}
			} catch (Exception e) {
				log.error("ITS - updateSakaiCourseLecturer Exception" + e.getMessage(), e);
			}
		});
	}

	/**
	 * Add User to Sakai course site
	 * 
	 * @param site
	 * @param userId
	 * @param firstName
	 * @param lastName
	 * @param email
	 * @param password
	 * @param role
	 * @return
	 */
	private boolean addUserToCourseSite(Site site, String userId, String firstName, String lastName, String email, String password, String role) {		
		User user = null;		
		try {
			user = userDirectoryService.getUserByEid(userId);			
		} catch (UserNotDefinedException e) {
			try {
				user = userDirectoryService.addUser(userId, userId, firstName, lastName, email,
						password, "registered", null);
				log.info("ITS new Sakai user created: " + userId + ", " + firstName + ", " + lastName + ", " + email, e);
			} catch (Exception ex) {
				log.error("ITS addStudentEnrollmentToSite.UserNotDefinedException: Failed to create Sakai user: " + userId, ex);
			}
		} 
		if(user != null) {
			site.addMember(user.getId(), role, true, false);
			try {
	            siteService.saveSiteMembership(site);
				log.info("ITS userId : " + user.getEid() + " added to Sakai site: " + site.getId());
				return true;
			} catch (IdUnusedException e) {
				log.error("ITS addStudentEnrollmentToSite.IdUnusedException : " + userId + " for Sakai site: " + site.getId(), e);
			} catch (PermissionException e) {
				log.error("ITS addStudentEnrollmentToSite.PermissionException : " + userId + " for Sakai site: " + site.getId(), e);
			}
		}
		return false;
	}

	/**
	 * Looks up Sakai Site, if the site does not exist, one will be created
	 * 
	 * @param sakaiSiteId
	 * @param lecturer
	 * @return
	 */
	private Site getSakaiSiteForLecturer(String sakaiSiteId, ITSLecturer lecturer) {
		Site site = null;
		try {
			site = siteService.getSite(sakaiSiteId);
		} catch (IdUnusedException e) {

			ITSCourse course = courseDao.findCourseForParams(lecturer.getAcadYear(), lecturer.getCourse(),
					lecturer.getOfferingType(), lecturer.getSemester());
			if (course == null) {
				log.warn("ITS - Could not find the ITS course for Site " + sakaiSiteId + " and lecturer " + lecturer);
			} else {
				site = createSakaiSite(course);
			}
		}
		return site;
	}

	/**
	 * Create Sakai course site
	 * 
	 * @param course
	 * @return
	 */
	private Site createSakaiSite(ITSCourse course) {

		log.info("ITS - Creating unpublished Sakai Site for ITS Course");
		Site newSite = null;
		try {
			String siteName = generateSiteName(course.getAcadYear(), course.getCourseCode(), course.getCampusCode(),
					course.getSemCode());
			if (StringUtils.isBlank(siteName))
				return null;
			String siteId = siteName.replace(BLANK_SPACE, HYPHEN);
			if(!siteService.siteExists(siteId)) {
				String description = generateCourseDescription(course);
				newSite = siteService.addSite(siteId, "course");
				newSite.setTitle(siteName);
				newSite.setDescription(description);
				newSite.setShortDescription(description);
				newSite.setPublished(false);
				newSite.setJoinable(false);

				addDefaultSakaiTools(newSite);
				
				String term = generateTerm(course);
				String termEid = generateTermEid(course);

				ResourcePropertiesEdit propEdit = newSite.getPropertiesEdit();
				propEdit.addProperty("term", term);
				propEdit.addProperty("term_eid", termEid);
				siteService.save(newSite);
				
				//Create Academic session if not found
				try {
					cmService.getAcademicSession(termEid);
//					log.info("Found AcademicSession with id " + termEid);
				} catch (IdNotFoundException e) {
					cmAdmin.createAcademicSession(termEid, term, generateTermDescription(course), null, null);
					log.info("Created AcademicSession with id " + termEid);
				}

				course.setSakaiSiteId(newSite.getId());
				courseDao.updateITSCourse(course);

				log.info("ITS - New Course site created: " + siteId);
			}

		} catch (IdInvalidException | PermissionException e) {
			log.error("createSakaiSite: " + e.getMessage(), e);
		} catch (IdUsedException e) {
			log.error("createSakaiSite - IdUsedException: " + e.getId(), e);
		} catch (Exception e) {
			log.error("createSakaiSite: " + e.getMessage(), e);
		}
		return newSite;
	}
	
	/**
	 * Adds lits of default Sakai Tools
	 * 
	 * @param site
	 */
	private void addDefaultSakaiTools(Site site) {

		SitePage overviewPage = site.addPage();
		ToolConfiguration overviewToolConfig = overviewPage.addTool("sakai.iframe.site");
		
		overviewPage.getPropertiesEdit().addProperty(
				SitePage.IS_HOME_PAGE, Boolean.TRUE.toString());
		overviewToolConfig.setTitle(TOOL_OVERVIEW_TITEL);

		int synopticToolIndex = 0;

		for (String toolId : DEFAULT_TOOL_ID_MAP) {

			if (TOOLS_WITH_SYNOPTIC_ID_MAP.containsKey(toolId)) {

				// add tool
				site.addPage().addTool(toolId);

				ToolConfiguration toolConfig = overviewPage.addTool(toolId);
				if (null != toolConfig) {
					toolConfig.setLayoutHints(synopticToolIndex + ",1");
					synopticToolIndex++;
				}
			} else {
				site.addPage().addTool(toolId);
			}
		}

		// for synoptic tools
		if (synopticToolIndex > 0) {
			overviewPage.setLayout(SitePage.LAYOUT_DOUBLE_COL);
		}
	}
	
	/**
	 * Sakai admin login
	 */
	private void loginToSakai() {
		User user = null;
		String userId = serverConfigurationService.getString("its.integration.admin.username", "adminITS");
		try {
			user = userDirectoryService.getUserByEid(userId);
		} catch (UserNotDefinedException e) {
			//user doesn't exist, lets make it:
			try {
				String password = serverConfigurationService.getString("its.integration.admin.password", "adminITS123!@#");
				String email = serverConfigurationService.getString("its.integration.admin.email", "itsAdmin@st.ug.edu.gh");
				
				//String id, String eid, String firstName, String lastName, String email, String pw, String type, ResourceProperties properties
				user = userDirectoryService.addUser(userId, userId, userId, userId, email, password, "registered", null);
				
			} catch (UserIdInvalidException e1) {
				log.error("ITS Admin userId UserIdInvalidException, userId: " + userId, e1);
			} catch (UserAlreadyDefinedException e1) {
				log.error("ITS Admin userId UserAlreadyDefinedException, userId: " + userId, e1);
			} catch (UserPermissionException e1) {
				log.error("ITS Admin userId UserPermissionException, userId: " + userId, e1);
			}
		}
		if(user != null){
			Session sakaiSession = sessionManager.getCurrentSession();
			sakaiSession.setUserId(user.getId());
			sakaiSession.setUserEid(user.getEid());

			// update the user's externally provided realm definitions
			authzGroupService.refreshUser(user.getId());
		}	
	}
	

	/**
	 * Sakai admin logout
	 */
	private void logoutFromSakai() {
		Session sakaiSession = sessionManager.getCurrentSession();
		sakaiSession.invalidate();
	}

	/**
	 * Helper method to create Sakai course site term value
	 * 
	 * @param course
	 * @return
	 */
	private String generateTerm(ITSCourse course) {
		StringBuilder term = new StringBuilder();
		term.append(SEMESTER_SEM).append(BLANK_SPACE).append(course.getSemCode()).append(BLANK_SPACE);
		int yearLastTwoDigits = course.getAcadYear() % 100;
		term.append("" + (yearLastTwoDigits - 1)).append("/" + yearLastTwoDigits);
		return term.toString();
	}

	/**
	 * Helper method to create Sakai course site term_eid value
	 * 
	 * @param course
	 * @return
	 */
	private String generateTermEid(ITSCourse course) {
		StringBuilder termEid = new StringBuilder();
		termEid.append(SEMESTER_S).append(course.getSemCode()).append(HYPHEN);
		int yearLastTwoDigits = course.getAcadYear() % 100;
		termEid.append("" + (yearLastTwoDigits - 1)).append("" + yearLastTwoDigits);
		return termEid.toString();
	}
	
	/**
	 * Helper method to create Sakai course site term description value
	 * 
	 * @param course
	 * @return
	 */
	private String generateTermDescription(ITSCourse course) {
		StringBuilder term = new StringBuilder();
		term.append(course.getSemester()).append(BLANK_SPACE);
		int yearLastTwoDigits = course.getAcadYear() % 100;
		term.append("" + (yearLastTwoDigits - 1)).append("/" + yearLastTwoDigits);
		return term.toString();
	}

	/**
	 * Helper method to create Sakai course site description value
	 * 
	 * @param course
	 * @return
	 */
	private String generateCourseDescription(ITSCourse course) {
		StringBuilder description = new StringBuilder();
		description.append(course.getCourseCode()).append(BLANK_SPACE);
		description.append(course.getCourse()).append(BLANK_SPACE);
		description.append(course.getCampus()).append(BLANK_SPACE);
		description.append(course.getSemester()).append(BLANK_SPACE);
		description.append("" + course.getAcadYear());
		return description.toString();
	}

	/**
	 * Helped method to get Sakai site Id
	 * 
	 * @param itsEnrollment
	 * @return
	 */
	private String generateSiteNameId(ITSEnrollment itsEnrollment) {
		return generateSiteName(itsEnrollment).replace(BLANK_SPACE, HYPHEN);
	}

	/**
	 * Helped method to get Sakai site Id
	 * 
	 * @param itsLecturer
	 * @return
	 */
	private String generateSiteNameId(ITSLecturer itsLecturer) {
		return generateSiteName(itsLecturer).replace(BLANK_SPACE, HYPHEN);
	}

	/**
	 * Helper method to create Sakai course site title value
	 * 
	 * @param enrollment
	 * @return
	 */
	private String generateSiteName(ITSEnrollment enrollment) {
		return generateSiteName(enrollment.getAcadYear(), enrollment.getCourseCode(), enrollment.getCampusCode(),
				enrollment.getSemCode());
	}

	/**
	 * Helper method to create Sakai course site title value
	 * 
	 * @param itsLecturer
	 * @return
	 */
	private String generateSiteName(ITSLecturer itsLecturer) {
		return generateSiteName(itsLecturer.getAcadYear(), itsLecturer.getCourse(), itsLecturer.getOfferingType(),
				itsLecturer.getSemester());
	}

	/**
	 * Helper method to create Sakai course site title value
	 * 
	 * @param acadYear
	 * @param courseCode
	 * @param campusCode
	 * @param semCode
	 * @return
	 */
	private String generateSiteName(int acadYear, String courseCode, String campusCode, String semCode) {
		StringBuilder siteid = new StringBuilder();
		try {
			String courseSplitArray[] = courseCode.split("(?<=\\D)(?=\\d)");
			String campusCodeVal = campusCode.startsWith("0") ? campusCode.substring(1) : campusCode;
			int yearLastTwoDigits = acadYear % 100;

			siteid.append(courseSplitArray[0]).append(BLANK_SPACE).append(courseSplitArray[1]).append(BLANK_SPACE);
			siteid.append(campusCodeVal).append(BLANK_SPACE);
			siteid.append(SEMESTER_S).append(semCode).append(HYPHEN);
			siteid.append("" + (yearLastTwoDigits - 1)).append("" + yearLastTwoDigits);
		} catch (Exception e) {
			log.warn("ITS - Could not create siteId generateSiteId() for : acadYear:" + acadYear + ", courseCode: "
					+ courseCode + ", campusCode: " + campusCode + ", semCode: " + semCode);
			return null;
		}
		return siteid.toString();
	}

	/**
	 * Set Sakai client integration implementation
	 * 
	 * @return
	 */
	public SISClient getClient() {
		if (client == null) {
			String integrationClient = serverConfigurationService.getString("its.client.integration", "ORACLE");
			switch (integrationClient) {
			case "MYSQL":
				client = (TestGhanaITSIntegrationClient) applicationContext
						.getBean("coza.opencollab.sakai.impl.TestGhanaITSIntegrationClient");
				break;
			default:
				client = (GhanaITSIntegrationClient) applicationContext
						.getBean("coza.opencollab.sakai.impl.GhanaITSIntegrationClient");
			}
		}
		return client;
	}

	public ITSCourseDao getCourseDao() {
		return courseDao;
	}

	public void setCourseDao(ITSCourseDao courseDao) {
		this.courseDao = courseDao;
	}

	public ITSEnrollmentDao getEnrollmentDao() {
		return enrollmentDao;
	}

	public void setEnrollmentDao(ITSEnrollmentDao enrollmentDao) {
		this.enrollmentDao = enrollmentDao;
	}

	public ITSLecturerDao getLecturerDao() {
		return lecturerDao;
	}

	public void setLecturerDao(ITSLecturerDao lecturerDao) {
		this.lecturerDao = lecturerDao;
	}

	public UserDirectoryService getUserDirectoryService() {
		return userDirectoryService;
	}

	public void setUserDirectoryService(UserDirectoryService userDirectoryService) {
		this.userDirectoryService = userDirectoryService;
	}

	public ServerConfigurationService getServerConfigurationService() {
		return serverConfigurationService;
	}

	public void setServerConfigurationService(ServerConfigurationService serverConfigurationService) {
		this.serverConfigurationService = serverConfigurationService;
	}

	public AuthzGroupService getAuthzGroupService() {
		return authzGroupService;
	}

	public void setAuthzGroupService(AuthzGroupService authzGroupService) {
		this.authzGroupService = authzGroupService;
	}

	public SessionManager getSessionManager() {
		return sessionManager;
	}

	public void setSessionManager(SessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

	public SecurityService getSecurityService() {
		return securityService;
	}

	public void setSecurityService(SecurityService securityService) {
		this.securityService = securityService;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public SiteService getSiteService() {
		return siteService;
	}

	public void setSiteService(SiteService siteService) {
		this.siteService = siteService;
	}

	public void setClient(SISClient client) {
		this.client = client;
	}
	
	public CourseManagementService getCmService() {
		return cmService;
	}

	public void setCmService(CourseManagementService cmService) {
		this.cmService = cmService;
	}
	
	public CourseManagementAdministration getCmAdmin() {
		return cmAdmin;
	}

	public void setCmAdmin(CourseManagementAdministration cmAdmin) {
		this.cmAdmin = cmAdmin;
	}
}
