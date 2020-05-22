package hibernate.tables;

import javax.persistence.*;

import org.hibernate.Session;

import hibernate.util.HibernateUtil;

@Entity
@Table
public class Librarian extends DBTable{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Integer id;
	
	@Column(name = "firstName", nullable = false, length = 20)
	private String firstName;
	
	@Column(name = "lasttName", nullable = false, length = 20)
	private String lastName;
	
	@OneToOne()
	@JoinColumn(name = "library_id", referencedColumnName = "id", nullable = true)
	private Library library;
	
	public Librarian() {}
	public Librarian(Integer id, String firstName, String lastName, Library library) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.library = library;
	}
	public Librarian(Integer id) {
		this(id, HibernateUtil.getSessionFactory().getCurrentSession());
	}
	public Librarian(Integer id, Session session) {
		boolean transactionIsActive = session.getTransaction().isActive();
		if(!transactionIsActive) {
			session.getTransaction().begin();
		}
		Librarian tmp = session.get(Librarian.class, id);
		if(tmp != null) {
			this.id = tmp.id;
			this.firstName = tmp.firstName;
			this.lastName = tmp.lastName;
			this.library = tmp.library;
		}
		if(!transactionIsActive) {
			session.clear();
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
		session.createSQLQuery("DELETE FROM Librarian WHERE id = " + id).executeUpdate();
		
		if(!transactionIsActive) {
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
	public Library getLibrary() {
		return library;
	}
	public void setLibrary(Library library) {
		this.library = library;
	}
	
	@Override
	public String toString() {
		return "Librarian[id = " + id + ", FN = " + firstName + ", LN = " + lastName + ", library = " + library + "]";
	}
}
