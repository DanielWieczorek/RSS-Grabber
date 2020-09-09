import{Component, OnInit}from '@angular/core';
import {MicroserviceStatusService}from '../shared/microservice-status/microservice-status.service'
import {MicroserviceStatus}from '../shared/microservice-status/microservice-status'



@Component({
selector: 'app-microservice-status',
templateUrl: './microservice-status.component.html',
styleUrls: ['./microservice-status.component.css']
})
export class MicroserviceStatusComponent implements OnInit {

title = 'app';
dataRss : MicroserviceStatus[];
dataChart : MicroserviceStatus[];
dataMicroservice : MicroserviceStatus[];
dataMisc : MicroserviceStatus[];

error: string;

constructor(private microserviceStatus: MicroserviceStatusService){
    }

    start(name : string){
      this.microserviceStatus.start(name).subscribe(data => {
      this.saveData(data);



      });
    }

    stop(name : string){
        this.microserviceStatus.stop(name).subscribe(data => {
            console.log(data);
        this.saveData(data);
      });
    }

    ngOnInit(): void {
        this.microserviceStatus.status().subscribe(data => {
            console.log("data arrived",data)
        this.saveData(data);
      },
      err => {console.log(err);this.error = err;});
    }

    saveData(data : MicroserviceStatus[]):void{
      this.dataRss =  data.filter(item => item.name.lastIndexOf('rss',0) == 0);
      this.dataChart =  data.filter(item => item.name.lastIndexOf('chart',0) == 0);
      this.dataMicroservice =  data.filter(item => item.name.lastIndexOf('microservice',0) == 0);
      this.dataMisc =  data.filter(item => item.name.lastIndexOf('microservice',0) == -1 &&
            item.name.lastIndexOf('chart',0) == -1 && 
            item.name.lastIndexOf('rss',0) == -1);
    }
}
