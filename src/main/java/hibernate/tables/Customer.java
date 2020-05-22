package hibernate.tables;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import org.hibernate.Session;
import org.hibernate.type.IntegerType;

import hibernate.util.HibernateUtil;

@Entity
@Table(name = "Customer")
public class Customer extends DBTable{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Integer id;
	
	@Column(name = "firstName", nullable = false, length = 20)
	private String firstName;
	
	@Column(name = "lastName", nullable = false, length = 20)
	private String lastName;
	
	@Transient
	private Integer unreturnedBooksCount = null;
	
	public Customer() {}
	public Customer(Integer id, String firstName, String lastName) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
	}
	public Customer(Integer id) {
		this(id, HibernateUtil.getSessionFactory().getCurrentSession());
	}
	public Customer(Integer id, Session session) {
		boolean transactionIsActive = session.getTransaction().isActive();
		if(!transactionIsActive) {
			session.getTransaction().begin();
		}
		Customer tmp = session.get(Customer.class, id);
		if(tmp != null) {
			this.id = tmp.id;
			this.firstName = tmp.firstName;
			this.lastName = tmp.lastName;
		}
		if(!transactionIsActive) {
			session.getTransaction().commit();
		}
	}
	@Override
	public void deleteMe(Session session) {
		boolean transactionIsActive = session.getTransaction().isActive();
		if(!transactionIsActive) {
			session.getTransaction().begin();
		}
		
		session.createSQLQuery("DELETE FROM Operation WHERE customer_id = " + id).executeUpdate();
		session.createSQLQuery("DELETE FROM Customer WHERE id = " + id).executeUpdate();
		
		if(!transactionIsActive) {
			session.clear();
			session.getTransaction().commit();
		}
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public List<Operation> getOperations(){
		return getOperations(HibernateUtil.getSessionFactory().getCurrentSession());
	}
	public List<Operation> getOperations(Session session){
		List<Operation> list = null;
		boolean transactionIsActive = session.getTransaction().isActive();
		if(!transactionIsActive) {
			session.getTransaction().begin();
		}
		list = session.createQuery("FROM Operation WHERE customer_id = " + id, Operation.class).list();
		if(!transactionIsActive) {
			session.getTransaction().commit();
		}
		return list;
	}
	
	public Integer getUnreturnedBooksCount() {
		return getUnreturnedBooksCount(HibernateUtil.getSessionFactory().getCurrentSession());
	}
	public Integer getUnreturnedBooksCount(Session session) {
		if(unreturnedBooksCount == null) {
			return getRefreshedUnreturnedBooksCount(session);
		}
		return unreturnedBooksCount;
	}
	public Integer getRefreshedUnreturnedBooksCount(){
		return getRefreshedUnreturnedBooksCount(HibernateUtil.getSessionFactory().getCurrentSession());
	}
	public Integer getRefreshedUnreturnedBooksCount(Session session){
		unreturnedBooksCount = getUnreturnedBooks(session).size();
		return unreturnedBooksCount;
	}
	
	public List<Book> getBooks(){
		return getBooks(HibernateUtil.getSessionFactory().getCurrentSession());
	}
	public List<Book> getBooks(Session session){
		boolean transactionIsActive = session.getTransaction().isActive();
		if(!transactionIsActive) {
			session.getTransaction().begin();
		}
		List<Book> books = session.createSQLQuery(
				  "SELECT DISTINCT {book.*} "
				+ "FROM {book} INNER JOIN operation "
				+ "ON {book}.id = operation.book_id "
				+ "WHERE operation.customer_id = " + id)
//				.addEntity("operation", Operation.class)
				.addEntity("book", Book.class)
				.list();
		
		if(!transactionIsActive) {
			session.getTransaction().commit();
		}
		return books;
	}
	
	public List<Book> getUnreturnedBooks(){
		return getUnreturnedBooks(HibernateUtil.getSessionFactory().getCurrentSession());
	}
	public List<Book> getUnreturnedBooks(Session session){
		boolean transactionIsActive = session.getTransaction().isActive();
		if(!transactionIsActive) {
			session.getTransaction().begin();
		}
		List<Book> books = session.createSQLQuery(
				  "SELECT DISTINCT {book.*} "
				+ "FROM {book} INNER JOIN operation "
				+ "ON {book}.id = operation.book_id "
				+ "WHERE operation.is_returned = 0 AND operation.customer_id = " + id)
				.addEntity("book", Book.class)
//				.addEntity("operation", Operation.class)
				.list();
		
		if(!transactionIsActive) {
			session.getTransaction().commit();
		}
		return books;
	}
	
	@Override
	public String toString() {
		return "Customer[id = " + id + ", FN = " + firstName + ", LN = " + lastName + "]";
	}
}
