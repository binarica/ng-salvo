import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { GameService } from '../game.service';
import { Game } from '../game';
import { Player } from '../player';

@Component({
	selector: 'app-games',
	templateUrl: './games.component.html',
	styleUrls: ['./games.component.css']
})

export class GamesComponent implements OnInit {

	games: Game[] = [];
	player: Player;

	constructor(
		private router: Router,
		private gameService: GameService
	) {}

	ngOnInit() {
		this.getGames();
	}

	getGames() {
		this.gameService.getGames()
			.subscribe(data => {
				this.player = data['player'];
				this.games = data['games'];
			});
	}

	createGame() {
		this.gameService.createGame()
			.subscribe(gpid => {
				this.redirectToGamePage(gpid);
			});
	}

	joinGame(gameId: number) {
		this.gameService.joinGame(gameId)
			.subscribe(gpid => {
				this.redirectToGamePage(gpid);
			});
	}

	reenterGame(game) {
		const gpid = game.gamePlayers.find(gamePlayer => gamePlayer.player.id === this.player.id).id;
		this.redirectToGamePage(gpid);
	}

	redirectToGamePage(gpid) {
		this.router
			.navigate(['/game', { gp: gpid }])
			.then();
	}

	redirectToHomePage() {
		this.router
			.navigate(['/'])
			.then();
	}

	hasJoined(game) {
		return this.player != null && (game.gamePlayers.some(gamePlayer => gamePlayer.player.id === this.player.id));
	}

	isGameFull(game) {
		return game != null && (game.players.length > 1);
	}

	getItemHtml(game) {
		const date = new Date(game.created)
			.toLocaleString();

		const players = game.gamePlayers
			.sort((a, b) => a.id - b.id)
			.map((gamePlayer) => gamePlayer.player.email)
			.join(', ');

		return `${date}: ${players}`;
	}
}
