$(document).ready(() => {
	$.ajax({
		url: "/api/games"
	}).done((data) => {
		renderList(data);
	});

	function getItemHtml(game) {
		let date = new Date(game.created);
		let emails = game.gamePlayers.map((gamePlayer) => gamePlayer.player.email).join(", ");

		return "<li>" + date.toLocaleString() + ": " + emails + "</li>";
	}

	function getListHtml(data) {
		return data.map(getItemHtml).join("");
	}

	function renderList(data) {
		let html = getListHtml(data);
		document.getElementById("games").innerHTML = html;
	}
});

