import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'app';
  data : Object;

  constructor(private http: HttpClient){
  }
  
  start(name : String){
   	this.http.get('http://localhost:10000/start/'+name).subscribe(data => {
      this.data = data;
    });
  }
  
  stop(name : String){
  	this.http.get('http://localhost:10000/stop/'+name).subscribe(data => {
      this.data = data;
    });
  }

  ngOnInit(): void {
    this.http.get('http://localhost:10000/status').subscribe(data => {
      this.data = data;
    });
  }
}
