package coza.opencollab.sakai.impl.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate5.HibernateCallback;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import coza.opencollab.sakai.api.dao.ITSEnrollmentDao;
import coza.opencollab.sakai.api.model.ITSEnrollment;

/**
 * ITS enrollment DAO implementation
 *
 * @author JC Gillman
 * 
 */
public class ITSEnrollmentDaoImpl extends HibernateDaoSupport implements ITSEnrollmentDao {

	@Override
	public ITSEnrollment updateITSEnrollment(ITSEnrollment enrollment) {
		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
		if (enrollment.getId() == null) {
			enrollment.setCreatedDate(new Date());
		}
		session.saveOrUpdate(enrollment);
		session.flush();
		return enrollment;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ITSEnrollment> getITSEnrollmentsByAcadYear(int year) {

		List<ITSEnrollment> enrollments = new ArrayList<>();

		HibernateCallback<List<ITSEnrollment>> hcb = session -> {
			Query q = session.getNamedQuery("FindEnrollmentsByAcadYear");
			q.setParameter("acadYear", year);
			return q.list();
		};

		enrollments = getHibernateTemplate().execute(hcb);

		return enrollments;
	}

	@Override
	public ITSEnrollment getITSEnrollmentById(Long id) {
		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
		Query q = session.createQuery("from ITSEnrollment ITSE where ITSE.id=:id");
		q.setParameter("id", id);
		return (ITSEnrollment) q.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ITSEnrollment> getITSEnrollments() {
		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
		Query q = session.createQuery("from ITSEnrollment ITSE");
		return (List<ITSEnrollment>) q.list();
	}

	@Override
	public boolean deleteITSEnrollment(Long id) {
		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
		Query q = session.createQuery("delete from ITSEnrollment ITSE where ITSE.id=:id");
		q.setParameter("id", id);
		return q.executeUpdate() > 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ITSEnrollment> getITSEnrollmentsByAcadYearOrderBySakaiSiteId(int year) {

		List<ITSEnrollment> enrollments = new ArrayList<>();

		HibernateCallback<List<ITSEnrollment>> hcb = session -> {
			Query q = session.getNamedQuery("FindEnrollmentsByAcadYearOrderBySakaiSiteId");
			q.setParameter("acadYear", year);
			return q.list();
		};

		enrollments = getHibernateTemplate().execute(hcb);

		return enrollments;
	}

	@Override
	public List<ITSEnrollment> getITSEnrollmentsForCurrentAndNextAcadYear(int year) {

		List<ITSEnrollment> enrollments = new ArrayList<>();

		HibernateCallback<List<ITSEnrollment>> hcb = session -> {
			Query q = session.getNamedQuery("FindEnrollmentsForCurrentAndNextAcadYear");
			q.setParameter("acadYear", year);
			q.setParameter("acadYearNext", year + 1);
			return q.list();
		};

		enrollments = getHibernateTemplate().execute(hcb);

		return enrollments;
	}
}
