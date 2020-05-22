package web.thymeleaf.templates.popup;

import java.util.List;

import web.spring.controller.Constants;
import web.thymeleaf.templates.input.Input;
import web.thymeleaf.templates.input.InputHidden;

public interface UpdatePopup {
	public enum PopupType{
		Library("update-library-popup"),
		Librarian("update-librarian-popup"),
		Book("update-book-popup"),
		Author("update-author-popup"),
		Customer("update-customer-popup"),
		Operation("update-operation-popup");
		private String popupId, popupOpenId;
		private PopupType(String popupId) {
			this.popupId = popupId;
			this.popupOpenId = popupOpenId + "-open";
		}
		public String getPopupId() {
			return popupId;
		}
		public String getPopupOpenId() {
			return popupOpenId;
		}
	}
	public List<Input> getInputs();
	public String getTitle();
	public void setTitle(String title);
	public String getFormAction();
	public void setFormAction(String formAction);
	public List<InputHidden> getOptionalList();
	public void setOptionalList(List<InputHidden> optionalList);
	public String getError();
	public void setError(String error);
	
	public String getPopupId();
	public String getFormId();
	public String getPopupFile();
	public String getTestFile();
}
