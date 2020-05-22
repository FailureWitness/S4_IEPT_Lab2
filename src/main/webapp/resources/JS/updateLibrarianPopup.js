let updateLibrarianPopup = document.getElementById("update-librarian-popup"),
	updateLibrarianPopupOpen = document.getElementById("update-librarian-popup-open");
	
if(updateLibrarianPopupOpen != null && updateLibrarianPopup != null){
	updateLibrarianPopupOpen.onclick = function(){
		updateLibrarianPopup.style.display = "block";
	};
	window.addEventListener('click', function(event){
		if(event.target == updateLibrarianPopup){
			updateLibrarianPopup.style.display = "none";
		}
	});
}