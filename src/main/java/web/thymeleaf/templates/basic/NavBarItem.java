package web.thymeleaf.templates.basic;

import web.spring.controller.Constants;

public class NavBarItem {
	private String name;
	private String href;
	public NavBarItem(String name, String href) {
		this.name = name;
		this.href = Constants.path_prefix + href;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getHref() {
		return href;
	}
	public void setHref(String href) {
		this.href = Constants.path_prefix + href;
	}
}
