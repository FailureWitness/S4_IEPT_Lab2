package web.thymeleaf.templates.input;

public class InputNumber implements Input{
	private String name;
	private Integer value;
	private String text;
	
	public InputNumber(String name, Integer value, String text) {
		this.name = name;
		this.value = value;
		this.text = text;
	}
	
	public String getType() {
		return InputType.TextNumber.toString();
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getValue() {
		return value;
	}
	public void setValue(Integer value) {
		this.value = value;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
}
