package web.thymeleaf.templates.input;

public class InputSubmit implements Input{
	private String value;
	
	public InputSubmit(String value) {
		this.value = value;
	}
	
	public String getType() {
		return InputType.Submit.toString();
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
