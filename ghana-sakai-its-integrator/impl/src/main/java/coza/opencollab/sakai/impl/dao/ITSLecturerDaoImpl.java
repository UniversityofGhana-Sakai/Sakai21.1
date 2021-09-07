package coza.opencollab.sakai.impl.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate5.HibernateCallback;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import coza.opencollab.sakai.api.dao.ITSLecturerDao;
import coza.opencollab.sakai.api.model.ITSEnrollment;
import coza.opencollab.sakai.api.model.ITSLecturer;

/**
 * ITS lecturer DAO implementation
 *
 * @author JC Gillman
 * 
 */
public class ITSLecturerDaoImpl extends HibernateDaoSupport implements ITSLecturerDao {

	@Override
	public ITSLecturer updateITSLecturer(ITSLecturer lecturer) {
		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
		session.saveOrUpdate(lecturer);
		session.flush();
		return lecturer;
	}

	@Override
	public ITSLecturer getITSLecturerById(Long id) {
		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
		Query q = session.createQuery("from ITSLecturer ITSL where ITSL.id=:id");
		q.setParameter("id", id);
		return (ITSLecturer) q.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ITSLecturer> getITSLecturers() {
		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
		Query q = session.createQuery("from ITSLecturer ITSL");
		return (List<ITSLecturer>) q.list();
	}

	@Override
	public boolean deleteITSLecturer(Long id) {
		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
		Query q = session.createQuery("delete from ITSLecturer ITSL where ITSL.id=:id");
		q.setParameter("id", id);
		return q.executeUpdate() > 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ITSLecturer> getITSLecturersByAcadYearOrderBySakaiSiteId(int year) {

		List<ITSLecturer> lecturers = new ArrayList<>();

		HibernateCallback<List<ITSLecturer>> hcb = session -> {
			Query q = session.getNamedQuery("FindLecturersByAcadYearOrderBySakaiSiteId");
			q.setParameter("acadYear", year);
			return q.list();
		};

		lecturers = getHibernateTemplate().execute(hcb);

		return lecturers;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ITSLecturer> getITSLecturersByAcadYear(int year) {

		List<ITSLecturer> lecturers = new ArrayList<>();

		HibernateCallback<List<ITSLecturer>> hcb = session -> {
			Query q = session.getNamedQuery("FindLecturersByAcadYear");
			q.setParameter("acadYear", year);
			return q.list();
		};

		lecturers = getHibernateTemplate().execute(hcb);

		return lecturers;
	}

	@Override
	public List<ITSLecturer> getITSLecturersForCurrentAndNextAcadYear(int year) {

		List<ITSLecturer> lecturers = new ArrayList<>();

		HibernateCallback<List<ITSLecturer>> hcb = session -> {
			Query q = session.getNamedQuery("FindLecturersForCurrentAndNextAcadYear");
			q.setParameter("acadYear", year);
			q.setParameter("acadYearNext", year + 1);
			return q.list();
		};

		lecturers = getHibernateTemplate().execute(hcb);

		return lecturers;
	}
}
