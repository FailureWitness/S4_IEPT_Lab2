package web.thymeleaf.templates.popup;

import java.util.List;

import org.springframework.ui.Model;

import web.spring.controller.Constants;
import web.thymeleaf.templates.input.InputHidden;

public class ListPopup {
	private static final String file = "list.js";
	
	private List<InputHidden> hiddens;
	private String formAction;
	private Integer popupSettings;
	
	public static final Integer select = 1, delete = 2;
	
	public ListPopup(List<InputHidden> hiddens, String formAction, Integer popupSettings) {
		this.hiddens = hiddens;
		this.formAction = Constants.path_prefix + formAction;
		this.popupSettings = popupSettings;
	}
	
	public List<InputHidden> getHiddens() {
		return hiddens;
	}
	public void setHiddens(List<InputHidden> hiddens) {
		this.hiddens = hiddens;
	}
	public String getFormAction() {
		return formAction;
	}
	public void setFormAction(String formAction) {
		this.formAction = Constants.path_prefix + formAction;
	}
	public Integer getPopupSettings() {
		return popupSettings;
	}
	public void setPopupSettings(Integer popupSettings) {
		this.popupSettings = popupSettings;
	}
	public void setPopupAttributes(Model model) {
		model.addAttribute("listPopup", true);
		model.addAttribute("popupAction", formAction);
		model.addAttribute("listPopupHiddens", hiddens);
		
		if((popupSettings & select) != 0) {
			model.addAttribute("listPopupSelect", true);
		}
		if((popupSettings & delete) != 0) {
			model.addAttribute("listPopupDelete", true);
		}
	}
	
	public static String getFile() {
		return file;
	}
}
