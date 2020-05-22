package hibernate.tables;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.type.IntegerType;

import hibernate.util.HibernateUtil;

@Entity
@Table(name = "Book")
public class Book extends DBTable{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Integer id;
	
	@Column(name = "name", nullable = false, length = 30)
	private String name;
	
	@Column(name = "count", nullable = false)
	private Integer count = 0;
	
	@Transient
	private Integer unreturnedCount = null;
	
	@ManyToOne()
	@JoinColumn(name = "library_id", referencedColumnName = "id")
	private Library library;
	
	@ManyToMany(mappedBy = "books", fetch = FetchType.EAGER)
	private Set<Author> authors;
	
	public Book() {}
	public Book(Integer id, String name, Integer count, Library library, Set<Author> authors) {
		this.id = id;
		this.name = name;
		this.count = count;
		this.library = library;
		this.authors = authors;
	}
	public Book(Integer id) {
		this(id, HibernateUtil.getSessionFactory().getCurrentSession());
	}
	public Book(Integer id, Session session) {
		boolean transactionIsActive = session.getTransaction().isActive();
		if(!transactionIsActive) {
			session.getTransaction().begin();
		}
		Book tmp = session.get(Book.class, id);
		if(tmp != null) {
			this.id = tmp.id;
			this.name = tmp.name;
			this.count = tmp.count;
			this.library = tmp.library;
			this.authors = tmp.authors;
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
		
		library = null;
		authors = null;
		session.createSQLQuery("DELETE FROM Operation WHERE book_id = " + id).executeUpdate();
		session.createSQLQuery("DELETE FROM author_book WHERE book_id = " + id).executeUpdate();
		session.createSQLQuery("DELETE FROM Book WHERE id = " + id).executeUpdate();
		
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Set<Author> getAuthors() {
		return authors;
	}
	public void setAuthors(Set<Author> authors) {
		this.authors = authors;
	}
	public Library getLibrary() {
		return library;
	}
	public void setLibrary(Library library) {
		this.library = library;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
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
		list = session.createQuery("FROM Operation WHERE book_id = " + id, Operation.class).list();
		if(!transactionIsActive) {
			session.getTransaction().commit();
		}
		return list;
	}
	
	public Integer getUnreturnedCount() {
		return getUnreturnedCount(HibernateUtil.getSessionFactory().getCurrentSession());
	}
	public Integer getUnreturnedCount(Session session) {
		if(unreturnedCount == null) {
			unreturnedCount = getRefreshedUnreturnedCount(session);
		}
		return unreturnedCount;
	}
	public Integer getRefreshedUnreturnedCount() {
		return getRefreshedUnreturnedCount(HibernateUtil.getSessionFactory().getCurrentSession());
	}
	public Integer getRefreshedUnreturnedCount(Session session) {
		boolean transactionIsActive = session.getTransaction().isActive();
		if(!transactionIsActive) {
			session.getTransaction().begin();
		}
		unreturnedCount = (Integer)session
				.createSQLQuery("SELECT DISTINCT COUNT(*) AS result FROM operation WHERE is_returned = 0 and book_id = " + id)
				.addScalar("result", new IntegerType()).list().get(0);
		if(!transactionIsActive) {
			session.getTransaction().commit();
		}
		return unreturnedCount;
	}
	
	@Override
	public String toString() {
		return "Book[id = " + id + ", name = " + name + ", count = " + count + ", library = " + library + "]";
	}
	@Override
	public boolean equals(Object obj) {
		try {
			Book book = (Book)obj;
			return id == book.id;
		} catch(Exception ex) {
			return false;
		}
	}
}
