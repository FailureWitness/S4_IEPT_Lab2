package web.thymeleaf.templates.basic;

import java.util.List;

import web.thymeleaf.templates.input.Input;
import web.thymeleaf.templates.input.Input.InputType;
import web.thymeleaf.templates.input.InputHidden;

public class ListItem {
	public enum CssClass{
		CreateButton("button create-form"),
		ListItem("list-form");
		
		private String name;
		CssClass(String name){
			this.name = name;
		}
		public String getName() {
			return name;
		}
	}
	private List<InputHidden> hiddenList;
	private Input input;
	private CssClass cssClass;
	
	public ListItem(Input input, List<InputHidden> hiddenList, CssClass cssClass) {
		this.input = input;
		this.hiddenList = hiddenList;
		this.cssClass = cssClass;
	}
	public List<InputHidden> getHiddenList() {
		return hiddenList;
	}
	public void setHiddenList(List<InputHidden> hiddenList) {
		this.hiddenList = hiddenList;
	}
	public CssClass getCssClass() {
		return cssClass;
	}
	public void setCssClass(CssClass cssClass) {
		this.cssClass = cssClass;
	}
	public Input getInput() {
		return input;
	}
	public void setInput(Input input) {
		this.input = input;
	}
}
