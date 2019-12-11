import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { AuthService } from '../auth.service';
import { PlayerService } from '../player.service';
import { Player } from '../player';

@Component({
	selector: 'app-login',
	templateUrl: './login.component.html',
	styleUrls: ['./login.component.css']
})

export class LoginComponent implements OnInit {

	currentPlayer: Player = null;

	constructor(private authService: AuthService, private playerService: PlayerService, private modalService: NgbModal) { }

	ngOnInit() {
		// TODO: Add loading overlay animation before service call is completed (in the service)
		this.playerService.getCurrentPlayer()
			.subscribe(player => this.currentPlayer = player);
	}

	openModal(content) {
		this.modalService.open(content, { centered: true });
	}

	onSubmit(loginForm: NgForm) {
		this.authService.login(this.getFormData(loginForm))
			.subscribe(player => {
				this.currentPlayer = player;
				this.refresh();
			});
	}

	logout() {
		this.authService.logout()
			.subscribe(() => {
				this.currentPlayer = null;
				this.refresh();
			});
	}

	createNewPlayer(loginForm: NgForm) {
		const newPlayer = this.getFormData(loginForm);

		this.playerService.createPlayer(newPlayer)
			.subscribe(() => {
				this.authService.login(newPlayer)
					.subscribe(player => {
						this.currentPlayer = player;
						this.refresh();
					});
			});
	}

	isLoggedIn() {
		return this.currentPlayer != null;
	}

	getFormData(form: NgForm) {
		const formData = new FormData();
		Object.keys(form.value).forEach(key => formData.append(key, form.value[key]));
		return formData;
	}

	refresh() {
		location.reload();
	}
}
