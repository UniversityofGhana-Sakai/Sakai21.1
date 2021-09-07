package coza.opencollab.sakai.api.dao;

import java.util.List;

import coza.opencollab.sakai.api.model.ITSCourse;
import coza.opencollab.sakai.api.model.ITSLecturer;
/**
 * 
 * DAO class for Ghana ITS lecturers
 * 
 * @author JC Gillman
 *
 */
public interface ITSLecturerDao {

    public ITSLecturer updateITSLecturer(ITSLecturer lecturer);
    
    public List<ITSLecturer> getITSLecturers();
    
    public ITSLecturer getITSLecturerById(Long id);

    public boolean deleteITSLecturer(Long id);

	public List<ITSLecturer> getITSLecturersByAcadYearOrderBySakaiSiteId(int year);

	public List<ITSLecturer> getITSLecturersByAcadYear(int year);

	public List<ITSLecturer> getITSLecturersForCurrentAndNextAcadYear(int year);
}
