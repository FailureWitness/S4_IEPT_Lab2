let updateAuthorForm = document.getElementById("update-author-form");

if(updateAuthorForm != null){
	console.log("test init");
	updateAuthorForm.onsubmit = function(event){
		let firstName = updateAuthorForm .elements["firstName"].value,
			lastName = updateAuthorForm .elements["lastName"].value,
			error = updateAuthorForm.children.namedItem("error");
		
		let result = testPersonFullName(firstName, lastName);
		if(!result.result){
			error.innerHTML = result.error
		}
		return result.result;
	}
}