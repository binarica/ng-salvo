import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { GameComponent } from './game/game.component';
import { GamesComponent } from './games/games.component';

const routes: Routes = [
	{ path: '', component: GamesComponent },
	{ path: 'games', component: GamesComponent },
	{ path: 'game', component: GameComponent}
];

@NgModule({
	imports: [RouterModule.forRoot(routes)],
	exports: [RouterModule]
})

export class AppRoutingModule { }
