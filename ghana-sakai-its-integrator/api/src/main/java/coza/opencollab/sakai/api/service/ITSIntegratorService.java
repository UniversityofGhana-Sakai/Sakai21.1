package coza.opencollab.sakai.api.service;

/**
 * 
 * ITS Integration Service interface
 * 
 * @author JC Gillman
 *
 */
public interface ITSIntegratorService {	

	public void syncWithITS();
    
    public void updateITSCourses();
    
    public void updateITSEnrollments();

    public void updateITSLecturers();

}
