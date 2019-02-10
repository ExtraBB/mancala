import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';

import { AppComponent } from './app.component';
import { GameComponent } from './components/game/game.component';
import { BoardComponent } from './components/board/board.component';
import { TimesPipe } from './pipes/times/times.pipe';

@NgModule({
  declarations: [
    AppComponent,
    GameComponent,
    BoardComponent,
    TimesPipe
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    ReactiveFormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
