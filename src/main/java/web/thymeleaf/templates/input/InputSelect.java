package web.thymeleaf.templates.input;

import java.util.List;

public class InputSelect implements Input {
	private String text;
	private String name;
	private List<SelectOption> options;
	public InputSelect(String text, String name, List<SelectOption> options) {
		this.text = text;
		this.name = name;
		this.options = options;
	}
	
	public String getType() {
		return InputType.Select.toString();
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<SelectOption> getOptions() {
		return options;
	}
	public void setOptions(List<SelectOption> options) {
		this.options = options;
	}
}
