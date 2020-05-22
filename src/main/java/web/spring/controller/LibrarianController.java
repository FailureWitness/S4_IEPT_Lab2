package web.spring.controller;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
import web.thymeleaf.templates.input.InputButton.JSID;
import web.thymeleaf.templates.input.InputHidden;
import web.thymeleaf.templates.input.InputSubmit;
import web.thymeleaf.templates.input.InputText;
import web.thymeleaf.templates.popup.DeletePopup;
import web.thymeleaf.templates.popup.ListPopup;
import web.thymeleaf.templates.popup.UpdateLibrarianPopup;
import web.thymeleaf.templates.popup.UpdateLibraryPopup;
import web.thymeleaf.templates.popup.UpdatePopup;

@Controller
public class LibrarianController {
	private enum HomeAction{
		Update("update", JSID.LibrarianOpen),
		Library("library"),
		SelectLibrary("select library"),
		NewLibrary("create library", JSID.LibraryOpen),
		Delete("delete", JSID.DeleteOpen);
		private String name;
		private Input input;
		private HomeAction(String name, JSID id) {
			this.name = name;
			input = new InputButton(name, id);
		}
		private HomeAction(String name) {
			this.name = name;
			input = new InputSubmit(name);
		}
		public String getName() {
			return name;
		}
		public Input getInput() {
			return input;
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
	public static List<ListItem> generateList(List<Librarian> librarians, Boolean canCreate){
		List<ListItem> list = new ArrayList<ListItem>();
		
		if(canCreate) {
			List<InputHidden> hiddenList = new ArrayList<InputHidden>();
			hiddenList.add(new InputHidden("itemId", "0"));
			InputButton button = new InputButton("Create librarian", JSID.LibrarianOpen);
			ListItem item = new ListItem(button, hiddenList, CssClass.CreateButton);
			list.add(item);
		}
		for(Librarian l: librarians) {
			List<InputHidden> hiddenList = new ArrayList<InputHidden>();
			hiddenList.add(new InputHidden("itemId", l.getId().toString()));
			InputSubmit submit = new InputSubmit(l.getFirstName() + " " + l.getLastName());
			ListItem item = new ListItem(submit, hiddenList, CssClass.ListItem);
			list.add(item);
		}
		
		return list;
	}
	private static List<InputHidden> generateDescriptionList(Librarian librarian){
		List<InputHidden> list = new ArrayList<InputHidden>();
		
		list.add(new InputHidden("ID", librarian.getId().toString()));
		list.add(new InputHidden("First name", librarian.getFirstName()));
		list.add(new InputHidden("Last name", librarian.getLastName()));
		list.add(new InputHidden("Library", librarian.getLibrary().getName()));
		
		return list;
	}
	private static List<Input> generateHomeControlList(Librarian librarian){
		List<Input> list = new ArrayList<Input>();
		
		for(HomeAction action: HomeAction.values()) {
			list.add(action.getInput());
		}
		
		list.add(new InputHidden("itemId", librarian.getId().toString()));
		
		return list;
	}
	private static List<UpdatePopup> generateLibrarianPopups(Librarian librarian){
		List<UpdatePopup> list = new ArrayList<UpdatePopup>();
		
		list.add(new UpdateLibrarianPopup(librarian, null, Constants.path_LibrarianUpdate, "Update librarian", ""));
		List<InputHidden> optionalLibraryList = new ArrayList<InputHidden>();
		optionalLibraryList.add(new InputHidden("librarianId", librarian.getId()));
		list.add(new UpdateLibraryPopup(Constants.path_LibrarianCreateLibrary, "Create library", optionalLibraryList, ""));
		
		return list;
	}
	
	@GetMapping(path = Constants.path_LibrarianList)
	public String librarianListGet(Model model) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.getTransaction().begin();
		
		List<Librarian> librarians = session.createQuery("FROM Librarian", Librarian.class).list(); 
		
		session.getTransaction().commit();
		
		ListPage page = new ListPage(generateList(librarians, false), 
				null, 
				Constants.path_LibrarianHome, "List of librarians", "get");
		page.addAttributes(model);
		
		return ListPage.getFile();
	}
	
	@GetMapping(path = Constants.path_LibrarianHome)
	public String librarianHomeGet(Model model, @RequestParam Integer itemId) {
		Librarian librarian = new Librarian(itemId);
		DeletePopup delete = new DeletePopup("Are you sure to delete librarian?", Constants.path_LibrarianDelete, itemId);
		
		ItemHome home = new ItemHome(
				generateDescriptionList(librarian), 
				generateHomeControlList(librarian),
				generateLibrarianPopups(librarian),
				delete, Constants.path_LibrarianHome, "Librarian home");
		home.addAttributes(model);
		
		return ItemHome.getFile();
	}
	@PostMapping(path = Constants.path_LibrarianHome)
	public String librarianHomeGet(Model model, @RequestParam String action, @RequestParam Integer itemId) {
		switch(HomeAction.getAction(action)) {
		case Update:
		case Delete:
		case NewLibrary:
			System.out.println("[ERROR] Imposible action (Librarian, librarianHomePost)");
			return null;
		case Library:
			model.addAttribute("itemId", new Librarian(itemId).getLibrary().getId());
			return "redirect:" + Constants.path_LibraryHome;
		case SelectLibrary:
			model.addAttribute("itemId", itemId);
			return "redirect:" + Constants.path_LibrarianSelectLibrary;
		}
		return null;
	}
	
