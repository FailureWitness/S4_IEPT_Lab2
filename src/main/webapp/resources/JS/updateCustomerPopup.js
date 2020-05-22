let updateCustomerPopup = document.getElementById("update-customer-popup"),
	updateCustomerPopupOpen = document.getElementById("update-customer-popup-open");

if(updateCustomerPopupOpen != null && updateCustomerPopup != null){
	updateCustomerPopupOpen.onclick = function(){
		updateCustomerPopup.style.display = "block";
	};
	window.addEventListener('click', function(event){
		if(event.target == updateCustomerPopup){
			updateCustomerPopup.style.display = "none";
		}
	});
}