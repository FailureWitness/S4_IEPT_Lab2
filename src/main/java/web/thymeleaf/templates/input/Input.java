package web.thymeleaf.templates.input;

public interface Input {
	public enum InputType{
		Text("text"),
		Submit("submit"),
		Hidden("hidden"),
		Select("select"),
		Button("button"),
		TextNumber("number");
		private String name;
		private InputType(String name) {
			this.name = name;
		}
		public String getName() {
			return name;
		}
	}
	public String getType();
}
