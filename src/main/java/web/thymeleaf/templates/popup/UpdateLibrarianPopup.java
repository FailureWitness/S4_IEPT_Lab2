package web.thymeleaf.templates.popup;

import java.util.ArrayList;
import java.util.List;

import hibernate.tables.Librarian;
import web.spring.controller.Constants;
import web.thymeleaf.templates.input.Input;
import web.thymeleaf.templates.input.InputHidden;
import web.thymeleaf.templates.input.InputSubmit;
import web.thymeleaf.templates.input.InputText;

public class UpdateLibrarianPopup implements UpdatePopup {
	private static final String popupFile = "updateLibrarianPopup.js", 
			testFile = "testLibrarianParameters.js", 
			formId = "update-librarian-form", 
			popupId = "update-librarian-popup";
	private Librarian librarian;
	private String formAction, title, error;
	private List<InputHidden> optionalList;
	
	public UpdateLibrarianPopup(Librarian librarian, List<InputHidden> optionalList, String formAction, String title, String error){
		this.librarian = librarian;
		this.formAction = Constants.path_prefix + formAction;
		this.title = title;
		this.optionalList = optionalList;
		this.error = error;
	}
	public UpdateLibrarianPopup(List<InputHidden> optionalList, String formAction, String title, String error) {
		this(new Librarian(null, "", "", null), optionalList, formAction, title, error);
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public List<Input> getInputs() {
		List<Input> list = new ArrayList<Input>();
		
		list.add(new InputText("firstName", librarian.getFirstName(), "First name"));
		list.add(new InputText("lastName", librarian.getLastName(), "Last name"));
		list.add(new InputSubmit("Save"));
		
		if(librarian.getId() == null) {
			list.add(new InputHidden("itemId", "0"));
		} else {
			list.add(new InputHidden("itemId", librarian.getId().toString()));
		}
		
		if(optionalList != null) {
			list.addAll(optionalList);
		}
		
		return list;
	}
	public String getPopupId() {
		return popupId;
	}
	public String getFormId() {
		return formId;
	}
	public String getFormAction() {
		return formAction;
	}
	public void setFormAction(String formAction) {
		this.formAction = formAction;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getError() {
		return error;
	}
	
	public String getPopupFile() {
		return popupFile;
	}
	public String getTestFile() {
		return testFile;
	}
	public List<InputHidden> getOptionalList() {
		return optionalList;
	}
	public void setOptionalList(List<InputHidden> optionalList) {
		this.optionalList = optionalList;
	}
}
