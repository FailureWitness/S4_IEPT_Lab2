package web.spring.controller;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import hibernate.tables.Author;
import hibernate.tables.Book;
import hibernate.tables.Customer;
import hibernate.tables.Library;
import hibernate.tables.Operation;
import hibernate.util.HibernateUtil;
import web.spring.controller.Constants.Error;
import web.thymeleaf.page.ItemHome;
import web.thymeleaf.page.ListPage;
import web.thymeleaf.templates.basic.ListItem;
import web.thymeleaf.templates.basic.ListItem.CssClass;
import web.thymeleaf.templates.input.Input;
import web.thymeleaf.templates.input.InputButton;
import web.thymeleaf.templates.input.InputButton.JSID;
import web.thymeleaf.templates.input.InputHidden;
import web.thymeleaf.templates.input.InputSubmit;
import web.thymeleaf.templates.popup.DeletePopup;
import web.thymeleaf.templates.popup.ListPopup;
import web.thymeleaf.templates.popup.UpdateAuthorPopup;
import web.thymeleaf.templates.popup.UpdateBookPopup;
import web.thymeleaf.templates.popup.UpdateLibraryPopup;
import web.thymeleaf.templates.popup.UpdatePopup;

@Controller
public class BookController {
	private enum HomeAction{
		Update("update", JSID.BookOpen),
		Library("library"),
		SelectLibrary("select library"),
		NewLibrary("create library", JSID.LibraryOpen),
		Authors("authors"),
		AddAuthor("add author"),
		NewAuthor("new author", JSID.AuthorOpen),
		Operations("operations"),
		NewOperation("new operation"),
		RemoveAuthor("remove author"),
		Special("special"),
		Delete("delete", JSID.DeleteOpen);
		private String name;
		private Input input;
		private HomeAction(String name) {
			this.name = name;
			input = new InputSubmit(name);
			
		}
		private HomeAction(String name, JSID id) {
			this.name = name;
			input = new InputButton(name, id);
		}
		public String getName() {
			return name;
		}
		public Input getInput() {
			return input;
		}
		public static HomeAction getByName(String name) {
			for(HomeAction a: values()) {
				if(a.getName().equals(name)) {
					return a;
				}
			}
			return null;
		}
	}
	public static List<ListItem> generateList(List<Book> books, Boolean canCreate){
		List<ListItem> list = new ArrayList<ListItem>();
		
		if(canCreate) {
			List<InputHidden> hiddenList = new ArrayList<InputHidden>();
			hiddenList.add(new InputHidden("itemId", "0"));
			InputButton button = new InputButton("Create book", JSID.BookOpen);
			list.add(new ListItem(button, hiddenList, CssClass.CreateButton));
		}
		for(Book book: books) {
			List<InputHidden> hiddenList = new ArrayList<InputHidden>();
			hiddenList.add(new InputHidden("itemId", book.getId().toString()));
			InputSubmit submit = new InputSubmit(book.getName());
			list.add(new ListItem(submit, hiddenList, CssClass.ListItem));
		}
		
		return list;
	}
	private static List<InputHidden> generateDescription(Book book){
		List<InputHidden> list = new ArrayList<InputHidden>();
		
		list.add(new InputHidden("ID", book.getId().toString()));
		list.add(new InputHidden("Name", book.getName()));
		list.add(new InputHidden("Count", book.getCount().toString()));
		
		list.add(new InputHidden("Unreturned count", book.getUnreturnedCount().toString()));
		
		String authors = "";
		for(Author author: book.getAuthors()) {
			authors += "<br> " + author.getFirstName() + " " + author.getLastName();
		}
		list.add(new InputHidden("Authors", authors));
		
		list.add(new InputHidden("Library", book.getLibrary().getName()));
		
		return list;
	}
	private static List<Input> generateHomeControlList(Book book){
		List<Input> list = new ArrayList<Input>();
		
		for(HomeAction action: HomeAction.values()) {
			if(action == HomeAction.NewOperation && book.getCount() == book.getUnreturnedCount()) {
				continue;
			}
			if((action == HomeAction.Authors || action == HomeAction.RemoveAuthor) && book.getAuthors().size() == 0) {
				continue;
			}
			list.add(action.getInput());
		}
		list.add(new InputHidden("itemId", book.getId().toString()));
		
		return list;
	}
	private static List<UpdatePopup> generatePopups(Book book){
		List<UpdatePopup> list = new ArrayList<UpdatePopup>();
		
		list.add(new UpdateBookPopup(book, "Update", Constants.path_BookUpdate, "", null));
		
		List<InputHidden> libraryOptional = new ArrayList<InputHidden>();
		libraryOptional.add(new InputHidden("bookId", book.getId()));
		UpdateLibraryPopup libraryPopup = new UpdateLibraryPopup(Constants.path_BookCrateLibrary, "Create", libraryOptional, "");
		list.add(libraryPopup);
		
		List<InputHidden> authorOptional = new ArrayList<InputHidden>();
		authorOptional.add(new InputHidden("bookId", book.getId()));
		list.add(new UpdateAuthorPopup("Create", Constants.path_BookCreateAuthor, "", authorOptional));
		
		return list;
	}
	
