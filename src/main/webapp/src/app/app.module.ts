import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';

import { AppComponent } from './app.component';
import { PlayerListComponent } from './player-list/player-list.component';
import { AppRoutingModule } from './app-routing.module';
import { HttpClientModule } from '@angular/common/http';
import { PlayerService } from './player.service';
import { GameComponent } from './game/game.component';
import { GridComponent } from './grid/grid.component';
import { CellComponent } from './cell/cell.component';
import { LoginComponent } from './login/login.component';
import { FormsModule } from '@angular/forms';
import { GamesComponent } from './games/games.component';

@NgModule({
	declarations: [
		AppComponent,
		PlayerListComponent,
		GameComponent,
		GridComponent,
		CellComponent,
		LoginComponent,
		GamesComponent
	],
	imports: [
		AppRoutingModule,
		BrowserModule,
		FormsModule,
		HttpClientModule,
		NgbModule
	],
	providers: [PlayerService],
	bootstrap: [AppComponent]
	})

export class AppModule { }
