package web.spring.controller;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import hibernate.tables.Book;
import hibernate.tables.Customer;
import hibernate.tables.Librarian;
import hibernate.tables.Library;
import hibernate.util.HibernateUtil;
import web.spring.controller.Constants.Error;
import web.thymeleaf.page.ItemHome;
import web.thymeleaf.page.ListPage;
import web.thymeleaf.templates.basic.ListItem;
import web.thymeleaf.templates.basic.ListItem.CssClass;
import web.thymeleaf.templates.input.Input;
import web.thymeleaf.templates.input.InputButton;
import web.thymeleaf.templates.input.InputHidden;
import web.thymeleaf.templates.input.InputSubmit;
import web.thymeleaf.templates.input.InputButton.JSID;
import web.thymeleaf.templates.popup.DeletePopup;
import web.thymeleaf.templates.popup.UpdateBookPopup;
import web.thymeleaf.templates.popup.UpdateLibrarianPopup;
import web.thymeleaf.templates.popup.UpdateLibraryPopup;
import web.thymeleaf.templates.popup.UpdatePopup;

@Controller
public class LibraryController {
	private enum HomeAction{
		Update("update", JSID.LibraryOpen),
		Librarian("librarian"),
		NewLibrarian("new librarian", JSID.LibrarianOpen),
		AllBooks("list of books"),
		UnreturnedBooks("list of unreturned books"),
		NewBook("add new book", JSID.BookOpen),
		Special("special"),
		Delete("delete", JSID.DeleteOpen);
		private String name;
		private Input input;
		HomeAction(String name, InputButton.JSID jsId){
			this.name = name;
			this.input = new InputButton(name, jsId);
		}
		HomeAction(String name){
			this.name = name;
			this.input = new InputSubmit(name);
		}
		public Input getInput() {
			return input;
		}
		public String getName() {
			return name;
		}
		public static HomeAction getAction(String name) {
			for(HomeAction action: values()) {
				if(action.getName().equals(name)) {
					return action;
				}
			}
			return null;
		}
	}
	
	public static List<ListItem> generateList(List<Library> libraries, Boolean canCreate){
		List<ListItem> list = new ArrayList<ListItem>();
		if(canCreate) {
			List<InputHidden> hiddenList = new ArrayList<InputHidden>();
			hiddenList.add(new InputHidden("itemId", 0));
			InputButton button = new InputButton("Create Library", JSID.LibraryOpen);
			ListItem createButton = new ListItem(button, hiddenList, CssClass.CreateButton);
			list.add(createButton);
		}
		
		for(Library lib: libraries) {
			List<InputHidden> hiddenList = new ArrayList<InputHidden>();
			hiddenList.add(new InputHidden("itemId", lib.getId()));
			InputSubmit submit = new InputSubmit("[" + lib.getCity() + "] " + lib.getName());
			ListItem item = new ListItem(submit, hiddenList, CssClass.ListItem);
			list.add(item);
		}
		
		return list;
	}
	public static List<InputHidden> generateDescription(Library lib){
		List<InputHidden> list = new ArrayList<InputHidden>();
		
		list.add(new InputHidden("ID", lib.getId()));
		list.add(new InputHidden("Name", lib.getName()));
		list.add(new InputHidden("City", lib.getCity()));
		
		
		Librarian liber = lib.getLibrarian();
		String liberDescription = "none";
		if(liber != null) {
			liberDescription = liber.getFirstName() + " " + liber.getLastName();
		}
		list.add(new InputHidden("Librarian", liberDescription));
		
		
		list.add(new InputHidden("Book count", lib.getCountOfBooks()));
		
		return list;
	}
	public static List<Input> generateHomeControlList(Library lib){
		List<Input> list = new ArrayList<Input>();
		
		for(HomeAction action: HomeAction.values()) {
			if(lib.getLibrarian() == null && action == HomeAction.Librarian) {
				continue;
			}
			if(lib.getCountOfBooks() == 0 && (action == HomeAction.AllBooks || action == HomeAction.UnreturnedBooks)) {
				continue;
			}
			list.add(action.getInput());
		}
		
		list.add(new InputHidden("itemId", lib.getId().toString()));
		
		return list;
	}
	public static List<UpdatePopup> generateLibraryPopups(Library lib){
		List<UpdatePopup> list = new ArrayList<UpdatePopup>();
		
		list.add(new UpdateLibraryPopup(lib, Constants.path_LibraryUpdate, "Update library", null, ""));
		
		List<InputHidden> optionalList_librarian = new ArrayList<InputHidden>();
		optionalList_librarian.add(new InputHidden("libraryId", lib.getId()));
		String librarianError = "";
		if(lib.getLibrarian() != null) {
			librarianError = "This library already has a librarian. If you create new librarian, system will delete old one";
		}
		list.add(new UpdateLibrarianPopup(optionalList_librarian, Constants.path_LibraryCreateLibrarian, "Create librarian", librarianError));
		
		List<InputHidden> optionalList_book = new ArrayList<InputHidden>();
		optionalList_book.add(new InputHidden("libraryId", lib.getId()));
		list.add(new UpdateBookPopup("Create", Constants.path_LibraryCreateBook, "", optionalList_book));
		
		return list;
	}
	
