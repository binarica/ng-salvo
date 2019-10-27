import { Component } from '@angular/core';
import { PlayerService } from '../player.service';
import { NgForm } from '@angular/forms';
import { Player } from '../player';

@Component({
	selector: 'app-login',
	templateUrl: './login.component.html',
	styleUrls: ['./login.component.css']
})

export class LoginComponent {

	currentPlayer: Player = null;
	submitted = false;

	constructor(private playerService: PlayerService) { }

	ngOnInit() {
		// TODO: Add loading overlay animation before service call is completed
		this.playerService.getCurrentPlayer()
		.subscribe(player => this.currentPlayer = player);
	}

	onSubmit(loginForm: NgForm) {
		this.submitted = true;

		this.playerService.login(this.getFormData(loginForm))
		.subscribe(_ => {
			this.playerService.getCurrentPlayer()
			.subscribe(player => this.currentPlayer = player);
		});
	}

	logout() {
		this.playerService.logout()
		.subscribe(_ => this.currentPlayer = null);
	}

	createNewPlayer(loginForm: NgForm) {
		const newPlayer = this.getFormData(loginForm);

		this.playerService.createPlayer(newPlayer)
		.subscribe(_ => {
			this.playerService.login(newPlayer)
			.subscribe(_ => {
				this.playerService.getCurrentPlayer()
				.subscribe(player => this.currentPlayer = player);
			});
		});
	}

	getFormData(form: NgForm) {
		const formData = new FormData();
		Object.keys(form.value).forEach(key => formData.append(key, form.value[key]));
		return formData;
	}
}
