package web.thymeleaf.templates.popup;

import java.util.ArrayList;
import java.util.List;


import hibernate.tables.Library;
import web.spring.controller.Constants;
import web.thymeleaf.templates.input.Input;
import web.thymeleaf.templates.input.InputHidden;
import web.thymeleaf.templates.input.InputSubmit;
import web.thymeleaf.templates.input.InputText;

public class UpdateLibraryPopup implements UpdatePopup{
	private static final String popupFile = "updateLibraryPopup.js",
			testFile = "testLibraryParameters.js",
			formId = "update-library-form",
			popupId = "update-library-popup";
	
	private Library lib;
	private String formAction, title, error;
	private List<InputHidden> optionalList;
	public UpdateLibraryPopup(Library lib, String formAction, String title, List<InputHidden> optionalList, String error) {
		this.lib = lib;
		this.formAction = Constants.path_prefix + formAction;
		this.title = title;
		this.optionalList = optionalList;
		this.error = error;
	}
	public UpdateLibraryPopup(String formAction, String title, List<InputHidden> optionalList, String error) {
		this(new Library(null, "", ""), formAction, title, optionalList, error);
	}
	public List<Input> getInputs() {
		List<Input> list = new ArrayList<Input>();
		
		list.add(new InputText("name", lib.getName(), "Name"));
		list.add(new InputText("city", lib.getCity(), "City"));
		list.add(new InputSubmit("Save"));
		
		if(lib.getId() != null) {
			list.add(new InputHidden("itemId", lib.getId().toString()));
		} else {
			list.add(new InputHidden("itemId", "0"));
		}
		
		if(optionalList != null) {
			list.addAll(optionalList);
		}
		
		return list;
	}
	public void setFormAction(String formAction) {
		this.formAction = formAction;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTitle() {
		return title;
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
	public void setError(String error) {
		this.error = error;
	}
	public String getError() {
		return error;
	}
	
	public String getTestFile() {
		return testFile;
	}
	public String getPopupFile() {
		return popupFile;
	}
	public List<InputHidden> getOptionalList() {
		return optionalList;
	}
	public void setOptionalList(List<InputHidden> optionalList) {
		this.optionalList = optionalList;
	}
}
