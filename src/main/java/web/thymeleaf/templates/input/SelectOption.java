package web.thymeleaf.templates.input;

public class SelectOption extends InputHidden{
	private Boolean isSelected;
	public SelectOption(String name, String value, Boolean isSelected) {
		super(name, value);
		this.isSelected = isSelected;
	}
	
	public Boolean IsSelected() {
		return isSelected;
	}
	public void setIsSelected(Boolean isSelected) {
		this.isSelected = isSelected;
	}
	@Override
	public String getType() {
		return null;
	}
}
