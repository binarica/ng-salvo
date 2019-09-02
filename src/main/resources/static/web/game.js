document.addEventListener("DOMContentLoaded", () => {
	const urlParams = new URLSearchParams(window.location.search);
	const gamePlayerId = urlParams.get('gp');
	const url = "/api/game_view/" + gamePlayerId;

	const rows = [ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 ];
	const columns = [ "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",];

	let players = [];
	let shipLocations = [];

	fetch(url)
		.then((response) => response.json())
		.then((data) => renderTable(data));

	function getHeadersHtml() {
		return "<tr><th></th>" + rows.map((row) => (
			"<th>" + row + "</th>"
		)).join("") + "</tr>";
	}

	function renderHeaders() {
		document.querySelector("#table-headers").innerHTML = getHeadersHtml();
	}

	function getColumnsHtml(currentColumn) {
		return rows.map((currentRow) => {
			let currentCell = currentColumn + currentRow;
			return shipLocations.includes(currentCell)
				? "<td class='ship'></td>"
				: "<td></td>";
		}).join("")
	}
	
	function getRowsHtml() {
		return rows.map((currentRow, i) => {
			let currentColumn = columns[i];
			return "<tr><th>" + currentColumn + "</th>" +
				getColumnsHtml(currentColumn) + "</tr>";
		}).join("");
	}

	function renderRows() {
		document.querySelector("#table-rows").innerHTML = getRowsHtml();
	}

	function renderTable(game) {
		if (game.gamePlayers) {
			players = game.gamePlayers
				.sort((gamePlayer) => (
					gamePlayerId == gamePlayer.id ? -1 : 1
				))
				.map((gamePlayer) => (
					gamePlayerId == gamePlayer.id
						? gamePlayer.player.email.concat(" (you)")
						: gamePlayer.player.email
				)).join(" vs ");

			document.querySelector("#players").innerHTML = players;
		}

		if (game.ships && game.ships.length) {
			shipLocations = game.ships.map((ship) => (
				ship.locations
			)).reduce((result, current) => (
				result.concat(current))
			);
		}

		renderHeaders();
		renderRows();
	}
});