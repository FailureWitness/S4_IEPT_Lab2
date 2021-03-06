package web.thymeleaf.templates.popup;

import java.util.ArrayList;
import java.util.List;

import hibernate.tables.Author;
import web.spring.controller.Constants;
import web.thymeleaf.templates.input.Input;
import web.thymeleaf.templates.input.InputHidden;
import web.thymeleaf.templates.input.InputSubmit;
import web.thymeleaf.templates.input.InputText;

public class UpdateAuthorPopup implements UpdatePopup{
	private static final String 
				popupFile = "updateAuthorPopup.js", 
				testFile = "testAuthorParameters.js", 
				popupId = "update-author-popup", 
				formId = "update-author-form";
	
	private String title, formAction, error;
	private List<InputHidden> optionalList;
	private Author author;
	
	public UpdateAuthorPopup(Author author,
			String title, String formAction, String error,
			List<InputHidden> optionalList) {
		this.author = author;
		this.title = title;
		this.formAction = Constants.path_prefix + formAction;
		this.error = error;
		this.optionalList = optionalList;
	}
	public UpdateAuthorPopup(String title, String formAction, String error,
			List<InputHidden> optionalList) {
		this(new Author(null, "", "", null), title, formAction, error, optionalList);
	}
	
	public List<Input> getInputs() {
		List<Input> list = new ArrayList<Input>();
		
		list.add(new InputText("firstName", author.getFirstName(), "First Name"));
		list.add(new InputText("lastName", author.getLastName(), "Last Name"));
		
		list.add(new InputSubmit("Save"));
		if(author.getId() == null) {
			list.add(new InputHidden("itemId", 0));
		} else {
			list.add(new InputHidden("itemId", author.getId()));
		}
		if(optionalList != null) {
			list.addAll(optionalList);
		}
		
		return list;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getFormAction() {
		return formAction;
	}
	public void setFormAction(String formAction) {
		this.formAction = Constants.path_prefix + formAction;
	}
	public List<InputHidden> getOptionalList() {
		return optionalList;
	}
	public void setOptionalList(List<InputHidden> optionalList) {
		this.optionalList = optionalList;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}

	public String getPopupId() {
		return popupId;
	}
	public String getFormId() {
		return formId;
	}
	public String getPopupFile() {
		return popupFile;
	}
	public String getTestFile() {
		return testFile;
	}

}
