package coza.opencollab.sakai.cm;

import java.util.List;

import coza.opencollab.sakai.api.model.ITSCourse;
import coza.opencollab.sakai.api.model.ITSEnrollment;
import coza.opencollab.sakai.api.model.ITSLecturer;
import coza.opencollab.sakai.api.model.ITSUser;

/**
 * 
 * ITS Client integration interface
 * 
 * @author JC Gillman
 *
 */
public interface SISClient {
	
	public List <ITSCourse> getITSCoursesData();
	
	public List <ITSUser> getITSUsersData();

	public List <ITSEnrollment> getITSEnrollmentsData();

	public List <ITSLecturer> getITSLecturersData();
}
