package web.thymeleaf.page;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.ui.Model;

import web.spring.controller.Constants;
import web.spring.controller.MainController;
import web.thymeleaf.templates.input.Input;
import web.thymeleaf.templates.input.InputHidden;
import web.thymeleaf.templates.popup.DeletePopup;
import web.thymeleaf.templates.popup.UpdatePopup;

public class ItemHome {
	private static final String file = "itemHome";
	private List<InputHidden> describeList;
	private List<Input> controlButtons;
	private List<UpdatePopup> updatePopupList;
	private DeletePopup deletePopup;
	private String formAction;
	private String title;
	
	public ItemHome(
			List<InputHidden> describeList,	List<Input> controlButtons,
			List<UpdatePopup> updatePopupList, DeletePopup deletePopup,
			String formAction, String title) {
		this.describeList = describeList;
		this.controlButtons = controlButtons;
		this.updatePopupList = updatePopupList;
		this.deletePopup = deletePopup;
		this.formAction = Constants.path_prefix + formAction;
		this.title = title;
	}
	
	public List<InputHidden> getDescribeList() {
		return describeList;
	}
	public void setDescribeList(List<InputHidden> describeList) {
		this.describeList = describeList;
	}
	public List<Input> getControlButtons() {
		return controlButtons;
	}
	public void setControlButtons(List<Input> controlButtons) {
		this.controlButtons = controlButtons;
	}
	public List<UpdatePopup> getUpdatePopupList() {
		return updatePopupList;
	}
	public void setUpdatePopupList(List<UpdatePopup> updatePopupList) {
		this.updatePopupList = updatePopupList;
	}
	public DeletePopup getDeletePopup() {
		return deletePopup;
	}
	public void setDeletePopup(DeletePopup deletePopup) {
		this.deletePopup = deletePopup;
	}
	public void setFormAction(String formAction) {
		this.formAction = Constants.path_prefix + formAction;
	}
	public String getFormAction() {
		return formAction;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public void addAttributes(Model model) {
		MainController.setStandartTemplates(model);
		
		Set<String> jsLinks = new HashSet<String>();
		if(updatePopupList != null) {
			for(UpdatePopup ups: updatePopupList) {
				jsLinks.add(ups.getPopupFile());
				jsLinks.add(ups.getTestFile());
			}
		}
		
		model.addAttribute("descriptionList", describeList);
		model.addAttribute("formList", controlButtons);
		model.addAttribute("formAction", formAction);
		model.addAttribute("updatePopupList", updatePopupList);
		model.addAttribute("deletePopup", deletePopup);
		model.addAttribute("jsLinks", jsLinks);
		model.addAttribute("title", title);
	}
	public static String getFile() {
		return file;
	}
}
