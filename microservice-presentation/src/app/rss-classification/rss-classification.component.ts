import { Component, OnInit } from '@angular/core';
import { RssClassificationService } from '../shared/rss-classification/rss-classification.service'
import { RssEntry } from '../shared/rss-classification/rss-entry'
import { ClassificationStatistics } from '../shared/rss-classification/classification-statistics'



@Component({
  selector: 'app-rss-classification',
  templateUrl: './rss-classification.component.html',
  styleUrls: ['./rss-classification.component.css']
})
export class RssClassificationComponent implements OnInit {

    title = 'app';
    data : RssEntry[];
    statistics : ClassificationStatistics;
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

    updateStatistics() {
        this.statistics.classified++;
        this.statistics.unclassified--;
    }
    
    classifyNeutral(data: RssEntry) : void {
        data.classification = 0;
        this.sendClassificationRequest(data);
    }
    
    sendClassificationRequest(data: RssEntry ) : void {
        this.rssClassification.classify(data).subscribe(d => {
            console.log(data)
            this.removeItem(data);
            this.updateStatistics();
          });
    }
    
    ngOnInit(): void {
        this.rssClassification.find().subscribe(data => {
          console.log(data)
        this.data = data as RssEntry[];
      },
      err => this.error = err);

      this.rssClassification.statistics().subscribe(statistics => {
          console.log(statistics)
        this.statistics = statistics as ClassificationStatistics;
      },
      err => this.error = err);
    }
    
}
