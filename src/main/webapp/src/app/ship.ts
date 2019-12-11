export class Ship {
	type: string;
	locations: string[];

	constructor(type: string, locations: string[]) {
		this.type = type;
		this.locations = locations;
	}
}
