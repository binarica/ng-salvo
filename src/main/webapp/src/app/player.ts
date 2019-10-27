import { Optional } from '@angular/core';

export class Player {
	id: number;
	userName: string;
	password: string;
	total?: number = 0;
	won?: number = 0;
	lost?: number = 0;
	tied?: number = 0;

	constructor(id: number, userName: string, @Optional() scores: number[]) {
		this.id = id;
		this.userName = userName;
		this.total = scores.reduce((a, b) => a + b);

		for (let score of scores) {
			switch (score) {
				case 1:
					this.won++;
					break;
				case 0.5:
					this.tied++;
					break;
				case 0:
					this.lost++;
			}
		}
	}
}