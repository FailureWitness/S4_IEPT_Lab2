package hibernate.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import hibernate.tables.Author;
import hibernate.tables.Book;
import hibernate.tables.Librarian;
import hibernate.tables.Library;
import hibernate.tables.Operation;

public class HibernateUtil {
	private static StandardServiceRegistry registry;
    private static SessionFactory sessionFactory;
    
    public static SessionFactory getSessionFactory() {
    	if(sessionFactory == null) {
    		registry = new StandardServiceRegistryBuilder().configure().build();
    		MetadataSources source = new MetadataSources(registry);
    		Metadata metadata = source.getMetadataBuilder().build();
    		sessionFactory = metadata.getSessionFactoryBuilder().build();
    	}
    	return sessionFactory;
    }
    public static void shutdown() {
    	if(registry != null) {
    		StandardServiceRegistryBuilder.destroy(registry);
    	}
    }

    // ------------------- Hibernate Test ---------------------------
    public static void createLib() {
    	Session s = getSessionFactory().getCurrentSession();
    	s.getTransaction().begin();
    	Library lib = new Library(null, "Kyiv", "Kyiv-lib");
    	Book b1 = new Book(null, "BOOK_1", 1, lib, null);
    	Book b2 = new Book(null, "BOOK_2", 1, lib, null);
    	
    	Author a1 = new Author(null, "FN_1", "LN_1", null);
    	Author a2 = new Author(null, "FN_2", "LN_2", null);
    	
    	s.saveOrUpdate(lib);
    	s.saveOrUpdate(b1);
    	s.saveOrUpdate(b2);
    	s.saveOrUpdate(a1);
    	s.saveOrUpdate(a2);
    	s.getTransaction().commit();
    }
    public static void generateDependencisAllToALL() {
    	Session s = getSessionFactory().getCurrentSession();
    	s.getTransaction().begin();
    	
    	Set<Book> books = new HashSet<Book>(s.createQuery("from Book", Book.class).list());
    	List<Author> author = s.createQuery("from Author", Author.class).list();
    	
    	for(int i = 0; i < author.size(); i++) {
    		author.get(i).setBooks(books);
    		s.saveOrUpdate(author.get(i));
    	}
    	
    	s.getTransaction().commit();
    }
    public static void main(String[] args) {
    	Session session = getSessionFactory().getCurrentSession();
    	session.getTransaction().begin();
    	
    	Library lib = new Library(1, session);
    	lib.setName("TEST HIBERNATE");
    	lib.updateMe(session);
    	
    	session.getTransaction().commit();
    }
}
