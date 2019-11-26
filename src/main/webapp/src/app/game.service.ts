import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { Game } from './game';
import { catchError } from 'rxjs/operators';

@Injectable({
	providedIn: 'root'
})

export class GameService {

	serviceUrl = '/api';

	constructor(private http: HttpClient) { }

	getGames(): Observable<any> {
		return this.http.get<any>(`${this.serviceUrl}/games`);
	}

	createGame(): Observable<any> {
		return this.http.post(`${this.serviceUrl}/games`, {})
			.pipe(catchError(this.handleError));
	}

	joinGame(gameId: number): Observable<any> {
		return this.http.post(`${this.serviceUrl}/games/${gameId}/players`, {})
			.pipe(catchError(this.handleError));
	}

	getGame(gpid: number): Observable<Game> {
		return this.http.get<Game>(`${this.serviceUrl}/game_view/${gpid}`)
			.pipe(catchError(this.handleError));
	}

	handleError(response) {
		let errorMessage = '';
		if (response.error) {
			errorMessage = `Error: ${response.error}\nError Code: ${response.status}`;
			window.alert(errorMessage);
		}
		return throwError(errorMessage);
	}
}
