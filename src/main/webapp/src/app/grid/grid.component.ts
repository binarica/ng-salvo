import { Component, ElementRef, Input, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ToastrService } from 'ngx-toastr';

import { GameService } from '../game.service';

import { Ship } from '../ship';

const GRID_SIZE = 10;

@Component({
	selector: 'app-grid',
	templateUrl: './grid.component.html',
	styleUrls: ['./grid.component.css']
})

export class GridComponent implements OnInit {

	@Input() ships = [];
	@Input() salvoes = [];

	rows = [];
	cols = [];

	grid = new Map();

	constructor(
		private route: ActivatedRoute,
		private elementRef: ElementRef,
		private gameService: GameService,
		private toastrService: ToastrService
	) {
		for (let i = 0; i < GRID_SIZE; i++) {
			this.rows.push(String.fromCharCode('A'.charCodeAt(0) + i));
			this.cols.push(1 + i);
		}
	}

	ngOnInit() {
		this.renderShips();
		this.renderSalvoes();
	}

	renderShips() {
		for (const ship of this.ships) {
			this.grid.set(ship.locations[0], JSON.stringify(ship));
		}
	}

	renderSalvoes() {
		/*
		for (const salvo of this.salvoes) {
			for (const location of salvo.locations) {
				this.grid.set(location, 'salvo');
			}
		}
		*/
	}

	addShip(ship, location) {
		const shipLocations = [];

		for (let i = 0; i < ship.length; i++) {
			let row = location.charAt(0);
			let col = parseInt(location.substr(1), 10);
			switch (ship.orientation as string) {
				case 'horizontal':
					col += i;
					break;
				case 'vertical':
					row = String.fromCharCode(row.charCodeAt(0) + i);
					break;
			}

			shipLocations.push(row + col);
		}

		this.ships.push(new Ship(ship.type, shipLocations));
		this.renderShips();
	}

	onDragEnter(ev) {
		ev.currentTarget.classList.add('busy');
	}

	onDragLeave(ev) {
		ev.currentTarget.classList.remove('busy');
	}

	onDragOver(ev) {
		ev.preventDefault();
	}

	onDrop(ev) {
		ev.preventDefault();
		ev.currentTarget.classList.remove('busy');

		try {
			const ship = JSON.parse(ev.dataTransfer.getData('text/plain'));
			const location = ev.currentTarget.getAttribute('data-location');
			this.addShip(ship, location);
		} catch (e) {
			this.toastrService.error('Invalid placement');
		}
	}

	resetGrid() {
		this.ships = [];
		this.grid.clear();
	}

	submitShips() {
		const gamePlayerId = +this.route.snapshot.paramMap.get('gp');
		this.gameService.addShips(gamePlayerId, this.ships)
			.subscribe(() => {
				location.reload();
			});
	}

	getLocation(row, col) {
		return row + col;
	}
}
