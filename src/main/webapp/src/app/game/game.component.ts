import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { GameService } from '../game.service';
import { Game } from '../game';
import { Ship } from '../ship';
import { Salvo } from '../salvo';

@Component({
	selector: 'app-game',
	templateUrl: './game.component.html',
	styleUrls: ['./game.component.css']
})

export class GameComponent implements OnInit {

	game: Game = null;
	players: string[] = [];
	ships: Ship[];
	playerSalvoes: Salvo[];
	opponentSalvoes: Salvo[];
	donePlacingShips = false;
	readyToShoot = true;

	constructor(
		private route: ActivatedRoute,
		private gameService: GameService
	) {}

	ngOnInit() {
		this.getGame();
	}

	getGame() {
		const id = +this.route.snapshot.paramMap.get('gp');

		this.gameService.getGame(id)
			.subscribe(game => {
				this.game = game;

				const gamePlayers = game.gamePlayers.sort(gamePlayer => gamePlayer.id === id ? -1 : 1);

				this.players = gamePlayers.map(gamePlayer => gamePlayer.player.email);

				this.ships = game.ships;

				const currentPlayer = gamePlayers[0];

				this.playerSalvoes = game.salvoes.filter(salvo => salvo.player === currentPlayer.player.id);
				this.opponentSalvoes = game.salvoes.filter(salvo => salvo.player !== currentPlayer.player.id);

				this.donePlacingShips = game.ships.length !== 0;
			});
	}
}
