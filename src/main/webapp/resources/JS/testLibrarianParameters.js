let updateLibrarianForm = document.getElementById("update-librarian-form");

if(updateLibrarianForm != null){
	updateLibrarianForm.onsubmit = function(event){
		let firstName = updateLibrarianForm .elements["firstName"].value,
			lastName = updateLibrarianForm .elements["lastName"].value,
			error = updateLibrarianForm.children.namedItem("error");
		
		let result = testPersonFullName(firstName, lastName);
		if(!result.result){
			error.innerHTML = result.error
		}
		return result.result;
	}
}