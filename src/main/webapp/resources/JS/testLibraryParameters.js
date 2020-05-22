let updateLibraryForm = document.getElementById("update-library-form");

if(updateLibraryForm != null){
	updateLibraryForm.onsubmit = function(event){
		let name = updateLibraryForm.elements["name"].value,
			city = updateLibraryForm.elements["city"].value,
			error = updateLibraryForm.children.namedItem("error");
		
		let nameLengthTest = testFieldLength(name, 30, "Name");
		if(!nameLengthTest.result){
			error.innerHTML = nameLengthTest.error;
			return false;
		}
		
		let cityLengthTest = testFieldLength(name, 20, "City");
		if(!cityLengthTest.result){
			error.innerHTML = cityLengthTest.error;
			return false;
		}
		
		return true;
	}
}