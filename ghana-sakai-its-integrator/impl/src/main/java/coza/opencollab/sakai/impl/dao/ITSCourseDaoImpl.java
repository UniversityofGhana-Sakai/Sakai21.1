package coza.opencollab.sakai.impl.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate5.HibernateCallback;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import coza.opencollab.sakai.api.dao.ITSCourseDao;
import coza.opencollab.sakai.api.model.ITSCourse;

/**
 * ITS course DAO implementation
 *
 * @author JC Gillman
 * 
 */
public class ITSCourseDaoImpl extends HibernateDaoSupport implements ITSCourseDao {

	@Override
	public ITSCourse updateITSCourse(ITSCourse course) {
		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
		if (course.getId() == null) {
			course.setCreateDate(new Date());
		} else {
			course.setModifiedDate(new Date());
		}
		session.saveOrUpdate(course);
		session.flush();
		return course;
	}

	@Override
	public ITSCourse getITSCourseById(Long id) {
		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
		Query q = session.createQuery("from ITSCourse ITSC where ITSC.id=:id");
		q.setParameter("id", id);
		return (ITSCourse) q.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ITSCourse> getITSCourses() {
		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
		Query q = session.createQuery("from ITSCourse ITSC");
		return (List<ITSCourse>) q.list();
	}

	@Override
	public boolean deleteITSCourse(Long id) {
		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
		Query q = session.createQuery("delete from ITSCourse ITSC where ITSC.id=:id");
		q.setParameter("id", id);
		return q.executeUpdate() > 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ITSCourse> getITSCoursesByAcadYear(int year) {

		List<ITSCourse> courses = new ArrayList<>();

		HibernateCallback<List<ITSCourse>> hcb = session -> {
			Query q = session.getNamedQuery("FindCoursesByAcadYear");
			q.setParameter("acadYear", year);
			return q.list();
		};

		courses = getHibernateTemplate().execute(hcb);

		return courses;
	}

	@Override
	public ITSCourse findCourseForParams(int acadYear, String courseCode, String campusCode, String semCode) {
		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
		Query q = session.createQuery(
				"from ITSCourse ITSC where ITSC.acadYear = :acadYear and ITSC.courseCode = :courseCode and ITSC.campusCode = :campusCode and ITSC.semCode = :semCode");
		q.setParameter("acadYear", acadYear);
		q.setParameter("courseCode", courseCode);
		q.setParameter("campusCode", campusCode);
		q.setParameter("semCode", semCode);
		return (ITSCourse) q.uniqueResult();
	}

	@Override
	public List<ITSCourse> getITSCoursesForCurrentAndNextAcadYear(int year) {

		List<ITSCourse> courses = new ArrayList<>();

		HibernateCallback<List<ITSCourse>> hcb = session -> {
			Query q = session.getNamedQuery("FindCoursesForCurrentAndNextAcadYear");
			q.setParameter("acadYear", year);
			q.setParameter("acadYearNext", year + 1);
			return q.list();
		};

		courses = getHibernateTemplate().execute(hcb);

		return courses;
	}
}
