package coza.opencollab.sakai.api.dao;

import java.util.List;

import coza.opencollab.sakai.api.model.ITSEnrollment;

/**
 * 
 * DAO class for Ghana ITS enrollments
 * 
 * @author JC Gillman
 *
 */
public interface ITSEnrollmentDao {

	public ITSEnrollment updateITSEnrollment(ITSEnrollment enrollment);

	public List<ITSEnrollment> getITSEnrollments();

	public List<ITSEnrollment> getITSEnrollmentsByAcadYear(int year);

	public List<ITSEnrollment> getITSEnrollmentsForCurrentAndNextAcadYear(int year);

	public ITSEnrollment getITSEnrollmentById(Long id);

	public boolean deleteITSEnrollment(Long id);

	public List<ITSEnrollment> getITSEnrollmentsByAcadYearOrderBySakaiSiteId(int year);

}
