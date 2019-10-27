import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { Game } from './game';
import { catchError } from 'rxjs/operators';

@Injectable({
	providedIn: 'root'
})

export class GameService {

	// TODO: Get Game Player ID from current Player when joining a game
	gamePlayerId: number = 1;

	constructor(private http: HttpClient) { }

	getData(): Observable<Game> {
		return this.http.get<Game>(`/api/game_view/${this.gamePlayerId}`)
		.pipe(catchError(response => throwError(response.error.error)));
	}

	getGamePlayerId(): number {
		return this.gamePlayerId;
	}
}
