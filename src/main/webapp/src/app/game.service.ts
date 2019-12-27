import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Game } from './game';
import { Ship } from './ship';

import { environment } from '../environments/environment';

@Injectable({
	providedIn: 'root'
})

export class GameService {

	constructor(private http: HttpClient) { }

	getGames(): Observable<object> {
		return this.http.get<object>(`${environment.apiUrl}/games`);
	}

	createGame(): Observable<object> {
		return this.http.post(`${environment.apiUrl}/games`, {});
	}

	joinGame(gameId: number): Observable<object> {
		return this.http.post(`${environment.apiUrl}/games/${gameId}/players`, {});
	}

	getGame(gamePlayerId: number): Observable<Game> {
		return this.http.get<Game>(`${environment.apiUrl}/game_view/${gamePlayerId}`);
	}

	addShips(gamePlayerId: number, ships: Ship[]): Observable<object> {
		return this.http.post(`${environment.apiUrl}/games/players/${gamePlayerId}/ships`, ships);
	}

	addSalvo(gamePlayerId: number, shots: string[]): Observable<object> {
		return this.http.post(`${environment.apiUrl}/games/players/${gamePlayerId}/salvoes`, shots);
	}
}
