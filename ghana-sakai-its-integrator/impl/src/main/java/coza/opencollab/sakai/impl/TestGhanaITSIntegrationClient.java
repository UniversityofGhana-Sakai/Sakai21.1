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

import org.sakaiproject.component.api.ServerConfigurationService;

import coza.opencollab.sakai.api.model.ITSCourse;
import coza.opencollab.sakai.api.model.ITSEnrollment;
import coza.opencollab.sakai.api.model.ITSLecturer;
import coza.opencollab.sakai.api.model.ITSUser;
import coza.opencollab.sakai.cm.SISClient;
import lombok.extern.slf4j.Slf4j;

/**
 * TEST: Ghana ITS Integration Client, using MySQL tables with dummy data for
 * local testing
 *
 * @author JC Gillman
 */
@Slf4j
public class TestGhanaITSIntegrationClient implements SISClient {

	private ServerConfigurationService serverConfigurationService;

	private String connectionUrl = null;
	private String username = null;
	private String password = null;

	private Connection connection = null;

	public TestGhanaITSIntegrationClient() {
	}

	public void init() {
		connectionUrl = serverConfigurationService.getString("its.client.integration.mysql.url",
				"mysql://127.0.0.1:3306/ghana193?useUnicode=true&characterEncoding=UTF-8");
		username = serverConfigurationService.getString("its.client.integration.mysql.username", "sakai");
		password = serverConfigurationService.getString("its.client.integration.mysql.password", "ironchef");
	}

	@Override
	public List<ITSCourse> getITSCoursesData() {
		List<ITSCourse> courseList = new ArrayList<ITSCourse>();
		String sqlSelectAllCourses = "SELECT * FROM its_courses_its";
		try {
			PreparedStatement ps = getConnection().prepareStatement(sqlSelectAllCourses);
			ResultSet rs = ps.executeQuery();

			ITSCourse course = null;
			while (rs.next()) {
				course = new ITSCourse();
				course.setAcadYear(rs.getInt("ACAD_YR"));
				course.setCourseCode(getTrimmedValue(rs, "COURSE_CODE"));
				course.setCourse(getTrimmedValue(rs, "COURSE"));
				course.setSemCode(rs.getString("SEM_CODE"));
				course.setSemester(getTrimmedValue(rs, "SEMESTER"));
				course.setCampusCode(getTrimmedValue(rs, "CAMPUS_CODE"));
				course.setCampus(getTrimmedValue(rs, "CAMPUS"));
				Optional<Date> createDateOpt = Optional.ofNullable(rs.getDate("CREATE_DATE"));
				course.setCreateDate(createDateOpt.isPresent() ? new Date(createDateOpt.get().getTime()) : null);
				Optional<Date> modDateOpt = Optional.ofNullable(rs.getDate("MODIFIED_DATE"));
				course.setModifiedDate(modDateOpt.isPresent() ? new Date(modDateOpt.get().getTime()) : null);
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
		String sqlSelectAllUsers = "SELECT * FROM its_users_its";
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
		String sqlSelectAllEnrollments = "SELECT * FROM its_enrollments_its";
		try {
			PreparedStatement ps = getConnection().prepareStatement(sqlSelectAllEnrollments);
			ResultSet rs = ps.executeQuery();

			ITSEnrollment enrollment = null;
			while (rs.next()) {
				enrollment = new ITSEnrollment();
				enrollment.setIAGSTNO(rs.getInt("STUDNO"));
				enrollment.setSurname(getTrimmedValue(rs, "SURNAME"));
				enrollment.setOtherNames(getTrimmedValue(rs, "OTHER_NAMES"));
				enrollment.setPin(getTrimmedValue(rs, "PIN"));
				enrollment.setAcadYear(rs.getInt("ACAD_YR"));
				enrollment.setSemCode(getTrimmedValue(rs, "SEM_CODE"));
				enrollment.setSemester(getTrimmedValue(rs, "SEMESTER"));
				enrollment.setProgCode(getTrimmedValue(rs, "PROG_CODE"));
				enrollment.setProgram(getTrimmedValue(rs, "PROGRAM"));
				enrollment.setCourseCode(getTrimmedValue(rs, "COURSE_CODE"));
				enrollment.setCourse(getTrimmedValue(rs, "COURSE"));
				enrollment.setCampusCode(getTrimmedValue(rs, "CAMPUS_CODE"));
				enrollment.setCampus(getTrimmedValue(rs, "CAMPUS"));
				Optional<Date> itsDateCreatedOpt = Optional.ofNullable(rs.getDate("CREATE_DATE"));
				enrollment.setItsDateCreated(
						itsDateCreatedOpt.isPresent() ? new Date(itsDateCreatedOpt.get().getTime()) : null);
				enrollment.setEmail(getTrimmedValue(rs, "EMAIL"));
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
		String sqlSelectAllLecturers = "SELECT * FROM its_lecturers_its";
		try {
			PreparedStatement ps = getConnection().prepareStatement(sqlSelectAllLecturers);
			ResultSet rs = ps.executeQuery();

			ITSLecturer lecturer = null;
			while (rs.next()) {
				lecturer = new ITSLecturer();
				lecturer.setSTAFFNO(rs.getInt("STAFFNO"));
				lecturer.setPin(getTrimmedValue(rs, "PIN"));
				lecturer.setSurname(getTrimmedValue(rs, "SURNAME"));
				lecturer.setOtherNames(getTrimmedValue(rs, "OTHERNAMES"));
				lecturer.setDept(getTrimmedValue(rs, "DEPT"));
				lecturer.setTel(getTrimmedValue(rs, "TEL"));
				lecturer.setEmail(getTrimmedValue(rs, "EMAIL"));
				lecturer.setCourse(getTrimmedValue(rs, "COURSE"));
				String campusCode = getTrimmedValue(rs, "OFFERING_TYPE");
				lecturer.setOfferingType(campusCode.startsWith("0") ? campusCode : "0" + campusCode);
				lecturer.setAcadYear(rs.getInt("ACAD_YEAR"));
				lecturer.setSemester(getTrimmedValue(rs, "SEMESTER"));
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
