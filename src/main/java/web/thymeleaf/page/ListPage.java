package web.thymeleaf.page;

import java.util.HashSet;
import java.util.List;

import org.springframework.ui.Model;

import web.spring.controller.Constants;
import web.spring.controller.MainController;
import web.thymeleaf.templates.basic.ListItem;
import web.thymeleaf.templates.popup.ListPopup;
import web.thymeleaf.templates.popup.UpdatePopup;

public class ListPage {
	private static final String file = "list";
	private List<ListItem> items;
	private UpdatePopup updatePopup;
	private String title, formAction, method;
	private ListPopup listPopup;
	
	public ListPage(List<ListItem> items, 
			UpdatePopup updatePopup, 
			String formAction, String title, String method) {
		this.items = items;
		this.updatePopup = updatePopup;
		this.formAction = Constants.path_prefix + formAction;
		this.title = title;
		this.method = method;
		listPopup = null;
	}
	public ListPage(List<ListItem> items, 
			UpdatePopup updatePopup,
			ListPopup listPopup,
			String title, String method) {
		this.items = items;
		this.updatePopup = updatePopup;
		this.title = title;
		this.method = method;
		this.listPopup = listPopup;
		formAction = listPopup.getFormAction();
	}
	

	
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getFormAction() {
		return formAction;
	}
	public void setFormAction(String formAction) {
		this.formAction = Constants.path_prefix + formAction;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public UpdatePopup getUpdatePopup() {
		return updatePopup;
	}
	public void setUpdatePopup(UpdatePopup updatePopup) {
		this.updatePopup = updatePopup;
	}
	public List<ListItem> getItems() {
		return items;
	}
	public void setItems(List<ListItem> items) {
		this.items = items;
	}
	public ListPopup getListPopup() {
		return listPopup;
	}
	public void setListPopup(ListPopup listPopup) {
		this.listPopup = listPopup;
	}
	
	
	public void addAttributes(Model model) {
		MainController.setStandartTemplates(model);
		HashSet<String> jsLinks = new HashSet<String>();
		if(updatePopup != null) {
			jsLinks.add(updatePopup.getPopupFile());
			jsLinks.add(updatePopup.getTestFile());
		}
		if(listPopup != null) {
			listPopup.setPopupAttributes(model);
			jsLinks.add(ListPopup.getFile());
		}
		model.addAttribute("formAction", formAction);
		model.addAttribute("list", items);
		model.addAttribute("title", title);
		model.addAttribute("method", method);
		model.addAttribute("updatePopup", updatePopup);
		model.addAttribute("jsLinks", jsLinks);
	}
	public static String getFile() {
		return file;
	}
}
