let deletePopup = document.getElementById("delete-popup"),
	deletePopupOpen = document.getElementById("delete-popup-open"),
	deletePopupClose = document.getElementById("delete-popup-close");

if(deletePopupOpen != null && deletePopup != null){
	deletePopupOpen.onclick = function(){
		deletePopup.style.display = "block";
	};
	window.addEventListener('click', function(event){
		if(event.target == deletePopup){
			deletePopup.style.display = "none";
		}
	});
	if(deletePopupClose != null){
		deletePopupClose.onclick = function(){
			deletePopup.style.display = "none";
		}
	}
}

function testFieldLength(fieldValue, maxLength, fieldName){
	let result = {
			error: "",
			result: false
	};
	if(fieldValue.length == 0){
		result.error = fieldName + " must not be empty";
		return result;
	}
	if(fieldValue.length > maxLength){
		result.error = fieldName + " must not be longer than " + maxLength + " characters";
		return result;
	}
	result.result = true;
	return result;
}
function testPersonFullName(firstName, lastName){
	let result = {
			error:"",
			result:false
		};
	let firstNameLengthTest = testFieldLength(firstName, 20, "First name");
	if(!firstNameLengthTest.result){
		return firstNameLengthTest;
	}
	if(firstName.charAt(0) < 'A' || firstName.charAt(0) > 'Z'){
		result.error = "First name must starts from uppercase latin letter";
		return result;
	}
	for(var i = 1; i < firstName.length; i++){
		if(firstName.charAt(i) < 'a' || firstName.charAt(i) > 'z'){
			result.error = "Not first characters of first name must be lowercase latin letters";
			return result;
		}
	}
	
	let lastNameLengthTest = testFieldLength(lastName, 20, "Last name");
	if(!lastNameLengthTest.result){
		return lastNameLengthTest;
	}
	if(lastName.charAt(0) < 'A' || lastName.charAt(0) > 'Z'){
		result.error = "Last name must starts from uppercase latin letter";
		return result;
	}
	for(var i = 1; i < lastName.length; i++){
		if(lastName.charAt(i) < 'a' || lastName.charAt(i) > 'z'){
			result.error = "Not first characters of last name must be lowercase latin letters";
			return result;
		}
	}
	
	if(firstName === "Homer" && lastName === "Simpson"){
		result.error = "We don't work with cartoon heros...";
		return result;
	}
	result.result = true;
	return result; 
}