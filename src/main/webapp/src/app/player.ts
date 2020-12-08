import { Optional } from '@angular/core';

export class Player {
	id: number;
	email: string;
	total ? = 0;
	won ? = 0;
	lost ? = 0;
	tied ? = 0;

	constructor(id: number, email: string, @Optional() scores: number[]) {
		this.id = id;
		this.email = email;
		this.total = scores.reduce((a, b) => a + b);

		for (const score of scores) {
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
