import { Component, OnInit } from '@angular/core';
import { GameService } from '../game.service';
import { Game } from '../game';

@Component({
	selector: 'app-grid',
	templateUrl: './grid.component.html',
	styleUrls: ['./grid.component.css']
})

export class GridComponent implements OnInit {

	game: Game;

	rows = [ "A", "B", "C", "D", "E", "F", "G", "H", "I", "J"];
	cols = [ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 ];

	constructor(private gameService: GameService) { }

	ngOnInit() {
		this.gameService.getData().subscribe(data => {
			this.game = data;
		});
	}
}