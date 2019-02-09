import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import Game from './models/Game';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  availableGames: Game[];
  activeGame: Game;

  constructor(private http: HttpClient) {
    this.retrieveAvailableGames();
    this.activeGame = {
       id: "test",
       board: {
         pockets: [0,1,2,3,4,5,6,7,8,9,10,11,12,13]
       }
    }
  }

  retrieveAvailableGames() {
    this.http.get<Game[]>("http://localhost:8080/games", {}).subscribe(response => {
      this.availableGames = response;
    });
  }

  createGame() {
    this.http.put<Game>("http://localhost:8080/game", {}).subscribe(response => {
      this.activeGame = response;
      this.retrieveAvailableGames();
    })
  }

  joinGame(id: string) {
    this.http.post<Game>(`http://localhost:8080/game/join?id=${id}`, {}).subscribe(response => {
      this.activeGame = response;
    })
  }
}
