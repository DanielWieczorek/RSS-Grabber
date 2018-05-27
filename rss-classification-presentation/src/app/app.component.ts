import { RssEntry } from './rss-entry';
import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  providers:[RssEntry]
})
export class AppComponent {

  title = 'app';
  data : RssEntry[];

  constructor(private http: HttpClient){
  }
  
  classifyPositive(data: RssEntry) : void {
      data.classification = 1;
      this.sendClassificationRequest(data);
     
  }

  classifyNegative(data: RssEntry) : void {
      data.classification = -1;
      this.sendClassificationRequest(data);
  }
  
  removeItem(element: RssEntry) {
      const index = this.data.indexOf(element);
      this.data.splice(index, 1);
  }
  
  classifyNeutral(data: RssEntry) : void {
      data.classification = 0;
      this.sendClassificationRequest(data);
  }
  
  sendClassificationRequest(data: RssEntry ) : void {
      this.http.post<RssEntry>('http://localhost:10020/classify',data).subscribe(d => {
          console.log(data)
          this.removeItem(data);
        });
  }
  
  ngOnInit(): void {
    this.http.get('http://localhost:10020/find').subscribe(data => {
        console.log(data)
      this.data = data as RssEntry[];
    });
  }
}
