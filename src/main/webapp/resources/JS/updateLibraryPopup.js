let updateLibraryPopup = document.getElementById("update-library-popup"),
	updateLibraryPopupOpen = document.getElementById("update-library-popup-open");

if(updateLibraryPopupOpen != null && updateLibraryPopup != null){
	updateLibraryPopupOpen.onclick = function(){
		updateLibraryPopup.style.display = "block";
	};
	window.addEventListener('click', function(event){
		if(event.target == updateLibraryPopup){
			updateLibraryPopup.style.display = "none";
		}
	});
}