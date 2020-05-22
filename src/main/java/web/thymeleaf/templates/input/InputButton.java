package web.thymeleaf.templates.input;

public class InputButton implements Input {
	public enum JSID{
		LibraryOpen("update-library-popup-open"),
		LibrarianOpen("update-librarian-popup-open"),
		BookOpen("update-book-popup-open"),
		AuthorOpen("update-author-popup-open"),
		CustomerOpen("update-customer-popup-open"),
		OperationOpen("update-operation-popup-open"),
		DeleteOpen("delete-popup-open");
		private String id;
		private JSID(String id) {
			this.id = id;
		}
		public String getId() {
			return id;
		}
	}
	private String name;
	private JSID jsId;
	
	public InputButton(String name, JSID jsId) {
		this.name = name;
		this.jsId = jsId;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public void setJsId(JSID jsId) {
		this.jsId = jsId;
	}
	public String getName() {
		return name;
	}
	public String getJsId() {
		return jsId.getId();
	}
	
	public String getType() {
		return InputType.Button.toString();
	}
	
}
