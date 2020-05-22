package web.thymeleaf.templates.popup;

import java.util.ArrayList;
import java.util.List;

import hibernate.tables.Customer;
import web.spring.controller.Constants;
import web.thymeleaf.templates.input.Input;
import web.thymeleaf.templates.input.InputHidden;
import web.thymeleaf.templates.input.InputSubmit;
import web.thymeleaf.templates.input.InputText;

public class UpdateCustomerPopup implements UpdatePopup {
	private static final String popupFile = "updateCustomerPopup.js",
								testFile = "testCustomerParameters.js",
								popupId = "update-customer-popup",
								formId = "update-customer-form";
	
	private String title, formAction, error;
	private List<InputHidden> optionalList;
	private Customer customer;
	
	public UpdateCustomerPopup(Customer customer, 
			String title, String formAction, String error, 
			List<InputHidden> optionalList) {
		this.customer = customer;
		this.title = title;
		this.formAction = Constants.path_prefix + formAction;
		this.error = error;
		this.optionalList = optionalList;
	}
	public UpdateCustomerPopup(String title, String formAction, String error, 
			List<InputHidden> optionalList) {
		this(new Customer(null, "", ""), title, formAction, error, optionalList);
	}
	
	public List<Input> getInputs() {
		List<Input> list = new ArrayList<Input>();
		
		list.add(new InputText("firstName", customer.getFirstName(), "First name"));
		list.add(new InputText("lastName", customer.getLastName(), "Last name"));
		list.add(new InputSubmit("Save"));
		if(customer.getId() == null) {
			list.add(new InputHidden("itemId", 0));
		} else {
			list.add(new InputHidden("itemId", customer.getId()));
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
