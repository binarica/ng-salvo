import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { Observable } from 'rxjs';

import { Player } from './player';
import { environment } from '../environments/environment';


@Injectable({
  providedIn: 'root'
})

export class AuthService {

  constructor(private http: HttpClient) { }

	login(data: FormData): Observable<Player> {
		return this.http.post<Player>(`${environment.apiUrl}/login`, data, { withCredentials: true });
	}

	logout() {
		return this.http.post(`${environment.apiUrl}/logout`, { withCredentials: true });
	}
}
