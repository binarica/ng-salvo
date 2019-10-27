import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { Player } from './player';
import { catchError, map } from 'rxjs/operators';

@Injectable({
	providedIn: 'root'
})

export class PlayerService {

	private serviceUrl = '/api';

	constructor(private http: HttpClient) { }

	getCurrentPlayer(): Observable<any> {
		return this.http.get<any>(`${this.serviceUrl}/games`)
		.pipe(map(data => {
			if (data != null) {
				return data.player;
			}
		}));
	}

	getGamesData(): Observable<any> {
		return this.http.get<any>(`${this.serviceUrl}/games`)
		.pipe(map(data => data.games));
	}

	login(data: FormData) {
		return this.http.post(`${this.serviceUrl}/login`, data, {
			withCredentials: true
		}).pipe(catchError(this.handleError));
	}

	logout() {
		return this.http.post(`${this.serviceUrl}/logout`, {
			withCredentials: true
		}).pipe(catchError(this.handleError));
	}

	createPlayer(data: FormData): Observable<Player> {
		return this.http.post<Player>(`${this.serviceUrl}/players`, data)
		.pipe(catchError(this.handleError));
	}

	handleError(response) {
		let errorMessage = '';
		if (response.error) {
			// TODO: Use NG Bootstrap alerts to handle server error messages
			errorMessage = `Error: ${response.error.error}\nError Code: ${response.status}`;
			window.alert(errorMessage);
		}
		return throwError(errorMessage);
	}
}
