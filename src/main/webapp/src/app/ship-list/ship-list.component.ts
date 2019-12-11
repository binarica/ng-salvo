import { Component, OnInit } from '@angular/core';

import { SHIP_TYPES } from '../ship-types';

@Component({
  selector: 'app-ship-list',
  templateUrl: './ship-list.component.html',
  styleUrls: ['./ship-list.component.css']
})

export class ShipListComponent implements OnInit {

	shipTypes = [];
	orientation: string;

	constructor() {
		this.shipTypes = SHIP_TYPES;
		this.orientation = 'horizontal';
	}

	ngOnInit() {
	}

	getClassName(shipType: string) {
		return 'ship-'.concat(shipType.replace(/\s/, '-').toLowerCase());
	}

	getDataAttr(data) {
		data.orientation = this.orientation;
		return JSON.stringify(data);
	}

	onDragStart(ev) {
		const el = ev.target;
		const data = el.getAttribute('data-ship-type');
		ev.dataTransfer.setData('text/plain', data);
	}
}
