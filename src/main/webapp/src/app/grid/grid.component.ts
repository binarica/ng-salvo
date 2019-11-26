import { Component, Input, OnInit } from '@angular/core';

@Component({
	selector: 'app-grid',
	templateUrl: './grid.component.html',
	styleUrls: ['./grid.component.css']
})

export class GridComponent implements OnInit {

	@Input() ships = [];
	@Input() salvoes = [];

	rows = [ 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J' ];
	cols = [ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 ];

	grid = new Map();

	constructor() { }

	ngOnInit() {
		this.addShips();
		this.addSalvoes();
	}

	addShips() {
		for (const ship of this.ships) {
			const name = 'ship-'.concat(ship.type.replace(/\s/g, '-').toLowerCase());
			for (const location of ship.locations) {
				this.grid.set(location, name);
			}
		}
	}

	addSalvoes() {
		for (const salvo of this.salvoes) {
			for (const location of salvo.locations) {
				this.grid.set(location, 'salvo');
			}
		}
	}
}
