export class Game {
	id: number;
	date: string;
	gamePlayers: any;
	ships?: any;
	salvoes?: any;

	constructor(id: number, date: string, gamePlayers: any) {
		this.id = id;
		this.date = date;
		this.gamePlayers = gamePlayers;
	}
}
