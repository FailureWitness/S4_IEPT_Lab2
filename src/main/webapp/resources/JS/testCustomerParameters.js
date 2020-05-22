let updateCustomerForm = document.getElementById("update-customer-form");

if(updateCustomerForm != null){
	updateCustomerForm.onsubmit = function(event){
		let firstName = updateCustomerForm .elements["firstName"].value,
			lastName = updateCustomerForm .elements["lastName"].value,
			error = updateCustomerForm.children.namedItem("error");
		
		let result = testPersonFullName(firstName, lastName);
		if(!result.result){
			error.innerHTML = result.error
		}
		return result.result;
	}
}