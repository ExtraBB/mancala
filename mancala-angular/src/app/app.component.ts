import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'mancala-angular';

  constructor(private http: HttpClient) {
    this.http.get("/game").subscribe(response => {
      console.log(response);
      this.title = "hi";
    })
  }
}
