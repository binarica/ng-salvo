import { Component, OnInit } from '@angular/core';
import { GameService } from '../game.service';
import { Game } from '../game';
import { Grid } from '../grid';

@Component({
	selector: 'app-game',
	templateUrl: './game.component.html',
	styleUrls: ['./game.component.css'],
	providers: [GameService]
})

export class GameComponent implements OnInit {

	game: Game = null;
	grids: Grid[];

	players: string[] = [];

	constructor(private gameService: GameService) { }

	ngOnInit() {
		this.gameService.getData().subscribe(data => {
			this.game = data;
			const gamePlayerId = this.gameService.getGamePlayerId();
			
			this.players = this.game.gamePlayers
			.sort(gamePlayer => gamePlayer.id == gamePlayerId ? -1 : 1)
			.map(gamePlayer => gamePlayer.player.email);

			const salvoes = this.game.salvoes;
			
			salvoes.forEach(salvo => {
				// TODO: Add ships and salvoes to grids
			});
		});
	}
}