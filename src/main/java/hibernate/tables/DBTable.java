package hibernate.tables;

import org.hibernate.Session;

import hibernate.util.HibernateUtil;

public abstract class DBTable {
	public void deleteMe() {
		deleteMe(HibernateUtil.getSessionFactory().getCurrentSession());
	}
	public void deleteMe(Session session) {
		boolean transactionIsActive = session.getTransaction().isActive();
		if(!transactionIsActive) {
			session.getTransaction().begin();
		}
		
		session.delete(this);
		
		if(!transactionIsActive) {
			session.getTransaction().commit();
		}
	}
	public void updateMe() {
		updateMe(HibernateUtil.getSessionFactory().getCurrentSession());
	}
	public void updateMe(Session session) {
		boolean transactionIsActive = session.getTransaction().isActive();
		if(!transactionIsActive) {
			session.getTransaction().begin();
		}
		
		session.saveOrUpdate(this);
		
		if(!transactionIsActive) {
			session.getTransaction().commit();
		}
	}
}
