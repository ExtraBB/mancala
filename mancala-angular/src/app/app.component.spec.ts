import { TestBed, async } from '@angular/core/testing';
import { AppComponent } from './app.component';
import { GameComponent } from './components/game/game.component';
import { BoardComponent } from './components/board/board.component';
import { TimesPipe } from './pipes/times/times.pipe';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';

describe('AppComponent', () => {
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        AppComponent,
        GameComponent,
        BoardComponent,
        TimesPipe
      ],
      imports: [ReactiveFormsModule, HttpClientModule]
    }).compileComponents();
  }));

  it('should create the app', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.debugElement.componentInstance;
    expect(app).toBeTruthy();
  });
});
