let updateAuthorPopup = document.getElementById("update-author-popup"),
	updateAuthorPopupOpen = document.getElementById("update-author-popup-open");
	
if(updateAuthorPopupOpen != null && updateAuthorPopup != null){
	console.log("open init")
	updateAuthorPopupOpen.onclick = function(){
		updateAuthorPopup.style.display = "block";
	};
	window.addEventListener('click', function(event){
		if(event.target == updateAuthorPopup){
			updateAuthorPopup.style.display = "none";
		}
	});
}