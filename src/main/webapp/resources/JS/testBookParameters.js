let updateBookForm = document.getElementById("update-book-form");

if(updateBookForm != null){
	console.log("init");
	updateBookForm.onsubmit = function(event){
		let name = updateBookForm.elements["name"].value,
			count = updateBookForm.elements["count"].value,
			error = updateBookForm.children.namedItem("error");
		
		let nameLengthTest = testFieldLength(name, 30, "Name");
		if(!nameLengthTest.result){
			error.innerHTML = nameLengthTest.error;
			return false;
		}
		
		if(count.length == 0){
			error.innerHTML = "Count must not be empty";
			return false;
		}
		if(count <= 0){
			error.innerHTML = "Count must be grater than 0";
			return false;
		}
		if(count > 2147483648){
			error.innerHTML = "Count must be lower than 2147483648";
			return false;
		}
		
		return true;
	}
}