	@GetMapping(path = Constants.path_BookList)
	public String bookListGet(Model model) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.getTransaction().begin();
		List<Book> books = session.createQuery("FROM Book ", Book.class).list();
		session.getTransaction().commit();
		
		ListPage page = new ListPage(generateList(books, false), 
				null, 
				Constants.path_BookHome, "List of books", "get");
		page.addAttributes(model);
		
		return ListPage.getFile();
	}
	
	@GetMapping(path = Constants.path_BookHome)
	public String bookHomeGet(Model model, @RequestParam Integer itemId) {
		Book book = new Book(itemId);
		
		DeletePopup delete = new DeletePopup("Are you sure to delete book?", Constants.path_BookDelete, itemId);
		ItemHome home = new ItemHome(
				generateDescription(book), generateHomeControlList(book), 
				generatePopups(book), delete, 
				Constants.path_BookHome, "Book home");
		home.addAttributes(model);
		
		return ItemHome.getFile();
	}
	@PostMapping(path = Constants.path_BookHome)
	public String bookHomePost(Model model, @RequestParam String action, @RequestParam Integer itemId) {
		
		switch(HomeAction.getByName(action)) {
		case Delete:
		case NewLibrary:
		case NewAuthor:
		case Update:
			System.out.println("[ERROR] Imposible action (Book, bookHomePost)");
			return null;
		case Library:
			model.addAttribute("itemId", new Book(itemId).getLibrary().getId());
			return "redirect:" + Constants.path_LibraryHome;
		case SelectLibrary:
			return bookSelectLibrary(model, new Book(itemId));
		case Authors:
			return bookAllAuthors(model, new Book(itemId));
		case AddAuthor:
			return bookAddAuthor(model, new Book(itemId));
		case RemoveAuthor:
			return bookRemoveAuthor(model, new Book(itemId));
		case Operations:
			return bookAllOperations(model, new Book(itemId));
		case NewOperation:
			return bookCreateOperation(model, itemId);
		case Special:
			return bookSpecial(model, new Book(itemId));
		}
		
		return null;
	}
	
	@PostMapping(path = Constants.path_BookUpdate)
	public String bookUpdatePost(Model model, 
			@RequestParam Integer itemId, 
			@RequestParam String name,
			@RequestParam String count) {
		Book book = new Book(itemId);
		Error error = testBookParameters(name, count, book);
		if(error != Error.NoError) {
			System.out.println("[ERROR] " + error.getDescription());
		} else {
			book.setName(name);
			book.setCount(Integer.parseInt(count));
			book.updateMe();
		}
		
		model.addAttribute("itemId", book.getId());
		return "redirect:" + Constants.path_BookHome;
	}
	
	@PostMapping(path = Constants.path_BookDelete)
	public String deleteBook(Integer itemId) {
		Book book = new Book(itemId);
		book.deleteMe();
		return "redirect:" + Constants.path_BookList;
	}
	private static Boolean grater(String value, String maxValue) {
		if(value.length() > maxValue.length()) {
			return true;
		}
		if(value.length() < maxValue.length()) {
			return false;
		}
		for(int i = 0; i < value.length(); i++) {
			if(value.charAt(i) > maxValue.charAt(i)) {
				return true;
			}
			if(value.charAt(i) < maxValue.charAt(i)) {
				return false;
			}
		}
		return false;
	}
	public static Error testBookParameters(String name, String count, Book book) {
		if(name == null || name.isEmpty()) {
			return Error.Book_emptyName;
		}
		if(name.length() > 30) {
			return Error.Book_nameLength;
		}
		if(count == null || count.length() == 0) {
			return Error.Book_emptyCount;
		}
		if(count.charAt(0) == '0') {
			return Error.Book_countStartFromZero;
		}
		for(int i = 1; i < count.length(); i++) {
			if(count.charAt(i) < '0' || count.charAt(i) > '9') {
				return Error.Book_onlyDigitsCount;
			}
		}
		if(count.charAt(0) == '-') {
			return Error.Book_negativeCount;
		}
		if(grater(count, Integer.toString(Integer.MAX_VALUE))) {
			return Error.Book_countLength;
		}
		if(book.getId() != null && book.getId() != 0 && Integer.parseInt(count) < book.getUnreturnedCount()) {
			return Error.Book_countLessUnreturned;
		}
		
		return Error.NoError;
	}
	
	@PostMapping(path = Constants.path_BookCrateLibrary)
	public String bookCreateLibPost(Model model, 
			@RequestParam Integer bookId, 
			@RequestParam Integer itemId,
			@RequestParam String name,
			@RequestParam String city) {
		Error error = LibraryController.testLibraryParameters(name, city);
		if(error != Error.NoError) {
			model.addAttribute("itemId", bookId);
			model.addAttribute("error", error.getId());
			return "redirect:" + Constants.path_BookCrateLibrary;
		}
		
		Book book = new Book(bookId);
		Library lib = new Library(null, city, name);
		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.getTransaction().begin();
		try {
			lib.updateMe(session);
			book.setLibrary(lib);
			book.updateMe(session);
			session.getTransaction().commit();
		} finally {
			if(session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
		}
		
		model.addAttribute("itemId", lib.getId());
		return "redirect:" + Constants.path_LibraryHome;
	}
	
	public String bookSelectLibrary(Model model, Book book) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.getTransaction().begin();
		List<Library> libs = session.createQuery("FROM Library lib WHERE lib.id != " + book.getLibrary().getId(), Library.class).list();
		session.getTransaction().commit();
		System.out.println("-----------------");
		List<InputHidden> hiddens = new ArrayList<InputHidden>();
		hiddens.add(new InputHidden("bookId", book.getId()));
		ListPage page = new ListPage(LibraryController.generateList(libs, false), 
				null,
				new ListPopup(hiddens, Constants.path_BookSelectLibrary, ListPopup.select),
				"Select library for book '" + book.getName() + "'", "post");
		page.addAttributes(model);
		return ListPage.getFile();
	}
	@PostMapping(Constants.path_BookSelectLibrary)
	public String bookSelectLibraryPost(Model model, @RequestParam Integer itemId, @RequestParam Integer bookId) {
		Book book = new Book(bookId);
		book.setLibrary(new Library(itemId));
		book.updateMe();
		model.addAttribute("itemId", itemId);
		return "redirect:" + Constants.path_LibraryHome;
	}
	
	public String bookAllAuthors(Model model, Book book) {
		ListPage page = new ListPage(AuthorController.generateList(new ArrayList<Author>(book.getAuthors()), false),
				null,
				Constants.path_AuthorHome, "List of authors of book '" + book.getName() + "'", "get");
		page.addAttributes(model);
		return ListPage.getFile();
	}
	
	public String bookAddAuthor(Model model, Book book) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.getTransaction().begin();
		List<Author> authors = session.createSQLQuery(
	  			  "SELECT {author.*} "
	  			+ "FROM {author} "
	  			+ "WHERE {author}.id NOT IN "
		  			+ "(SELECT author_book.author_id "
		  			+ "FROM author_book "
		  			+ "WHERE author_book.book_id = " + book.getId() + ")")
	  			.addEntity("author", Author.class).list();
		session.getTransaction().commit();
		
		List<InputHidden> hiddens = new ArrayList<InputHidden>();
		hiddens.add(new InputHidden("bookId", book.getId()));
		
		ListPage page = new ListPage(AuthorController.generateList(authors, false),
				null,
				new ListPopup(hiddens, Constants.path_BookSelectAuthor, ListPopup.select),
				"Select author for book '" + book.getName() + "'", "post");
		page.addAttributes(model);
		
		return ListPage.getFile();
	}
	@PostMapping(path = Constants.path_BookSelectAuthor)
	public String bookAddAuthorSelect(Model model, @RequestParam Integer itemId, @RequestParam Integer bookId) {
		Book book = new Book(bookId);
		Author author = new Author(itemId);
		author.addBook(book);

		model.addAttribute("itemId", itemId);
		return "redirect:" + Constants.path_AuthorHome;
	}
	
	public String bookRemoveAuthor(Model model, Book book) {
		List<Author> authors = new ArrayList<Author>(book.getAuthors());
		
		List<InputHidden> hiddens = new ArrayList<InputHidden>();
		hiddens.add(new InputHidden("bookId", book.getId()));
		ListPage page = new ListPage(AuthorController.generateList(authors, false),
				null,
				new ListPopup(hiddens, Constants.path_BookRemoveAuthor, ListPopup.delete),
				"Remove author from book '" + book.getName() + "'", "post");
		page.addAttributes(model);
		
		return ListPage.getFile();
	}
	@PostMapping(path = Constants.path_BookRemoveAuthor)
	public String bookRemoveAuthorPost(Model model, @RequestParam Integer itemId, @RequestParam Integer bookId) {
		Book book = new Book(bookId);
		Author author = new Author(itemId);
		author.removeBook(book);
		
		model.addAttribute("itemId", bookId);
		return "redirect:" + Constants.path_BookHome;
	}
	
	@PostMapping(path = Constants.path_BookCreateAuthor)
	public String bookCrateAuthor(Model model, 
			@RequestParam Integer bookId, 
			@RequestParam String firstName, 
			@RequestParam String lastName) {
		Book book = new Book(bookId);
		Author author = new Author(null, firstName, lastName, null);
		Error error = LibrarianController.testPersonParameters(firstName, lastName);
		if(error != Error.NoError) {
			System.out.println("[ERROR] " + error.getDescription());
		} else {
			author.updateMe();
			author.addBook(book);
		}
		model.addAttribute("itemId", author.getId());
		return "redirect:" + Constants.path_AuthorHome;
	}
	private String bookAllOperations(Model model, Book book) {
		List<Operation> operations = book.getOperations();
		ListPage page = new ListPage(OperationController.generateList(operations, false), 
				null, Constants.path_OperationHome, 
				"List of operations of book '" + book.getName() + "'", "get");
		page.addAttributes(model);
		return ListPage.getFile();
	}
	private String bookCreateOperation(Model model, Integer itemId) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.getTransaction().begin();
		List<Customer> customers = session.createQuery("FROM Customer", Customer.class).list();
		session.getTransaction().commit();
		
		List<InputHidden> hiddens = new ArrayList<InputHidden>();
		hiddens.add(new InputHidden("bookId", itemId));
		ListPage page = new ListPage(CustomerController.generateList(customers, false), 
				null,
				new ListPopup(hiddens, Constants.peth_BookCreateOperation, ListPopup.select),
				"Select customer for operation", "post");
		page.addAttributes(model);
		return ListPage.getFile();
	}
	
	@PostMapping(path = Constants.peth_BookCreateOperation)
	public String bookCreateOptionPost(Model model, Integer itemId, Integer bookId) {
		Operation operation = new Operation(null, new Customer(itemId), new Book(bookId), System.currentTimeMillis(), false);
		operation.updateMe();
		model.addAttribute("itemId", operation.getId());
		return "redirect:" + Constants.path_OperationHome;
	}
	
	public String bookSpecial(Model model, Book book) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.getTransaction().begin();
		List<Book> books = session.createSQLQuery(
				  "SELECT DISTINCT {book.*} "
				+ "FROM {book} INNER JOIN author_book "
				+ "ON {book}.id = author_book.book_id "
				+ "WHERE {book}.id NOT IN "
					+ "(SELECT DISTINCT book.id "
					+ "FROM book INNER JOIN author_book "
					+ "ON book.id = author_book.book_id "
					+ "WHERE author_book.author_id NOT IN "
						+ "(SELECT author_book.author_id "
						+ "FROM author_book "
						+ "WHERE author_book.book_id = " + book.getId() + "))"
//
//				  "SELECT DISTINCT {book.*} "
//				+ "FROM {book} INNER JOIN author_book INNER JOIN "
//					+ "(SELECT author_book.author_id AS id "
//					+ "FROM author_book "
//					+ "WHERE author_book.book_id = " + book.getId() + ") AS authors "
//				+ "ON {book}.id = author_book.book_id AND author_book.author_id = authors.id"
				).addEntity("book", Book.class).list();
		session.getTransaction().commit();
		
		ListPage page = new ListPage(generateList(books, false), null,
				Constants.path_BookHome, "List of books of authors of '" + book.getName() + "'", "get");
		page.addAttributes(model);
		
		return ListPage.getFile();
	}
	
}
