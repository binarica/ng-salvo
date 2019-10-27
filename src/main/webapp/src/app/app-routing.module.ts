import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { PlayerListComponent } from './player-list/player-list.component';
import { GameComponent } from './game/game.component';

const routes: Routes = [
	{ path: '', component: PlayerListComponent },
	{ path: 'games', component: PlayerListComponent },
	{ path: 'game', component: GameComponent}
];

@NgModule({
	imports: [RouterModule.forRoot(routes)],
	exports: [RouterModule]
})

export class AppRoutingModule { }