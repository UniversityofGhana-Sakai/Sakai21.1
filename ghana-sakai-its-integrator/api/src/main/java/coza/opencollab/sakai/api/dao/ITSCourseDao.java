package coza.opencollab.sakai.api.dao;

import java.util.List;

import coza.opencollab.sakai.api.model.ITSCourse;

/**
 * DAO class for Ghana ITS courses
 * 
 * @author JC Gillman
 *
 */
public interface ITSCourseDao {

	public ITSCourse updateITSCourse(ITSCourse course);

	public List<ITSCourse> getITSCourses();

	public ITSCourse getITSCourseById(Long id);

	public boolean deleteITSCourse(Long id);

	public List<ITSCourse> getITSCoursesByAcadYear(int year);

	public List<ITSCourse> getITSCoursesForCurrentAndNextAcadYear(int year);
	
	public ITSCourse findCourseForParams(int acadYear, String courseCode, String campusCode, String semCode);
}
