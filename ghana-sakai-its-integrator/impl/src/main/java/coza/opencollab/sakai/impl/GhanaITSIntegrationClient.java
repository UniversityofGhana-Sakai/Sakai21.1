package coza.opencollab.sakai.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.sakaiproject.component.api.ServerConfigurationService;

import coza.opencollab.sakai.api.model.ITSCourse;
import coza.opencollab.sakai.api.model.ITSEnrollment;
import coza.opencollab.sakai.api.model.ITSLecturer;
import coza.opencollab.sakai.api.model.ITSUser;
import coza.opencollab.sakai.cm.SISClient;
import lombok.extern.slf4j.Slf4j;

/**
 * Ghana ITS Integration Client
 *
 * @author JC Gillman
 */
@Slf4j
public class GhanaITSIntegrationClient implements SISClient {

	private ServerConfigurationService serverConfigurationService;
	private String connectionUrl = null;
	private String username = null;
	private String password = null;

	private Connection connection = null;

	public GhanaITSIntegrationClient() {
	}

	public void init() {
		connectionUrl = serverConfigurationService.getString("its.client.integration.oracle.url",
				"jdbc:oracle:thin:@10.2.2.90:1810:prodi41");
		username = serverConfigurationService.getString("its.client.integration.oracle.username", "sakaisys");
		password = serverConfigurationService.getString("its.client.integration.oracle.password", "sakai123");
	}

