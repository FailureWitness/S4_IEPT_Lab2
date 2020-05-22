package hibernate.tables;

import javax.persistence.*;

import org.hibernate.Session;

import hibernate.util.HibernateUtil;

@Entity
@Table(name = "Operation")
public class Operation extends DBTable{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Integer id;
	
	@ManyToOne()
	@JoinColumn(name = "customer_id", referencedColumnName = "id", nullable = false)
	private Customer customer;
	
	@ManyToOne()
	@JoinColumn(name = "book_id", referencedColumnName = "id", nullable = false)
	private Book book;
	
	@Column(name = "date", nullable = false)
	private Long date; // time since epoch
	
	@Column(name = "is_returned", nullable = false)
	private Boolean isReturned;
	
	public Operation() {}
	public Operation(Integer id, Customer customer, Book book, Long date, Boolean isReturned) {
		this.id = id;
		this.customer = customer;
		this.book = book;
		this.date = date;
		this.isReturned = isReturned;
	}
	public Operation(Integer id) {
		this(id, HibernateUtil.getSessionFactory().getCurrentSession());
	}
	public Operation(Integer id, Session session) {
		boolean transactionIsActive = session.getTransaction().isActive();
		if(!transactionIsActive) {
			session.getTransaction().begin();
		}
		Operation tmp = session.get(Operation.class, id);
		if(tmp != null) {
			this.id = tmp.id;
			this.customer = tmp.customer;
			this.book = tmp.book;
			this.date = tmp.date;
			this.isReturned = tmp.isReturned;
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

		customer = null;
		book = null;
		session.createSQLQuery("DELETE FROM Operation WHERE id = " + id).executeUpdate();
		
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
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public Book getBook() {
		return book;
	}
	public void setBook(Book book) {
		this.book = book;
	}
	public Long getDate() {
		return date;
	}
	public void setDate(Long date) {
		this.date = date;
	}
	public Boolean isReturned() {
		return isReturned;
	}
	public void setIsReturned(Boolean isReturned) {
		this.isReturned = isReturned;
	}
	
	@Override
	public String toString() {
		return "Operation[id = " + id + ", customer = " + customer + ", book = " + book + ", date = " + date + ", isReturned" + isReturned + "]";
	}
}
