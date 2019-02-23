import { Component, OnInit } from '@angular/core';
import { RssClassificationService } from '../shared/rss-classification/rss-classification.service'
import { RssEntry } from '../shared/rss-classification/rss-entry'


@Component({
  selector: 'app-rss-classification',
  templateUrl: './rss-classification.component.html',
  styleUrls: ['./rss-classification.component.css']
})
export class RssClassificationComponent implements OnInit {

    title = 'app';
    data : RssEntry[];
    error: string;

    constructor(private rssClassification: RssClassificationService){
        
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
        this.rssClassification.classify(data).subscribe(d => {
            console.log(data)
            this.removeItem(data);
          });
    }
    
    ngOnInit(): void {
        this.rssClassification.find().subscribe(data => {
          console.log(data)
        this.data = data as RssEntry[];
      },
      err => this.error = err);
    }
    
}
