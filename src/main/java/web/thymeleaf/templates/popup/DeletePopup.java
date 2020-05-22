package web.thymeleaf.templates.popup;

import web.spring.controller.Constants;

public class DeletePopup {
	private String warningText;
	private String formAction;
	private Integer ownerId;
	private static final String htmlId = "delete-popup";
	
	public DeletePopup(String warningText, String formAction, Integer ownerId) {
		this.formAction = Constants.path_prefix + formAction;
		this.warningText = warningText;
		this.ownerId = ownerId;
	}
	
	public String getWarningText() {
		return warningText;
	}
	public void setWarningText(String warningText) {
		this.warningText = warningText;
	}
	public String getFormAction() {
		return formAction;
	}
	public void setFormAction(String formAction) {
		this.formAction = formAction;
	}
	public Integer getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(Integer ownerId) {
		this.ownerId = ownerId;
	}
	public static String getHtmlid() {
		return htmlId;
	}
}
