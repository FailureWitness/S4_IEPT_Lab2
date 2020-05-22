package web.spring.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import hibernate.tables.Operation;
import hibernate.util.HibernateUtil;
import web.thymeleaf.page.ItemHome;
import web.thymeleaf.page.ListPage;
import web.thymeleaf.templates.basic.ListItem;
import web.thymeleaf.templates.basic.ListItem.CssClass;
import web.thymeleaf.templates.input.Input;
import web.thymeleaf.templates.input.InputButton;
import web.thymeleaf.templates.input.InputSubmit;
import web.thymeleaf.templates.popup.DeletePopup;
import web.thymeleaf.templates.input.InputButton.JSID;
import web.thymeleaf.templates.input.InputHidden;

@Controller
public class OperationController {
	private enum HomeAction{
		MarkReturned("mark as returned"),
		MarkUnreturned("mark as unreturned"),
		Book("book"),
		Customer("customer"),
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
	
	public static List<ListItem> generateList(List<Operation> operations, Boolean canCreate){
		List<ListItem> list = new ArrayList<ListItem>();
		if(canCreate) {
			List<InputHidden> hiddenList = new ArrayList<InputHidden>();
			hiddenList.add(new InputHidden("itemId", 0));
			InputButton button = new InputButton("Create operation", JSID.OperationOpen);
			list.add(new ListItem(button, hiddenList, CssClass.CreateButton));
		}
		for(Operation operation: operations) {
			List<InputHidden> hiddenList = new ArrayList<InputHidden>();
			hiddenList.add(new InputHidden("itemId", operation.getId()));
			Date date = new Date(operation.getDate());
			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyy");
			InputSubmit submit = new InputSubmit(
							"[" + sdf.format(date) + "] " 
							+ operation.getBook().getName() + " | "
							+ operation.getCustomer().getFirstName() + " " + operation.getCustomer().getLastName() + " | "
							+ (operation.isReturned() ? "RETURNED" : "NOT RETURNED"));
			list.add(new ListItem(submit, hiddenList, CssClass.ListItem));
		}
		return list;
	}
	private static List<InputHidden> generateDescription(Operation operation){
		List<InputHidden> list = new ArrayList<InputHidden>();
		
		list.add(new InputHidden("ID", operation.getId()));
		list.add(new InputHidden("Date", new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date(operation.getDate()))));
		list.add(new InputHidden("Returned", operation.isReturned() ? "YES" : "NO"));
		list.add(new InputHidden("Book", operation.getBook().getName()));
		list.add(new InputHidden("Customer", operation.getCustomer().getFirstName() + " " + operation.getCustomer().getLastName()));
		
		return list;
	}
	private static List<Input> generateHomeControllList(Operation operation){
		List<Input> list = new ArrayList<Input>();
		
		for(HomeAction action: HomeAction.values()) {
			if(action == HomeAction.MarkReturned && operation.isReturned()) {
				continue;
			}
			if(action == HomeAction.MarkUnreturned && !operation.isReturned()) {
				continue;
			}
			list.add(action.getInput());
		}
		list.add(new InputHidden("itemId", operation.getId()));
		
		return list;
	}
	
	@GetMapping(path = Constants.path_OperationList)
	public String operationListGet(Model model) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.getTransaction().begin();
		List<Operation> operations = session.createQuery("FROM Operation", Operation.class).list();
		session.getTransaction().commit();
		
		ListPage page = new ListPage(generateList(operations, false), null, Constants.path_OperationHome, "List of operations", "get");
		page.addAttributes(model);
		
		return ListPage.getFile();
	}
	
	@GetMapping(path = Constants.path_OperationHome)
	public String operationHomeGet(Model model, @RequestParam Integer itemId) {
		Operation operation = new Operation(itemId);
		ItemHome home = new ItemHome(generateDescription(operation), 
				generateHomeControllList(operation),
				null,
				new DeletePopup("Are you sure to delete operation?", Constants.path_OperationDelete, itemId),
				Constants.path_OperationHome, "Operation home");
		home.addAttributes(model);
		
		return ItemHome.getFile();
	}
	@PostMapping(path = Constants.path_OperationHome)
	public String operationHomePost(Model model, @RequestParam Integer itemId, @RequestParam String action) {
		switch(HomeAction.getAction(action)){
		case Delete:
			System.out.println("[ERROR] Impossible action (Operation, operationHomePost)");
			return null;
		case Book:
			model.addAttribute("itemId", new Operation(itemId).getBook().getId());
			return "redirect:" + Constants.path_BookHome;
		case Customer:
			model.addAttribute("itemId", new Operation(itemId).getCustomer().getId());
			return "redirect:" + Constants.path_CustomerHome;
		case MarkReturned:
			return changeStatus(model, new Operation(itemId), true);
		case MarkUnreturned:
			return changeStatus(model, new Operation(itemId), false);
		}
		return null;
	}
	
	private String changeStatus(Model model, Operation operation, Boolean status) {
		operation.setIsReturned(status);
		operation.updateMe();
		model.addAttribute("itemId", operation.getId());
		return "redirect:" + Constants.path_OperationHome;
	}
	
	@PostMapping(path = Constants.path_OperationDelete)
	public String deleteOperation(Integer itemId) {
		Operation operation = new Operation(itemId);
		operation.deleteMe();
		return "redirect:" + Constants.path_OperationList;
	}
}
