import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { PlayerService } from '../player.service';
import { NgForm } from '@angular/forms';
import { Player } from '../player';

@Component({
	selector: 'app-login',
	templateUrl: './login.component.html',
	styleUrls: ['./login.component.css']
})

export class LoginComponent implements OnInit {

	currentPlayer: Player = null;

	constructor(private playerService: PlayerService) { }

	ngOnInit() {
		// TODO: Add loading overlay animation before service call is completed (in the service)
		this.playerService.getCurrentPlayer()
			.subscribe(player => this.currentPlayer = player);
	}

	onSubmit(loginForm: NgForm) {
		this.playerService.login(this.getFormData(loginForm))
			.subscribe(player => {
				this.currentPlayer = player;
				window.location.reload();
			});
	}

	logout() {
		this.playerService.logout()
			.subscribe(() => {
				this.currentPlayer = null;
				window.location.reload();
			});
	}

	createNewPlayer(loginForm: NgForm) {
		const newPlayer = this.getFormData(loginForm);

		this.playerService.createPlayer(newPlayer)
			.subscribe(() => {
				this.playerService.login(newPlayer)
					.subscribe(player => {
						this.currentPlayer = player;
						window.location.reload();
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
}
