package coza.opencollab.sakai.impl.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate5.HibernateCallback;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import coza.opencollab.sakai.api.dao.ITSLecturerDao;
import coza.opencollab.sakai.api.dao.ITSUserDao;
import coza.opencollab.sakai.api.model.ITSEnrollment;
import coza.opencollab.sakai.api.model.ITSLecturer;
import coza.opencollab.sakai.api.model.ITSUser;

/**
 * ITS User DAO implementation
 *
 * @author JC Gillman
 * 
 */
public class ITSUserDaoImpl extends HibernateDaoSupport implements ITSUserDao {

	@Override
	public ITSUser updateITSUser(ITSUser user) {
		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
		session.saveOrUpdate(user);
		session.flush();
		return user;
	}

	@Override
	public ITSUser getITSUserById(Long id) {
		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
		Query q = session.createQuery("from ITSUser ITSU where ITSU.id=:id");
		q.setParameter("id", id);
		return (ITSUser) q.uniqueResult();
	}

	@Override
	public ITSUser getITSUserByUserId(Long userId) {
		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
		Query q = session.createQuery("from ITSUser ITSU where ITSU.userId = :userId");
		q.setParameter("userId", userId);
		return (ITSUser) q.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ITSUser> getITSUsers() {
		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
		Query q = session.createQuery("from ITSUser ITSU");
		return (List<ITSUser>) q.list();
	}

	@Override
	public boolean deleteITSUser(Long id) {
		Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
		Query q = session.createQuery("delete from ITSUser ITSU where ITSU.id=:id");
		q.setParameter("id", id);
		return q.executeUpdate() > 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ITSUser> getITSUsersByAcadYear(int year) {

		List<ITSUser> users = new ArrayList<>();

		HibernateCallback<List<ITSUser>> hcb = session -> {
			Query q = session.getNamedQuery("FindUsersByAcadYear");
			q.setParameter("acadYear", year);
			return q.list();
		};

		users = getHibernateTemplate().execute(hcb);

		return users;
	}
}
