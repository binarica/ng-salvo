import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { Player } from './player';

import { environment } from '../environments/environment';

@Injectable({
	providedIn: 'root'
})

export class PlayerService {

	constructor(private http: HttpClient) { }

	getCurrentPlayer(): Observable<any> {
		return this.http.get<any>(`${environment.apiUrl}/games`)
		.pipe(map(data => data.player));
	}

	createPlayer(data: FormData): Observable<Player> {
		return this.http.post<Player>(`${environment.apiUrl}/players`, data);
	}
}
