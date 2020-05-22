let updateBookPopup = document.getElementById("update-book-popup"),
	updateBookPopupOpen = document.getElementById("update-book-popup-open");

if(updateBookPopupOpen != null && updateBookPopup != null){
	updateBookPopupOpen.onclick = function(){
		updateBookPopup.style.display = "block";
	};
	window.addEventListener('click', function(event){
		if(event.target == updateBookPopup){
			updateBookPopup.style.display = "none";
		}
	});
}