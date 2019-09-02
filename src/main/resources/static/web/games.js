document.addEventListener("DOMContentLoaded", () => {
	fetch("/api/games")
		.then((response) => response.json())
		.then((data) => renderList(data));

	function getItemHtml(game) {
		let date = new Date(game.created);
		let players = game.gamePlayers.map((gamePlayer) => gamePlayer.player.email).join(", ");

		return "<li>" + date.toLocaleString() + ": " + players + "</li>";
	}

	function getListHtml(data) {
		return data.map(getItemHtml).join("");
	}

	function renderList(data) {
		document.querySelector("#games").innerHTML = getListHtml(data);
	}
});