package hibernate.tables;

import java.util.List;
import javax.persistence.*;

import org.hibernate.Session;
import org.hibernate.type.IntegerType;

import hibernate.util.HibernateUtil;

@Entity
@Table(name = "Library")
public class Library extends DBTable{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Integer id;
	
	@Column(name = "city", nullable = false, length = 20)
	private String city;
	
	@Column(name = "name", nullable = false, length = 30)
	private String name;
	
	@Transient
	private Integer bookCount = null;
	@Transient
	private Integer unreturnedBookCount = null;
	@Transient
	private Librarian librarian = null;
	
	public Library() {};
	public Library(Integer id, String city, String name) {
		this.id = id;
		this.city = city;
		this.name = name;
	}
	public Library(Integer id) {
		this(id, HibernateUtil.getSessionFactory().getCurrentSession());
	}
	public Library(Integer id, Session session) {
		boolean transactionIsActive = session.getTransaction().isActive();
		if(!transactionIsActive) {
			session.getTransaction().begin();
		}
		Library tmp = session.get(Library.class, id);
		if(tmp != null) {
			this.id = tmp.id;
			this.city = tmp.city;
			this.name = tmp.name;
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
		
		Librarian librarian = getLibrarian();
		if(librarian != null) {
			librarian.deleteMe(session);
		}
		List<Book> books = getBooks();
		for(Book book: books) {
			book.deleteMe(session);
		}
		session.createSQLQuery("DELETE FROM Library WHERE id = " + id).executeUpdate();
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
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	
	public Librarian getLibrarian() {
		return getLibrarian(HibernateUtil.getSessionFactory().getCurrentSession());
	}
	public Librarian getLibrarian(Session session) {
		if(librarian == null) {
			librarian = refreshLibrarian(session);
		}
		return librarian;
	}
	public Librarian refreshLibrarian() {
		return refreshLibrarian(HibernateUtil.getSessionFactory().getCurrentSession());
	}
	public Librarian refreshLibrarian(Session session) {
		librarian = null;
		if(id == null || id <= 0) {
			return librarian;
		}
		boolean transactionIsActive = session.getTransaction().isActive();
		if(!transactionIsActive) {
			session.getTransaction().begin();
		}
		List<Librarian> list = session.createQuery("FROM Librarian WHERE library_id = " + id, Librarian.class).list();
		if(list != null && list.size() != 0) {
			librarian = list.get(0);
		}
		if(!transactionIsActive) {
			session.getTransaction().commit();
		}
		return librarian;
	}
	
	public List<Book> getBooks(){
		return getBooks(HibernateUtil.getSessionFactory().getCurrentSession());
	}
	public List<Book> getBooks(Session session){
		List<Book> list = null;
		boolean transactionIsActive = session.getTransaction().isActive();
		if(!transactionIsActive) {
			session.getTransaction().begin();
		}
		list = session.createQuery("FROM Book WHERE library_id = " + id, Book.class).list();
		if(!transactionIsActive) {
			session.getTransaction().commit();
		}
		return list;
	}
	
	public Integer getCountOfBooks() {
		return getCountOfBooks(HibernateUtil.getSessionFactory().getCurrentSession());
	}
	public Integer getCountOfBooks(Session session) {
		if(bookCount == null) {
			bookCount = getRefreshedCountOfBooks(session);
		}
		return bookCount;
	}
	public Integer getRefreshedCountOfBooks() {
		return getRefreshedCountOfBooks(HibernateUtil.getSessionFactory().getCurrentSession());
	}
	public Integer getRefreshedCountOfBooks(Session session) {
		if(id == null || id <= 0) {
			return null;
		}
		boolean isActive = session.getTransaction().isActive();
		if(!isActive) {
			session.getTransaction().begin();
		}
		
		bookCount = (Integer)session.createSQLQuery("SELECT SUM(count) AS result FROM Book WHERE library_id = " + id)
				.addScalar("result", new IntegerType()).list().get(0);
		if(bookCount == null) {
			bookCount = 0;
		}
		
		if(!isActive) {
			session.getTransaction().commit();
		}
		return bookCount;
	}
	
	public Integer getCountOfUreturnedBooks() {
		return getCountOfUreturnedBooks(HibernateUtil.getSessionFactory().getCurrentSession());
	}
	public Integer getCountOfUreturnedBooks(Session session) {
		if(unreturnedBookCount == null) {
			unreturnedBookCount = getRefreshedCountOfBooks(session);
		}
		return unreturnedBookCount;
	}
	public Integer getRefreshedCountOfUreturnedBooks() {
		return getRefreshedCountOfBooks(HibernateUtil.getSessionFactory().getCurrentSession());
	}
	public Integer getRefreshedCountOfUreturnedBooks(Session session) {
		boolean isActive = session.getTransaction().isActive();
		if(!isActive) {
			session.getTransaction().begin();
		}
		
		unreturnedBookCount = (Integer)session.createSQLQuery(
				  "SELECT COUNT(book.id) AS result "
				+ "FROM book INNER JOIN operation "
				+ "ON book.id = operation.book_id"
				+ "WHERE book.library_id = " + id + " AND operation.is_returned = 0")
				.addScalar("result", new IntegerType())
				.list().get(0);
		if(unreturnedBookCount == null) {
			unreturnedBookCount = 0;
		}
		
		if(!isActive) {
			session.getTransaction().commit();
		}
		return bookCount;
	}
	
	public List<Book> getUnreturnedBooks(){
		return getUnreturnedBooks(HibernateUtil.getSessionFactory().getCurrentSession());
	}
	public List<Book> getUnreturnedBooks(Session session){
		boolean isActive = session.getTransaction().isActive();
		if(!isActive) {
			session.getTransaction().begin();
		}
		
		List<Book> list = session.createSQLQuery(
				  "SELECT DISTINCT {book.*} "
				+ "FROM {book} INNER JOIN operation "
				+ "ON {book}.id = operation.book_id "
				+ "WHERE {book}.library_id = " + id + " AND operation.is_returned = 0")
				.addEntity("book", Book.class)
				.list();
		
		if(!isActive) {
			session.getTransaction().commit();
		}
		return list;
	}
	
	@Override
	public String toString() {
		return "Library[id = " + id + ", name = " + name + ", city = " + city + "]";
	}
}
