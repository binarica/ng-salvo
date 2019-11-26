import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { GameService } from '../game.service';
import { Game } from '../game';

@Component({
	selector: 'app-game',
	templateUrl: './game.component.html',
	styleUrls: ['./game.component.css']
})

export class GameComponent implements OnInit {

	game: Game = null;
	gameInfo: string;

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
				this.gameInfo = game.gamePlayers
					.sort(gamePlayer => gamePlayer.id === id ? -1 : 1)
					.map(gamePlayer => gamePlayer.player.email)
					.join(' (you) vs ');
			});
	}
}
