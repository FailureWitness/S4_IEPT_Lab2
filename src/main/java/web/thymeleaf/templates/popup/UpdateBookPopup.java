package web.thymeleaf.templates.popup;

import java.util.ArrayList;
import java.util.List;

import hibernate.tables.Book;
import web.spring.controller.Constants;
import web.thymeleaf.templates.input.Input;
import web.thymeleaf.templates.input.InputHidden;
import web.thymeleaf.templates.input.InputNumber;
import web.thymeleaf.templates.input.InputSubmit;
import web.thymeleaf.templates.input.InputText;

public class UpdateBookPopup implements UpdatePopup{
	private static final String popupFile = "updateBookPopup.js", 
			testFile = "testBookParameters.js",
			formId = "update-book-form",
			popupId = "update-book-popup";
	
	private Book book;
	private String title, formAction, error;
	private List<InputHidden> optionalList;
	
	public UpdateBookPopup(Book book, 
			String title, String formAction, String error, 
			List<InputHidden> optionalList) {
		this.book = book;
		this.title = title;
		this.formAction = Constants.path_prefix + formAction;
		this.error = error;
		this.optionalList = optionalList;
	}
	public UpdateBookPopup(String title, String formAction, String error, 
			List<InputHidden> optionalList) {
		this(new Book(null, "", 0, null, null), title, formAction, error, optionalList);
	}
	
	public List<Input> getInputs() {
		List<Input> list = new ArrayList<Input>();
		
		list.add(new InputText("name", book.getName(), "Name"));
		list.add(new InputNumber("count", book.getCount(), "Count"));
		
		list.add(new InputSubmit("Save"));
		
		if(book.getId() == null) {
			list.add(new InputHidden("itemId", "0"));
		} else {
			list.add(new InputHidden("itemId", book.getId().toString()));
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
