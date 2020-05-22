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
import web.thymeleaf.templates.popup.UpdateCustomerPopup;
import web.thymeleaf.templates.popup.UpdatePopup;

@Controller
public class CustomerController {
	private enum HomeAction{
		Update("update", JSID.CustomerOpen),
		Books("books"),
		UnreturnedBooks("unreturned books"),
		Operations("operations"),
		NewOperation("new operation"),
		Delete("delete", JSID.DeleteOpen);
		private String name;
		private Input input;
		HomeAction(String name, JSID jsId){
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
	
	public static List<ListItem> generateList(List<Customer> customers, Boolean canCreate){
		List<ListItem> list = new ArrayList<ListItem>();
		if(canCreate) {
			List<InputHidden> hiddenList = new ArrayList<InputHidden>();
			hiddenList.add(new InputHidden("itemId", 0));
			InputButton button = new InputButton("Create customer", JSID.CustomerOpen);
			list.add(new ListItem(button, hiddenList, CssClass.CreateButton));
		}
		for(Customer customer: customers) {
			List<InputHidden> hiddenList = new ArrayList<InputHidden>();
			hiddenList.add(new InputHidden("itemId", customer.getId()));
			InputSubmit submit = new InputSubmit(customer.getFirstName() + " " + customer.getLastName());
			list.add(new ListItem(submit, hiddenList, CssClass.ListItem));
		}
		return list;
	}
	
	@GetMapping(path = Constants.path_CustomerList)
	public String customerListGet(Model model) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.getTransaction().begin();
		List<Customer> customers = session.createQuery("FROM Customer", Customer.class).list();
		session.getTransaction().commit();
		ListPage page = new ListPage(generateList(customers, true), 
				new UpdateCustomerPopup("Create", Constants.path_CustomerUpdate, "", null),
				Constants.path_CustomerHome, "List of customers", "get");
		page.addAttributes(model);
		
		return ListPage.getFile();
	}
	private static List<InputHidden> generateDescription(Customer customer) {
		List<InputHidden> list = new ArrayList<InputHidden>();
		
		list.add(new InputHidden("ID", customer.getId()));
		list.add(new InputHidden("First name", customer.getFirstName()));
		list.add(new InputHidden("Last name", customer.getLastName()));
		list.add(new InputHidden("Unreturned books count", customer.getUnreturnedBooksCount()));
		
		return list;
	}
	private static List<Input> generateHomeControlList(Customer customer){
		List<Input> list = new ArrayList<Input>();
		
		for(HomeAction action: HomeAction.values()) {
			if(action == HomeAction.UnreturnedBooks && customer.getUnreturnedBooksCount() == 0) {
				continue;
			}
			list.add(action.getInput());
		}
		list.add(new InputHidden("itemId", customer.getId()));
		
		return list;
	}
	private static List<UpdatePopup> generatePopups(Customer customer){
		List<UpdatePopup> list = new ArrayList<UpdatePopup>();
		
		list.add(new UpdateCustomerPopup(customer, "Update", Constants.path_CustomerUpdate, "", null));
		
		return list;
	}
	
	@GetMapping(path = Constants.path_CustomerHome)
	public String customerHomeGet(Model model, @RequestParam Integer itemId) {
		Customer customer = new Customer(itemId);
		ItemHome home = new ItemHome(generateDescription(customer),
				generateHomeControlList(customer),
				generatePopups(customer),
				new DeletePopup("Are you", Constants.path_CustomerDelete, itemId),
				Constants.path_CustomerHome, "Customer home");
		home.addAttributes(model);
		
		return ItemHome.getFile();
	}
	@PostMapping(path = Constants.path_CustomerHome)
	public String customerHomePost(Model model, @RequestParam Integer itemId, @RequestParam String action) {
		switch(HomeAction.getAction(action)) {
		case Delete:
		case Update:
			System.out.println("[ERROR] Imposible action (Customer, customerHomePost)");
			return null;
		case Books:
			return customerAllBooks(model, new Customer(itemId));
		case UnreturnedBooks:
			return customerUnreturnedBooks(model, new Customer(itemId));
		case Operations:
			return customerAllOperations(model, new Customer(itemId));
		case NewOperation:
			return customerCreateOperation(model, itemId);
		}
		return null;
	}
	
	@PostMapping(path = Constants.path_CustomerUpdate)
	public String customerUpdatePost(Model model, @RequestParam Integer itemId, @RequestParam String firstName, @RequestParam String lastName) {
		Customer customer = null;
		Error error = LibrarianController.testPersonParameters(firstName, lastName);
		if(error != Error.NoError) {
			System.out.println("[ERROR] " + error.getDescription());
		} else {
			if(itemId == 0) {
				customer = new Customer(null, "", "");
			} else {
				customer = new Customer(itemId);
			}
			customer.setFirstName(firstName);
			customer.setLastName(lastName);
			customer.updateMe();
		}
		model.addAttribute("itemId", customer.getId());
		return "redirect:" + Constants.path_CustomerHome;
	}
	@PostMapping(path = Constants.path_CustomerDelete)
	public String deleteCustomer(@RequestParam Integer itemId) {
		Customer customer = new Customer(itemId);
		customer.deleteMe();
		return "redirect:" + Constants.path_CustomerList;
	}
	
	public String customerAllBooks(Model model, Customer customer) {
		List<Book> books = customer.getBooks();
		ListPage page = new ListPage(BookController.generateList(books, false),
				null,
				Constants.path_BookHome, "List of books of customer '" + customer.getFirstName() + " " + customer.getLastName() + "'", "get");
		page.addAttributes(model);
		return ListPage.getFile();
	}
	
	public String customerUnreturnedBooks(Model model, Customer customer) {
		List<Book> books = customer.getUnreturnedBooks();
		ListPage page = new ListPage(BookController.generateList(books, false),
				null,
				Constants.path_BookHome, "List of books of customer '" + customer.getFirstName() + " " + customer.getLastName() + "'", "get");
		page.addAttributes(model);
		return ListPage.getFile();
	}
	
	private String customerAllOperations(Model model, Customer customer) {
		List<Operation> operations = customer.getOperations();
		ListPage page = new ListPage(OperationController.generateList(operations, false), 
				null,
				Constants.path_OperationHome, 
				"List of operations of customer '" + customer.getFirstName() + " " + customer.getLastName() + "'", 
				"get");
		page.addAttributes(model);
		
		return ListPage.getFile();
	}
	private String customerCreateOperation(Model model, Integer itemId) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.getTransaction().begin();
		List<Book> books = session.createQuery("FROM Book", Book.class).list();
		session.getTransaction().commit();
		
		List<InputHidden> hiddens = new ArrayList<InputHidden>();
		hiddens.add(new InputHidden("customerId", itemId));
		ListPage page = new ListPage(BookController.generateList(books, false), 
				null,
				new ListPopup(hiddens, Constants.path_CustomerCreateOperation, ListPopup.select),
				"Select book for operation", "post");
		page.addAttributes(model);
		return ListPage.getFile();
	}
	
	@PostMapping(path = Constants.path_CustomerCreateOperation)
	public String customerCreateOptionPost(Model model, Integer itemId, Integer customerId) {
		Operation operation = new Operation(null, new Customer(customerId), new Book(itemId), System.currentTimeMillis(), false);
		operation.updateMe();
		model.addAttribute("itemId", operation.getId());
		return "redirect:" + Constants.path_OperationHome;
	}
}
