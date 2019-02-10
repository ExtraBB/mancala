import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../environments/environment'
import Game from './models/Game';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  availableGames: String[];
  activeGame: Game;
  playerId: String;

  // Form
  createGameForm: FormGroup;
  formError: String;

  // WebSocket
  stompClient: Stomp.Client;
  activeSubscription: Stomp.Subscription;
  activeSubscriptionGameId: String;

  constructor(private http: HttpClient, private fb: FormBuilder) {
    this.createGameForm = this.fb.group({
      size: [14, Validators.min(4)],
      pieces: [6, Validators.min(1)]
    });

    const ws = new SockJS("http://localhost:8080/socket");
    this.stompClient = Stomp.over(ws);
    this.stompClient.connect({}, async (frame) => {
      this.register();
    });
  }

  subscribeToGame(game: Game) {
    if(!this.activeSubscription || this.activeSubscriptionGameId !== game.id) {
      this.activeSubscription = this.stompClient.subscribe(`/game/${game.id}/${this.playerId}`, (message) => {
        this.activeGame = JSON.parse(message.body);
      });
    }
  }

  register() {
    const playerFromCookie = document.cookie.split("=")[1];
    if(!playerFromCookie) {
      console.log("hello")
      this.http.get(`${environment.serverBaseUrl}/register`, {  withCredentials: true, responseType: 'text'  }).subscribe(response => {
        this.playerId = response;
        this.retrieveAvailableGames();
        this.retrieveActiveGame();
      });
    } else {
      this.playerId = playerFromCookie;
      this.retrieveAvailableGames();
      this.retrieveActiveGame();
    }
  }

  retrieveActiveGame() {
    this.http.get<Game>(`${environment.serverBaseUrl}/game`, {  withCredentials: true  }).subscribe(response => {
      this.activeGame = response;
      this.subscribeToGame(this.activeGame);
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
        this.subscribeToGame(this.activeGame);
      });
    } else {
      this.formError = "Board size has to be at least 4 and starting pieces at least 1"
    }
  }

  joinGame(id: string) {
    this.http.post<Game>(`${environment.serverBaseUrl}/game/join?id=${id}`, {}, {  withCredentials: true  }).subscribe(response => {
      this.activeGame = response;
      this.subscribeToGame(this.activeGame);
    });
  }
}
