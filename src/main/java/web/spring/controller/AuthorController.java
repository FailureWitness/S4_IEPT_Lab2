package web.spring.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import hibernate.tables.Author;
import hibernate.tables.Book;
import hibernate.tables.Customer;
import hibernate.util.HibernateUtil;
import web.spring.controller.Constants.Error;
import web.thymeleaf.page.ItemHome;
import web.thymeleaf.page.ListPage;
import web.thymeleaf.templates.basic.ListItem;
import web.thymeleaf.templates.basic.ListItem.CssClass;
import web.thymeleaf.templates.input.Input;
import web.thymeleaf.templates.input.InputButton;
import web.thymeleaf.templates.input.InputSubmit;
import web.thymeleaf.templates.popup.DeletePopup;
import web.thymeleaf.templates.popup.ListPopup;
import web.thymeleaf.templates.popup.UpdateAuthorPopup;
import web.thymeleaf.templates.popup.UpdatePopup;
import web.thymeleaf.templates.input.InputButton.JSID;
import web.thymeleaf.templates.input.InputHidden;

@Controller
public class AuthorController {
	private enum HomeAction{
		Update("update", JSID.AuthorOpen),
		AllBooks("list of books"),
		AddBook("add book"),
		RemoveBook("remove book"),
		Special("special"), // select all customers who got all books by this author
		Delete("delete", JSID.DeleteOpen);
		private String name;
		private Input input;
		
		private HomeAction(String name) {
			this.name = name;
			this.input = new InputSubmit(name);
		}
		private HomeAction(String name, JSID id) {
			this.name = name;
			this.input = new InputButton(name, id);
		}
		
		public String getName() {
			return name;
		}
		public Input getInput() {
			return input;
		}
		public static HomeAction getActionByName(String name) {
			for(HomeAction action: values()) {
				if(action.getName().equals(name)) {
					return action;
				}
			}
			return null;
		}
	}
	
	public static List<ListItem> generateList(List<Author> authors, Boolean canCreate){
		List<ListItem> list = new ArrayList<ListItem>();
		
		if(canCreate) {
			List<InputHidden> hiddenList = new ArrayList<InputHidden>();
			hiddenList.add(new InputHidden("itemId", 0));
			InputButton button = new InputButton("Create author", JSID.AuthorOpen);
			list.add(new ListItem(button, hiddenList, CssClass.CreateButton));
		}
		for(Author author: authors) {
			List<InputHidden> hiddenList = new ArrayList<InputHidden>();
			hiddenList.add(new InputHidden("itemId", author.getId()));
			InputSubmit submit = new InputSubmit(author.getFirstName() + " " + author.getLastName());
			list.add(new ListItem(submit, hiddenList, CssClass.ListItem));
		}
		
		return list;
	}
	private static List<InputHidden> generateDescribeList(Author author){
		List<InputHidden> list = new ArrayList<InputHidden>();
		
		list.add(new InputHidden("ID", author.getId()));
		list.add(new InputHidden("First name", author.getFirstName()));
		list.add(new InputHidden("Last name", author.getLastName()));
		list.add(new InputHidden("Count of books", author.getBooksCount()));
		
		return list;
	}
	private static List<Input> generateHomeControlList(Author author){
		List<Input> list = new ArrayList<Input>();
		
		for(HomeAction action: HomeAction.values()) {
			if((action == HomeAction.AllBooks || action == HomeAction.RemoveBook) && author.getBooksCount() == 0) {
				continue;
			}
			list.add(action.getInput());
		}
		list.add(new InputHidden("itemId", author.getId()));
		
		return list; 
	}
	private static List<UpdatePopup> generatePopups(Author author){
		List<UpdatePopup> list = new ArrayList<UpdatePopup>();
		
		list.add(new UpdateAuthorPopup(author, "Update", Constants.path_AuthorUpdate, "", null));
		
		return list;
	}
	
	@GetMapping(path = Constants.path_AuthorList)
	public String authorListGet(Model model) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.getTransaction().begin();
		List<Author> authors = session.createQuery("FROM Author", Author.class).list();
		session.getTransaction().commit();
		
		ListPage page = new ListPage(generateList(authors, true), 
				new UpdateAuthorPopup("Create", Constants.path_AuthorUpdate, "", null),
				Constants.path_AuthorHome, "List of authors", "get");
		page.addAttributes(model);
		
