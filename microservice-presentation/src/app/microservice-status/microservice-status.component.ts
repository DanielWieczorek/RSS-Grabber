import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';


@Component({
  selector: 'app-microservice-status',
  templateUrl: './microservice-status.component.html',
  styleUrls: ['./microservice-status.component.css']
})
export class MicroserviceStatusComponent implements OnInit {

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
      this.http.get('http://localhost:10000/getstatus').subscribe(data => {
        this.data = data;
      });
    }
}
