package web.spring.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import web.thymeleaf.page.HelpPage;
import web.thymeleaf.page.MainPage;
import web.thymeleaf.templates.basic.NavBarItem;
import web.thymeleaf.templates.input.Input;

@Controller
public class MainController {
	
	public static List<NavBarItem> getNavBar(){
		List<NavBarItem> result = new ArrayList<NavBarItem>();
		result.add(new NavBarItem("Home", Constants.path_mainView));
		result.add(new NavBarItem("Libraries", Constants.path_LibraryList));
		result.add(new NavBarItem("Librarians", Constants.path_LibrarianList));
		result.add(new NavBarItem("Books", Constants.path_BookList));
		result.add(new NavBarItem("Authors", Constants.path_AuthorList));
		result.add(new NavBarItem("Customaers", Constants.path_CustomerList));
		result.add(new NavBarItem("Operations", Constants.path_OperationList));
		result.add(new NavBarItem("Help", Constants.path_help));
		return result;
	}
	public static void setStandartTemplates(Model model) {
		model.addAttribute("navBar", getNavBar());
		model.addAttribute("footer", "-- Semester 4. IE&PT Lab2. Made by Evgeny Vasyliev K-25 --");
		
		for(Input.InputType type : Input.InputType.values()) {
			model.addAttribute(type.getName(), type.toString());
		}
	}
	
	@GetMapping(path = Constants.path_mainView)
	public String mainViewGet(Model model) {
		MainPage page = new MainPage();
		page.addAttributes(model);
		return MainPage.file;
	}
	
	@GetMapping(path = Constants.path_help)
	public String manualGet(Model model) {
		HelpPage page = new HelpPage();
		page.addAttributes(model);
		return HelpPage.file;
	}
}