		return ListPage.getFile();
	}
	
	@GetMapping(path = Constants.path_AuthorHome)
	public String authorHomeGet(Model model, Integer itemId) {
		Author author = new Author(itemId);
		ItemHome home = new ItemHome(generateDescribeList(author), 
				generateHomeControlList(author), 
				generatePopups(author), 
				new DeletePopup("Are you sure to delete author?", Constants.path_AuthorDelete, itemId), 
				Constants.path_AuthorHome, "Author home");
		home.addAttributes(model);
		return ItemHome.getFile();
	}
	@PostMapping(path = Constants.path_AuthorHome)
	public String authorHomePost(Model model, @RequestParam Integer itemId, @RequestParam String action) {
		
		switch(HomeAction.getActionByName(action)) {
		case Update:
		case Delete:
			System.out.println("[ERROR] Imposible action (Author, authorHpmePost)");
			return null;
		case AddBook:
			return authorAddBook(model, new Author(itemId));
		case RemoveBook:
			return authorRemoveBook(model, new Author(itemId));
		case AllBooks:
			return authorAllBooks(model, new Author(itemId));
		case Special:
			return authorSpecial(model, new Author(itemId));
		}
		return null;
	}
	
	@PostMapping(path = Constants.path_AuthorUpdate)
	public String authorUpdatePost(Model model, 
			@RequestParam Integer itemId, 
			@RequestParam String firstName, 
			@RequestParam String lastName) {
		Error error = LibrarianController.testPersonParameters(firstName, lastName);
		Author author = null;
		if(error != Error.NoError) {
			System.out.println("[ERROR] " + error.getDescription());
		} else {
			if(itemId <= 0) {
				author = new Author(null, "", "", null);
			} else {
				author = new Author(itemId);
			}
			author.setFirstName(firstName);
			author.setLastName(lastName);
			author.updateMe();
		}
		model.addAttribute("itemId", author.getId());
		return "redirect:" + Constants.path_AuthorHome;
	}
	
	@PostMapping(path = Constants.path_AuthorDelete)
	public String deleteAuthor(@RequestParam Integer itemId) {
		Author author = new Author(itemId);
		author.deleteMe();
		return "redirect:" + Constants.path_AuthorList;
	}
	
	public String authorAllBooks(Model model, Author author) {
		ListPage page = new ListPage(BookController.generateList(author.getBooks(HibernateUtil.getSessionFactory().getCurrentSession()), false),
				null,
				Constants.path_BookHome, "List of books of author '" + author.getFirstName() + " " + author.getLastName() + "'", "get");
		page.addAttributes(model);
		
		return ListPage.getFile();
	}
	public String authorAddBook(Model model, Author author) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.getTransaction().begin();
		List<Book> books = session.createSQLQuery(
  			  "SELECT {book.*} "
  			+ "FROM {book} "
  			+ "WHERE {book}.id NOT IN "
	  			+ "(SELECT author_book.book_id "
	  			+ "FROM author_book "
	  			+ "WHERE author_book.author_id = " + author.getId() + ")")
  			.addEntity("book", Book.class).list();
		session.getTransaction().commit();
		
		List<InputHidden> hiddens = new ArrayList<InputHidden>();
		hiddens.add(new InputHidden("authorId", author.getId()));
		
		ListPage page = new ListPage(BookController.generateList(books, false),
				null,
				new ListPopup(hiddens, Constants.path_AuthorSelectBook, ListPopup.select),
				"Select book for author '" + author.getFirstName() + " " + author.getLastName() + "'", "post");
		page.addAttributes(model);
		
		return ListPage.getFile();
	}
	public String authorRemoveBook(Model model, Author author) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.getTransaction().begin();
		List<Book> books = author.getBooks(session);
		session.getTransaction().commit();
		
		List<InputHidden> hiddens = new ArrayList<InputHidden>();
		hiddens.add(new InputHidden("authorId", author.getId()));
		
		ListPage page = new ListPage(BookController.generateList(books, false),
				null,
				new ListPopup(hiddens, Constants.path_AuthorRemoveBook, ListPopup.delete),
				"Remove book from author '" + author.getFirstName() + " " + author.getLastName() + "'", "post");
		page.addAttributes(model);
		
		return ListPage.getFile();
	}
	
	@PostMapping(path = Constants.path_AuthorSelectBook)
	public String authorSelectBookPost(Model model, @RequestParam Integer itemId, @RequestParam Integer authorId) {
		Book book = new Book(itemId);
		Author author = new Author(authorId);
		author.addBook(book);
		model.addAttribute("itemId", itemId);
		return "redirect:" + Constants.path_BookHome;
	}
	@PostMapping(path = Constants.path_AuthorRemoveBook)
	public String authorRemoveBookPost(Model model, @RequestParam Integer itemId, @RequestParam Integer authorId) {
		Book book = new Book(itemId);
		Author author = new Author(authorId);
		author.removeBook(book);
		model.addAttribute("itemId", authorId);
		return "redirect:" + Constants.path_AuthorHome;
	}
	
	private String authorSpecial(Model model, Author author) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.getTransaction().begin();
		List<Customer> customers = session.createSQLQuery(
				  "SELECT {customer.*} FROM {customer} WHERE {customer}.id IN "
					+ "(SELECT FS.FID as ID FROM "
						+ "(SELECT DISTINCT {customer}.id AS FID, count(operation.book_id) AS FC "
						+ "FROM operation INNER JOIN author_book INNER JOIN {customer} "
						+ "ON operation.book_id = author_book.book_id AND operation.customer_id = customer.id "
						+ "WHERE author_book.author_id = " + author.getId() + " "
						+ "GROUP BY {customer}.id) AS FS "
					+ "INNER JOIN "
						+ "(SELECT COUNT(author_book.book_id) as SC "
						+ "FROM author INNER JOIN author_book "
						+ "ON author.id = author_book.author_id "
						+ "WHERE author.id = " + author.getId() + ") AS SS "
					+ "ON FS.FC = SS.SC)")
				.addEntity("customer", Customer.class)
				.list();
		session.getTransaction().commit();
		
		ListPage page = new ListPage(CustomerController.generateList(customers, false),
				null, Constants.path_CustomerHome,
				"List of customers who got all books of author '" + author.getFirstName() + " " + author.getLastName() + "'", "get");
		page.addAttributes(model);
		
		return ListPage.getFile();
	}
}