	@Override
	public List<ITSCourse> getITSCoursesData() {
		List<ITSCourse> courseList = new ArrayList<ITSCourse>();
		String sqlSelectAllCourses = "select * from courses";

		
		try {
			PreparedStatement ps = getConnection().prepareStatement(sqlSelectAllCourses);
			ResultSet rs = ps.executeQuery();
			ITSCourse course = null;
			while (rs.next()) {
				course = new ITSCourse();
				course.setAcadYear(rs.getInt("ACAD_YR"));
				String courseCode = rs.getString("COURSE_CODE");
				if(StringUtils.isBlank(courseCode)) {
					log.warn("courseCode value is blank");
					continue;
				}
				course.setCourseCode(getTrimmedValue(courseCode));
				String courseVal = rs.getString("COURSE");
				if(StringUtils.isBlank(courseVal)) {
					log.warn("course value is blank");
					continue;
				}
				course.setCourse(getTrimmedValue(courseVal));
				String semCode = rs.getString("SEM_CODE");
				if(StringUtils.isBlank(semCode)) {
					log.warn("semCode value is blank");
					continue;
				}
				course.setSemCode(getTrimmedValue(semCode));
				String semester = rs.getString("SEMESTER");
				if(StringUtils.isBlank(semester)) {
					log.warn("semester value is blank");
					continue;
				}
				course.setSemester(getTrimmedValue(semester));
				String campusCode = rs.getString("CAMPUS_CODE");
				if(StringUtils.isBlank(campusCode)) {
					log.warn("campusCode value is blank");
					continue;
				}
				course.setCampusCode(getTrimmedValue(campusCode));
				String campus = rs.getString("CAMPUS");
				if(StringUtils.isBlank(campus)) {
					log.warn("campus value is blank");
					continue;
				}
				course.setCampus(getTrimmedValue(campus));
				Optional<Date> createDateOpt = Optional.ofNullable(rs.getDate("CREATE_DATE"));
				course.setCreateDate(createDateOpt.isPresent() ? new Date(createDateOpt.get().getTime()) : null);
//				Optional<Date> modDateOpt = Optional.ofNullable(rs.getDate("MOD_DATE"));
//				course.setModifiedDate(modDateOpt.isPresent() ? new Date(modDateOpt.get().getTime()) : null);
//				course.setModifiedDate(getTrimmedValue(rs, "MOD_DATE"));
				courseList.add(course);
			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			closeConnection();
		}
		return courseList;
	}
	
	@Override
	public List<ITSUser> getITSUsersData() {
		List<ITSUser> userList = new ArrayList<ITSUser>();
		String sqlSelectAllUsers = "SELECT * FROM users";
		try {
			PreparedStatement ps = getConnection().prepareStatement(sqlSelectAllUsers);
			ResultSet rs = ps.executeQuery();

			ITSUser user = null;
			while (rs.next()) {
				user = new ITSUser();
//				user.setIAGSTNO(rs.getInt("STUDNO"));
				userList.add(user);
			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			closeConnection();
		}
		return userList;
	}

	@Override
	public List<ITSEnrollment> getITSEnrollmentsData() {
		List<ITSEnrollment> enrollmentList = new ArrayList<ITSEnrollment>();
		String sqlSelectAllEnrollments = "select * from enrolments";
		try {
			PreparedStatement ps = getConnection().prepareStatement(sqlSelectAllEnrollments);
			ResultSet rs = ps.executeQuery();
			ITSEnrollment enrollment = null;
			while (rs.next()) {
				enrollment = new ITSEnrollment();
				int studentNo = rs.getInt("STUDNO");
				enrollment.setIAGSTNO(studentNo);
				String surname = rs.getString("SURNAME");
				if(StringUtils.isBlank(surname)) {
					log.warn("surname value is blank for staffNo: " + studentNo);
					continue;
				}
				enrollment.setSurname(getTrimmedValue(surname));
				String otherNames = rs.getString("OTHER_NAMES");
				if(StringUtils.isBlank(otherNames)) {
					log.warn("otherNames value is blank for staffNo: " + studentNo);
					continue;
				}
				enrollment.setOtherNames(getTrimmedValue(otherNames));				
				String pin = rs.getString("PIN");
				if(StringUtils.isBlank(pin)) {
					log.warn("pin value is blank for staffNo: " + studentNo);
					continue;
				}
				enrollment.setPin(getTrimmedValue(pin));				
				enrollment.setAcadYear(rs.getInt("ACAD_YR"));			
				String semCode = rs.getString("SEM_CODE");
				if(StringUtils.isBlank(semCode)) {
					log.warn("semCode value is blank for staffNo: " + studentNo);
					continue;
				}
				enrollment.setSemCode(getTrimmedValue(semCode));		
				String semester = rs.getString("SEMESTER");
				if(StringUtils.isBlank(semester)) {
					log.warn("semester value is blank for staffNo: " + studentNo);
					continue;
				}
				enrollment.setSemester(getTrimmedValue(semester));	
				String progCode = rs.getString("PROG_CODE");
				if(StringUtils.isBlank(progCode)) {
					log.warn("progCode value is blank for staffNo: " + studentNo);
					continue;
				}
				enrollment.setProgCode(getTrimmedValue(progCode));
				String program = rs.getString("PROGRAM");
				if(StringUtils.isBlank(program)) {
					log.warn("program value is blank for staffNo: " + studentNo);
					continue;
				}
				enrollment.setProgram(getTrimmedValue(program));
				String courseCode = rs.getString("COURSE_CODE");
				if(StringUtils.isBlank(courseCode)) {
					log.warn("courseCode value is blank for staffNo: " + studentNo);
					continue;
				}
				enrollment.setCourseCode(getTrimmedValue(courseCode));
				String course = rs.getString("COURSE");
				if(StringUtils.isBlank(course)) {
					log.warn("course value is blank for staffNo: " + studentNo);
					continue;
				}
				enrollment.setCourse(getTrimmedValue(course));
				String campusCode = rs.getString("CAMPUS_CODE");
				if(StringUtils.isBlank(campusCode)) {
					log.warn("campusCode value is blank for staffNo: " + studentNo);
					continue;
				}
				enrollment.setCampusCode(getTrimmedValue(campusCode));
				String campus = rs.getString("CAMPUS");
				if(StringUtils.isBlank(campus)) {
					log.warn("campus value is blank for staffNo: " + studentNo);
					continue;
				}
				enrollment.setCampus(getTrimmedValue(campus));
				Optional<Date> itsDateCreatedOpt = Optional.ofNullable(rs.getDate("CREATE_DATE"));
				enrollment.setItsDateCreated(
						itsDateCreatedOpt.isPresent() ? new Date(itsDateCreatedOpt.get().getTime()) : null);
				String email = rs.getString("EMAIL");
				if(StringUtils.isBlank(email)) {
					log.warn("email value is blank for staffNo: " + studentNo);
					continue;
				}
				enrollment.setEmail(getTrimmedValue(email));
				enrollment.setPhone(getTrimmedValue(rs, "PHONE"));
				enrollmentList.add(enrollment);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			closeConnection();
		}
		return enrollmentList;
	}

	@Override
	public List<ITSLecturer> getITSLecturersData() {

		List<ITSLecturer> lecturerList = new ArrayList<ITSLecturer>();
		String sqlSelectAllLecturers = "select * from lecturer";
		try {
			PreparedStatement ps = getConnection().prepareStatement(sqlSelectAllLecturers);
			ResultSet rs = ps.executeQuery();

			ITSLecturer lecturer = null;
			while (rs.next()) {
				lecturer = new ITSLecturer();
				int staffNo = rs.getInt("STAFFNO");
				lecturer.setSTAFFNO(staffNo);
				String pin = rs.getString("PIN");
				if(StringUtils.isBlank(pin)) {
					log.warn("pin value is blank for staffNo: " + staffNo);
					continue;
				}
				lecturer.setPin(getTrimmedValue(pin));
				String surname = rs.getString("SURNAME");
				if(StringUtils.isBlank(surname)) {
					log.warn("surname value is blank for staffNo: " + staffNo);
					continue;
				}
				lecturer.setSurname(getTrimmedValue(surname));
				String otherNames = rs.getString("OTHERNAMES");
				if(StringUtils.isBlank(otherNames)) {
					log.warn("otherNames value is blank for staffNo: " + staffNo);
					continue;
				}
				lecturer.setOtherNames(getTrimmedValue(otherNames));
				String dept = rs.getString("DEPT");
				if(StringUtils.isBlank(dept)) {
					log.warn("dept value is blank for staffNo: " + staffNo);
					continue;
				}
				lecturer.setDept(getTrimmedValue(dept));
				lecturer.setTel(getTrimmedValue(rs, "TEL"));
				String email = rs.getString("EMAIL");
				if(StringUtils.isBlank(email)) {
					log.warn("email value is blank for staffNo: " + staffNo);
					continue;
				}
				lecturer.setEmail(getTrimmedValue(email));
				String course = rs.getString("COURSE");
				if(StringUtils.isBlank(course)) {
					log.warn("course value is blank for staffNo: " + staffNo);
					continue;
				}
				lecturer.setCourse(getTrimmedValue(course));
				String offeringType = rs.getString("OFFERING TYPE");
				if(StringUtils.isBlank(offeringType)) {
					log.warn("offeringType value is blank for staffNo: " + staffNo);
					continue;
				}
				String campusCode = getTrimmedValue(offeringType);
				lecturer.setOfferingType(campusCode.startsWith("0") ? campusCode : "0" + campusCode);
				lecturer.setAcadYear(rs.getInt("ACAD_YEAR"));
				String semester = rs.getString("SEMESTER");
				if(StringUtils.isBlank(semester)) {
					log.warn("semester value is blank for staffNo: " + staffNo);
					continue;
				}
				lecturer.setSemester(getTrimmedValue(semester));
				lecturerList.add(lecturer);
			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			closeConnection();
		}
		return lecturerList;
	}
	
	private String getTrimmedValue(ResultSet rs, String columnName) {
		try {
			return rs.getString(columnName) != null ? rs.getString(columnName).trim() : null;
		} catch (SQLException e) {
			log.error("Column : " + columnName + " exception: " + e.getMessage(), e);
		}
		return null;
	}
	
	private String getTrimmedValue(String columnValue) {
		return columnValue != null ? columnValue.trim() : null;
	}

	public ServerConfigurationService getServerConfigurationService() {
		return serverConfigurationService;
	}

	public void setServerConfigurationService(ServerConfigurationService serverConfigurationService) {
		this.serverConfigurationService = serverConfigurationService;
	}

	private Connection getConnection() throws SQLException {
		// Class.forName("oracle.jdbc.OracleDriver");
		if (connection == null || connection.isClosed()) {
			connection = DriverManager.getConnection(connectionUrl, username, password);
		}
		return connection;
	}

	private void closeConnection() {
		try {
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		} catch (SQLException ex) {
			log.error(ex.getMessage(), ex);
		}
	}
}
