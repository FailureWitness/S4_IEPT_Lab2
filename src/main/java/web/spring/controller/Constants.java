package web.spring.controller;

public abstract class Constants {
	public static final String	// paths 
		path_prefix = "/S4_IEPT_Lab2",
		
		path_mainView = "/",
		path_help = "/help",
		
		path_LibraryList = "/Library/List",
		path_LibraryHome = "/Library/Home",
		path_LibraryUpdate = "/Library/Update",
		path_LibraryDelete = "/Library/Delete",
		path_LibraryCreateLibrarian = "/Library/CreateLibrarian",
		path_LibraryCreateBook = "/Library/CreateBook",
		
		path_LibrarianList = "/Librarian/List",
		path_LibrarianHome = "/Librarian/Home",
		path_LibrarianUpdate = "/Librarian/Update",
		path_LibrarianDelete = "/Librarian/Delete",
		path_LibrarianCreateLibrary = "/Librarian/CrateLibrary",
		path_LibrarianSelectLibrary = "/Librarian/SelectLibrary",
		
		path_BookList = "/Book/List",
		path_BookHome = "/Book/Home",
		path_BookUpdate = "/Book/Update",
		path_BookDelete = "/Book/Delete",
		path_BookCrateLibrary = "/Book/CreateLibrary",
		path_BookSelectLibrary = "/Book/SelectLibrary",
		path_BookCreateAuthor = "/Book/CreateAuthor",
		path_BookSelectAuthor = "/Book/SelectAuthor",
		path_BookRemoveAuthor = "/Book/RemoveAuthor",
		peth_BookCreateOperation = "/Book/CreateOperation",
		
		path_AuthorList = "/Author/List",
		path_AuthorHome = "/Author/Home",
		path_AuthorUpdate = "/Author/Update",
		path_AuthorDelete = "/Author/Delete",
		path_AuthorSelectBook = "/Author/SelectBook",
		path_AuthorRemoveBook = "/Author/RemoveBook",
		
		path_CustomerList = "/Customer/List",
		path_CustomerHome = "/Customer/Home",
		path_CustomerUpdate = "/Customer/Update",
		path_CustomerDelete = "/Customer/Delete",
		path_CustomerCreateOperation = "/Customer/CreateOperation",
		
		path_OperationList = "/Operation/List",
		path_OperationHome = "/Operation/Home",
		path_OperationUpdate = "/Operation/Update",
		path_OperationDelete = "/Operation/Delete";
	
//	public static final String	// files
//		file_MainView = "mainView",
//		file_listTemplate = "list",
//		file_homeTemplate = "itemHome",
//		file_updateTemplate = "update",
//		file_helpFile = "manual";
	
	public enum Error{
		NoError("No error"),
		
		Library_emptyName("Name must not be empty"),
		Library_nameLength("Name must not be longer than 30 charcters"),
		Library_emptyCity("City must not be empty"),
		Library_cityLength("City must not be longer than 20 charcters"),
		Library_haveLibrarian("This library already have librarian. If you create new librarian, system will delete old one"),
		
		Person_emptyFN("First name must not be empty"),
		Person_emptyLN("Last name must not be empty"),
		Person_lengthFN("First name must not be longer than 20 characters"),
		Person_lengthLN("Last name must not be longer than 20 characters"),
		Person_firstCharacterFN("First character of first name must be uppercase"),
		Person_notFirstCharacterFN("Not first characters of first name must be lowercase"),
		Person_firstCharacterLN("First character of last name must be uppercase"),
		Person_notFirstCharacterLN("Not first characters of last name must be lowercase"),
		Person_onlyLatinLetterFN("First name can contain latin lettes only"),
		Person_onlyLatinLetterLN("Last name can contain latin lettes only"),
		Person_crtoonHero("We do not work with cartoon heros..."), // Easter egg :3
		
		Book_emptyName("Name must not be empty"),
		Book_nameLength("Name must not be longer than 30 characters"),
		Book_emptyCount("Count must not be empty"),
		Book_countLength("Count must be lower than 2147483648"),
		Book_negativeCount("Count must be grater than 0"),
		Book_countStartFromZero("Count must not starts from 0"),
		Book_onlyDigitsCount("Count can contain digits only"),
		Book_countLessUnreturned("Count must not be less than unreturned count");
		private Integer Id;
		private String description;
		
		static {
			Integer id = 0;
			for(Error e: values()) {
				e.Id = id ++;
			}
		}
		
		Error(String description){
			this.description = description;
		}
		public Integer getId() {
			return Id;
		}
		public String getDescription() {
			return description;
		}
		public static Error getById(Integer id) {
			for(Error e: values()) {
				if(e.getId() == id) {
					return e;
				}
			}
			return null;
		}
	}
}
