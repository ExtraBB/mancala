import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import Board from '../../models/Board';

@Component({
  selector: 'app-board',
  templateUrl: './board.component.html',
  styleUrls: ['./board.component.scss']
})
export class BoardComponent implements OnInit {

  @Input() board: Board;
  @Input() side: String;
  @Output() move = new EventEmitter<number>();

  constructor() { }

  ngOnInit() {
  }
}
