package web.thymeleaf.templates.input;

public class InputText extends InputHidden{
	private String text;
	
	public InputText(String name, String value, String text) {
		super(name, value);
		this.text = text;
	}
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	@Override
	public String getType() {
		return InputType.Text.toString();
	}

}
