let listPopup = document.getElementById("list-popup"),
	listPopupMessage = document.getElementById("list-popup-message"),
	listPopupHidden = document.getElementById("list-popup-dynamic-hidden");

//let pForm = document.getElementById("list-popup-form");
//pForm.onsubmit = function(event){
//	console.log(listPopupHidden.name + " " + listPopupHidden.value);
//	return false;
//}

if(listPopup != null) {
	window.addEventListener('click', function(event){
		if(event.target == listPopup){
			listPopup.style.display = "none";
		}
	});
}

for(let i = 0; i < document.forms.length; i++){
	let formId = "list-item-" + i;
	let form = document.getElementById(formId);
	if(form != null && form.className == "list-form"){
		let listItemId = form.elements["itemId"];
		form.onsubmit = function(event){
			listPopupMessage.innerHTML = form.elements[0].value;
			listPopupHidden.value = listItemId.value
			listPopup.style.display = 'block';
			
			return false;
		}
	}
}