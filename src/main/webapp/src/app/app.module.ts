import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ToastrModule } from 'ngx-toastr';

import { AppComponent } from './app.component';
import { PlayerListComponent } from './player-list/player-list.component';
import { AppRoutingModule } from './app-routing.module';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { HttpErrorInterceptor } from './http-error.interceptor';
import { PlayerService } from './player.service';
import { GameComponent } from './game/game.component';
import { GridComponent } from './grid/grid.component';
import { CellComponent } from './cell/cell.component';
import { LoginComponent } from './login/login.component';
import { FormsModule } from '@angular/forms';
import { GamesComponent } from './games/games.component';
import { ShipListComponent } from './ship-list/ship-list.component';

@NgModule({
	declarations: [
		AppComponent,
		PlayerListComponent,
		GameComponent,
		GridComponent,
		CellComponent,
		LoginComponent,
		GamesComponent,
		ShipListComponent
	],
	imports: [
		AppRoutingModule,
		BrowserModule,
		FormsModule,
		HttpClientModule,
		NgbModule,
		BrowserAnimationsModule,
		ToastrModule.forRoot({
			positionClass: 'toast-top-center',
			preventDuplicates: true
		})
	],
	providers: [
		PlayerService,
		{
			provide: HTTP_INTERCEPTORS,
			useClass: HttpErrorInterceptor,
			multi: true
		}],
	bootstrap: [AppComponent]
	})

export class AppModule { }
