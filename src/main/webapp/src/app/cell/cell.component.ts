import { Component, OnInit, Input } from '@angular/core';

@Component({
	selector: 'app-cell',
	templateUrl: './cell.component.html',
	styleUrls: ['./cell.component.css']
})

export class CellComponent implements OnInit {
	@Input() cellData;

	constructor() { }

	ngOnInit() {

	}

	getCssClass() {
		const ship = this.parseJSON(this.cellData);
		if (Object.prototype.hasOwnProperty.call(ship, 'type')) {
			return 'ship ship-' + ship.type.replace(/\s/, '-').toLowerCase() + ' ' + this.getOrientation(ship);
		}

		return this.cellData;
	}

	getOrientation(ship) {
		return ship.locations[0].charAt(0) === ship.locations[1].charAt(0) ? 'horizontal' : 'vertical';
	}

	parseJSON(json) {
		try {
			return JSON.parse(json);
		} catch (e) {
			return '';
		}
	}
}
