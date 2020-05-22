package hibernate.tables;

import java.util.List;
import java.util.Set;
import javax.persistence.*;

import org.hibernate.Hibernate;
import org.hibernate.Session;

import hibernate.util.HibernateUtil;

@Entity
@Table(name = "Author")
public class Author extends DBTable{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Integer id;
	
	@Column(name = "firstName", nullable = false, length = 20)
	private String firstName;
	
	@Column(name = "lastName", nullable = false, length = 20)
	private String lastName;
	
	@Transient
	private Integer booksCount = null; 
	
	@ManyToMany()
	@JoinTable(name = "Author_Book",
		joinColumns = {@JoinColumn(name = "author_id")},
		inverseJoinColumns = {@JoinColumn(name = "book_id")})
	private Set<Book> books;
	
	public Author() {}
	public Author(Integer id, String firstName, String lastName, Set<Book> books) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.books = books;
	}
	public Author(Integer id) {
		this(id, HibernateUtil.getSessionFactory().getCurrentSession());
	}
	public Author(Integer id, Session session) {
		boolean transactionIsActive = session.getTransaction().isActive();
		if(!transactionIsActive) {
			session.getTransaction().begin();
		}
		Author tmp = session.get(Author.class, id);
		if(tmp != null) {
			this.id = tmp.id;
			this.firstName = tmp.firstName;
			this.lastName = tmp.lastName;
			this.books = tmp.books;
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

		session.createSQLQuery("DELETE FROM author_book WHERE author_id = " + id).executeUpdate();
		session.createSQLQuery("DELETE FROM Author WHERE id = " + id).executeUpdate();
		
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
	public Set<Book> getBooks() {
		return books;
	}
	public void setBooks(Set<Book> books) {
		this.books = books;
	}
	
	public List<Book> getBooks(Session session){
		List<Book> list = null;
		boolean transactionIsActive = session.getTransaction().isActive();
		if(!transactionIsActive) {
			session.getTransaction().begin();
		}
		list = session.createSQLQuery(
    			  "SELECT {book.*} "
    			+ "FROM {book} "
    			+ "INNER JOIN author_book as AB0 "
    			+ "ON {book}.id = AB0.book_id "
    			+ "WHERE AB0.author_id = " + id)
    			.addEntity("book", Book.class).list();
		if(!transactionIsActive) {
			session.getTransaction().commit();
		}
		return list;
	}
	
	public Integer getBooksCount() {
		return getBooksCount(HibernateUtil.getSessionFactory().getCurrentSession());
	}
	public Integer getBooksCount(Session session) {
		if(booksCount == null) {
			return getRefreshedBooksCount(session);
		}
		return booksCount;
	}
	public Integer getRefreshedBooksCount() {
		return getRefreshedBooksCount(HibernateUtil.getSessionFactory().getCurrentSession());
	}
	public Integer getRefreshedBooksCount(Session session) {
		boolean transactionIsActive = session.getTransaction().isActive();
		if(!transactionIsActive) {
			session.getTransaction().begin();
		}
		booksCount = getBooks(session).size();
		if(!transactionIsActive) {
			session.getTransaction().commit();
		}
		return booksCount;
	}
	
	public void addBook(Book book) {
		addBook(book, HibernateUtil.getSessionFactory().getCurrentSession());
	}
	public void addBook(Book book, Session session) {
		boolean transactionIsActive = session.getTransaction().isActive();
		if(!transactionIsActive) {
			session.getTransaction().begin();
		}
		session.createSQLQuery("INSERT IGNORE INTO author_book VALUES (" + id + ", " + book.getId() + ")").executeUpdate();
		if(!transactionIsActive) {
			session.getTransaction().commit();
		}
	}
	
	public void removeBook(Book book) {
		removeBook(book, HibernateUtil.getSessionFactory().getCurrentSession());
	}
	public void removeBook(Book book, Session session) {
		boolean transactionIsActive = session.getTransaction().isActive();
		if(!transactionIsActive) {
			session.getTransaction().begin();
		}
		
		session.createSQLQuery("DELETE FROM author_book WHERE author_id = " + id + " AND book_id = " + book.getId()).executeUpdate();
		
		if(!transactionIsActive) {
			session.getTransaction().commit();
		}
	}
	
	@Override
	public String toString() {
		return "Author[id = " + id + ", FN = " + firstName + ", LN = " + lastName + "]";
	}
}
