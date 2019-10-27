import { Component, OnInit } from '@angular/core';
import { PlayerService } from '../player.service';
import { Player } from '../player';

@Component({
	selector: 'app-player-list',
	templateUrl: './player-list.component.html',
	styleUrls: ['./player-list.component.css']
})

export class PlayerListComponent implements OnInit {

	gamePlayers: any = [];
	players: Player[];

	constructor(private playerService: PlayerService) { }

	ngOnInit() {
		this.playerService.getGamesData().subscribe(games => {
			this.gamePlayers = games.flatMap(game => game.gamePlayers);
			this.players = this.getPlayersList();
		});
	}

	getPlayersList(): Player[] {
		const getPlayer = gamePlayer => gamePlayer.player;

		const uniquePlayer = (currentPlayer, index, players) => (
			index === players.findIndex(player => player.id === currentPlayer.id)
		);

		const createPlayerInstance = player => (
			new Player(
				player.id, 
				player.email,
				this.getPlayerScores(player)
			)
		);

		return this.gamePlayers
		.map(getPlayer)
		.filter(uniquePlayer)
		.map(createPlayerInstance);
	}

	getPlayerScores(player: Player): number[] {
		return this.gamePlayers
		.filter(gamePlayer => gamePlayer.player.id === player.id && gamePlayer.score !== null)
		.map(gamePlayer => gamePlayer.score);
	}
}