	@PostMapping(path = Constants.path_LibrarianUpdate)
	public String labrarianUpdatePost(Model model, 
			@RequestParam Integer itemId, 
			@RequestParam(required = false) Integer libraryId,
			@RequestParam String firstName, 
			@RequestParam String lastName) {
		Error error = testPersonParameters(firstName, lastName);
		Librarian librarian = null;
		if(error != Error.NoError) {
			System.out.println("[ERROR] " + error.getDescription());
		} else {
			librarian = new Librarian(itemId);
			librarian.setFirstName(firstName);
			librarian.setLastName(lastName);
			if(libraryId != null) {
				librarian.setLibrary(new Library(itemId));
			}
			librarian.updateMe();
		}
		
		model.addAttribute("itemId", librarian.getId());
		return "redirect:" + Constants.path_LibrarianHome;
	}
	
	public static Error testPersonParameters(String firstName, String lastName) {
		if(firstName == null || firstName.isEmpty()) {
			return Error.Person_emptyFN;
		}
		for(int i = 0; i < firstName.length(); i++) {
			if((firstName.charAt(i) < 'a' || firstName.charAt(i) > 'z') && (firstName.charAt(i) < 'A' || firstName.charAt(i) > 'Z')) {
				return Error.Person_onlyLatinLetterFN;
			}
			if(i != 0 && Character.isUpperCase(firstName.charAt(i))){
				return Error.Person_notFirstCharacterFN;
			}
		}
		if(!Character.isUpperCase(firstName.charAt(0))) {
			return Error.Person_firstCharacterFN;
		}
		
		if(lastName == null || lastName.isEmpty()) {
			return Error.Person_emptyLN;
		}
		for(int i = 0; i < lastName.length(); i++) {
			if((lastName.charAt(i) < 'a' || lastName.charAt(i) > 'z') && (lastName.charAt(i) < 'A' || lastName.charAt(i) > 'Z')) {
				return Error.Person_onlyLatinLetterLN;
			}
			if(i != 0 && Character.isUpperCase(lastName.charAt(i))){
				return Error.Person_notFirstCharacterLN;
			}
		}
		if(!Character.isUpperCase(lastName.charAt(0))) {
			return Error.Person_firstCharacterLN;
		}
		
		if(firstName.equals("Homer") && lastName.equals("Simpson")) {
			return Error.Person_crtoonHero;
		}
		return Error.NoError;
	}
	
	@PostMapping(path = Constants.path_LibrarianDelete)
	private static String deleteLibrarianPost(@RequestParam Integer itemId) {
		Librarian librarian = new Librarian(itemId);
		librarian.deleteMe();
		return "redirect:" + Constants.path_LibrarianList;
	}
	
	@PostMapping(path = Constants.path_LibrarianCreateLibrary)
	private String createLibraryPost(Model model,
			@RequestParam Integer librarianId,
			@RequestParam String name, 
			@RequestParam String city) {
		Error error = LibraryController.testLibraryParameters(name, city);
		Library lib = null;
		if(error != Error.NoError) {
			System.out.println("[ERROR] " + error.getDescription());
		} else {
			lib = new Library(null, city, name);
			Librarian librarian = new Librarian(librarianId);
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.getTransaction().begin();
			try {
				lib.updateMe(session);
				librarian.setLibrary(lib);
				librarian.updateMe(session);
			} catch (Throwable th) {
				session.getTransaction().rollback();
				th.printStackTrace();
			}
			if(session.getTransaction().isActive()) {
				session.getTransaction().commit();
			}
		}
		model.addAttribute("itemId", lib.getId());
		return "redirect:" + Constants.path_LibraryHome;
	}
	
	@GetMapping(path = Constants.path_LibrarianSelectLibrary)
	public String selectLibraryGet(Model model, @RequestParam Integer itemId) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.getTransaction().begin();
		List<Library> libs = session.createQuery("FROM Library lib WHERE lib.id not in (SELECT library.id FROM Librarian)", Library.class).list();
		session.getTransaction().commit();
		
		List<InputHidden> hiddens = new ArrayList<InputHidden>();
		hiddens.add(new InputHidden("librarianId", itemId));
		ListPopup popup = new ListPopup(hiddens, Constants.path_LibrarianSelectLibrary, ListPopup.select); 
		ListPage page = new ListPage(
				LibraryController.generateList(libs, false),
				null,
				popup,
				"Select new library", "post");
		page.addAttributes(model);
		
		return ListPage.getFile();
	}
	@PostMapping(path = Constants.path_LibrarianSelectLibrary)
	public String selectLibraryPost(Model model, @RequestParam() Integer itemId, @RequestParam Integer librarianId) {
		System.out.println(itemId + " " + librarianId);
		Librarian librarian = new Librarian(librarianId);
		Library lib = new Library(itemId);
		
		System.out.println(itemId + " " + lib.getName());
		
		librarian.setLibrary(lib);
		librarian.updateMe();
		
		model.addAttribute("itemId", librarianId);
		return "redirect:" + Constants.path_LibrarianHome;
	}
}
