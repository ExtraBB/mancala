import { Component, OnInit, Input } from '@angular/core';
import Game from '../../models/Game';
import { HttpClient } from '@angular/common/http';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-game',
  templateUrl: './game.component.html',
  styleUrls: ['./game.component.scss']
})
export class GameComponent implements OnInit {

  @Input() game: Game;
  @Input() player: String;

  constructor(private http: HttpClient) { }

  ngOnInit() {
  }

  makeMove(pocket: number) {
    this.http.post<Game>(`${environment.serverBaseUrl}/game/move?pocket=${pocket}`, {}, {  withCredentials: true  }).subscribe(response => {
      this.game = response;
    })
  }

}