	@GetMapping(path = Constants.path_LibraryList)
	public String libraryListGet(Model model) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.getTransaction().begin();
		List<Library> libraries = session.createQuery("FROM Library", Library.class).list();
		session.getTransaction().commit();
		
		UpdateLibraryPopup popup = new UpdateLibraryPopup(Constants.path_LibraryUpdate, "Create library", null, "");
		ListPage page = new ListPage(generateList(libraries, true), 
				popup, 
				Constants.path_LibraryHome, "List of libraries", "get");
		page.addAttributes(model);
		
		return ListPage.getFile();
	}
	
	@GetMapping(path = Constants.path_LibraryHome)
	public String libraryHomeGet(Model model, @RequestParam Integer itemId) {
		Library lib = new Library(itemId);
		DeletePopup delete = new DeletePopup("Are you sure to delete library?", Constants.path_LibraryDelete, itemId);
		ItemHome home = new ItemHome(
				generateDescription(lib), 
				generateHomeControlList(lib), 
				generateLibraryPopups(lib), 
				delete, Constants.path_LibraryHome, "Library home");
		home.addAttributes(model);
		return ItemHome.getFile();
	}
	@PostMapping(path = Constants.path_LibraryHome)
	public String libraryHomePost(Model model, @RequestParam String action, @RequestParam Integer itemId) {
		switch(HomeAction.getAction(action)) {
		case Update:
		case NewLibrarian:	
		case NewBook:
		case Delete:
			System.out.println("[ERROR] Imposible action (Library, libraryHomePost)");
			return null;
		case Librarian:
			model.addAttribute("itemId", new Library(itemId).getLibrarian().getId());
			return "redirect:" + Constants.path_LibrarianHome;
		case AllBooks:
			return getAllBooks(model, new Library(itemId));
		case UnreturnedBooks:
			return getUnreturnedBooks(model, new Library(itemId));
		case Special:
			return librarySpecial(model, new Library(itemId));
		}
		return null;
	}
	@PostMapping(path = Constants.path_LibraryUpdate)
	public String updatePost(Model model, @RequestParam Integer itemId, @RequestParam String name, @RequestParam String city) {
		Constants.Error error = testLibraryParameters(name, city);
		Library lib = null;
		if(error != Constants.Error.NoError) {
			System.out.println("[ERROR] " + error.getDescription());
		} else {
			if(itemId <= 0) {
				lib = new Library(null, "", "");
			} else {
				lib = new Library(itemId);
			}
			lib.setName(name);
			lib.setCity(city);
			lib.updateMe();
		}
		model.addAttribute("itemId", lib.getId());
		return "redirect:" + Constants.path_LibraryHome;
	}
	
	public static Constants.Error testLibraryParameters(String name, String city) {
		if(name == null || name.isEmpty()) {
			return Constants.Error.Library_emptyName;
		}
		if(name.length() > 30) {
			return Constants.Error.Library_nameLength;
		}
		if(city == null || city.isEmpty()) {
			return Constants.Error.Library_emptyCity;
		}
		if(city.length() > 20) {
			return Constants.Error.Library_cityLength;
		}
		return Constants.Error.NoError;
	}
	
	@PostMapping(path = Constants.path_LibraryDelete)
	private String deleteLibrary(Integer itemId) {
		Library lib = new Library(itemId);
		lib.deleteMe();
		return "redirect:" + Constants.path_LibraryList;
	}
	
	@PostMapping(path = Constants.path_LibraryCreateLibrarian)
	public String createLibrarianPost(Model model,
			@RequestParam Integer libraryId,
			@RequestParam String firstName,
			@RequestParam String lastName) {
		Error error = LibrarianController.testPersonParameters(firstName, lastName);
		Librarian librarian = null;
		if(error != Error.NoError) {
			System.out.println("[ERROR] " + error.getDescription());
		} else {
			Library lib = new Library(libraryId);
			librarian = new Librarian(null, "", "", null);
			librarian.setFirstName(firstName);
			librarian.setLastName(lastName);
			librarian.setLibrary(lib);
			librarian.updateMe();
		}
		
		model.addAttribute("itemId", librarian.getId());
		return "redirect:" + Constants.path_LibrarianHome;
	}
	private String getAllBooks(Model model, Library lib) {
		List<Book> books = lib.getBooks(HibernateUtil.getSessionFactory().getCurrentSession());
		
		ListPage page = new ListPage(BookController.generateList(books, false), 
				null,
				Constants.path_BookHome, "List of books of library '" + lib.getName() + "'", "get");
		page.addAttributes(model);
		
		return ListPage.getFile();
	}
	private String getUnreturnedBooks(Model model, Library lib) {
		List<Book> books = lib.getUnreturnedBooks();
		ListPage page = new ListPage(BookController.generateList(books, false),
				null, Constants.path_BookHome,
				"List of unreturned books of library '" + lib.getName() + "'", "get");
		page.addAttributes(model);
		return ListPage.getFile();
	}

	@PostMapping(path = Constants.path_LibraryCreateBook)
	public String libraryCreateBookPost(Model model, 
			@RequestParam Integer libraryId, 
			@RequestParam Integer itemId, 
			@RequestParam String name, 
			@RequestParam String count) {
		Book book = new Book(null, "", 0, null, null);
		Error error = BookController.testBookParameters(name, count, book);
		if(error != Error.NoError) {
			System.out.println("[ERROR] " + error.getDescription());
		} else {
			Library lib = new Library(libraryId);
			book.setName(name);
			book.setCount(Integer.parseInt(count));
			book.setLibrary(lib);
			book.updateMe();
		}
		model.addAttribute("itemId", book.getId());
		return "redirect:" + Constants.path_BookHome;
	}
	public String librarySpecial(Model model, Library lib) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.getTransaction().begin();
		List<Customer> customers = session.createSQLQuery(
				  "SELECT DISTINCT {customer.*} "
				+ "FROM {customer} INNER JOIN operation INNER JOIN book "
				+ "ON {customer}.id = operation.customer_id AND operation.book_id = book.id "
				+ "WHERE book.library_id = " + lib.getId() + " AND {customer}.id not in "
					+ "(SELECT customer.id "
					+ "FROM customer INNER JOIN operation INNER JOIN book "
					+ "ON customer.id = operation.customer_id AND operation.book_id = book.id "
					+ "WHERE book.library_id != " + lib.getId() + ")"
				).addEntity("customer", Customer.class)
				.list();
		session.getTransaction().commit();
		ListPage page = new ListPage(CustomerController.generateList(customers, false),
				null,
				Constants.path_CustomerHome, "List of customers of library '" + lib.getName() + "' only", "get");
		page.addAttributes(model);
		return ListPage.getFile();
	}
}
