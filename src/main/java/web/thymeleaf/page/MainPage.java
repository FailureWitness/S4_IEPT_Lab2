package web.thymeleaf.page;

import org.springframework.ui.Model;

import web.spring.controller.MainController;

public class MainPage {
	public static final String file = "mainView";
	
	public void addAttributes(Model model) {
		MainController.setStandartTemplates(model);
	}
}
