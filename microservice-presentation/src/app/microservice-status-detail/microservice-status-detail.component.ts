import { Component, OnInit } from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import { MicroserviceStatusService } from '../shared/microservice-status/microservice-status.service'
import { MicroserviceStatus,MicroserviceAction } from '../shared/microservice-status/microservice-status'
import { HttpClient } from '@angular/common/http';
import 'rxjs/Rx' ;



@Component({
  selector: 'app-microservice-status-detail',
  templateUrl: './microservice-status-detail.component.html',
  styleUrls: ['./microservice-status-detail.component.css']
})
export class MicroserviceStatusDetailComponent implements OnInit {
    
    data: MicroserviceStatus;
    protocol: string = "http";
    hostname: string = "localhost";

  constructor(private MicroserviceStatus: MicroserviceStatusService, private route: ActivatedRoute, private http: HttpClient ) { }

  ngOnInit() {
      this.route.params
      .subscribe( params => this.MicroserviceStatus.getCachedData()
              .subscribe(data => {this.data = data.find(item => item.name === params['name'] ); console.log(this.data)}));
  }
  
  
  performExport() {
      const dataObservable = this.performGetAction('/feature/export');
      dataObservable.subscribe(data =>{

      const blob = new Blob([JSON.stringify(data)], { type: 'text/json' });
      const url= window.URL.createObjectURL(blob);
      var link = document.createElement('a');
      // Browsers that support HTML5 download attribute
      if (link.download !== undefined) 
      {
          link.setAttribute('href', url);
          link.setAttribute('download', `${this.data.name}-export.json`);
          link.style.visibility = 'hidden';
          document.body.appendChild(link);
          link.click();
          document.body.removeChild(link);
      }
      });
      }
  
  performImport(files: FileList) {
   let fileReader = new FileReader();
   fileReader.onload = (e) => {
       console.log(fileReader.result);
       this.performPostAction('/feature/import',fileReader.result as string)
       .subscribe(x => {
           console.log(x);
       });
     }
   console.log(files[0]);
     fileReader.readAsText(files[0]);
      
      }
  
  performGetAction(path : string) : any {
      const url: string = `${this.protocol}://${this.data.bindHostname}:${this.data.bindPort}${path}`;
      return this.http.get( url );
  }
  
  performPostAction(path : string, data: string) : any {
      const url: string = `${this.protocol}://${this.data.bindHostname}:${this.data.bindPort}${path}`;
      return this.http.post( url ,data, {headers:{'Content-Type': 'application/json'}});
  }

}
