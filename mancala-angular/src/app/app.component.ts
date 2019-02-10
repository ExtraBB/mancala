import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../environments/environment'
import Game from './models/Game';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  availableGames: String[];
  activeGame: Game;

  createGameForm: FormGroup;
  formError: String;

  constructor(private http: HttpClient, private fb: FormBuilder) {
    this.retrieveAvailableGames();
    this.retrieveActiveGame();
    this.createGameForm = this.fb.group({
      size: [14, Validators.min(4)],
      pieces: [6, Validators.min(1)]
    });
  }

  retrieveActiveGame() {
    this.http.get<Game>(`${environment.serverBaseUrl}/game`, {  withCredentials: true  }).subscribe(response => {
      this.activeGame = response;
      console.log(this.activeGame)
    });
  }

  retrieveAvailableGames() {
    this.http.get<String[]>(`${environment.serverBaseUrl}/games`, {  withCredentials: true  }).subscribe(response => {
      this.availableGames = response;
    });
  }

  createGame() {
    if(this.createGameForm.valid) {
      this.formError = "";
      const size = this.createGameForm.get('size').value || 14;
      const pieces = this.createGameForm.get('pieces').value || 6;
      this.http.put<Game>(`${environment.serverBaseUrl}/game?size=${size}&pieces=${pieces}`, {}, {  withCredentials: true  }).subscribe(response => {
        this.activeGame = response;
        this.retrieveAvailableGames();
      });
    } else {
      this.formError = "Board size has to be at least 4 and starting pieces at least 1"
    }
  }

  joinGame(id: string) {
    console.log(id);
    console.log(this.availableGames);
    this.http.post<Game>(`${environment.serverBaseUrl}/game/join?id=${id}`, {}, {  withCredentials: true  }).subscribe(response => {
      this.activeGame = response;
    });
  }
}
