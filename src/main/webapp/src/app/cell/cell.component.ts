import { Component, OnInit, Input } from '@angular/core';

@Component({
	selector: 'app-cell',
	templateUrl: './cell.component.html',
	styleUrls: ['./cell.component.css']
})

export class CellComponent implements OnInit {
	@Input() shipData;

	constructor() { }

	ngOnInit() {

	}

	getCssClass() {
		return 'ship-'.concat(this.parseJSON(this.shipData).type.replace(/\s/, '-').toLowerCase());
	}

	getOrientation() {
		const ship = this.parseJSON(this.shipData);
		return ship.locations[0].charAt(0) === ship.locations[1].charAt(0) ? 'horizontal' : 'vertical';
	}

	parseJSON(json) {
		try {
			return JSON.parse(this.shipData);
		} catch (e) {
			return '';
		}
	}
}
