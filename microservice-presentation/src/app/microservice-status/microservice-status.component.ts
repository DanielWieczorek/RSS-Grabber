import { Component, OnInit } from '@angular/core';
import { MicroserviceStatusService } from '../shared/microservice-status/microservice-status.service'
import { MicroserviceStatus } from '../shared/microservice-status/microservice-status'



@Component({
  selector: 'app-microservice-status',
  templateUrl: './microservice-status.component.html',
  styleUrls: ['./microservice-status.component.css']
})
export class MicroserviceStatusComponent implements OnInit {

    title = 'app';
    data : MicroserviceStatus[];
    error: string;

    constructor(private microserviceStatus: MicroserviceStatusService){
    }
    
    start(name : string){    
      this.microserviceStatus.start(name).subscribe(data => {
        this.data = data;
      });
    }
    
    stop(name : string){
        this.microserviceStatus.stop(name).subscribe(data => {
            console.log(data);
        this.data = data;
      });
    }

    ngOnInit(): void {
        this.microserviceStatus.status().subscribe(data => {
            console.log("data arrived",data)
        this.data = data;
      },
      err => {console.log(err);this.error = err;});
    }
}
