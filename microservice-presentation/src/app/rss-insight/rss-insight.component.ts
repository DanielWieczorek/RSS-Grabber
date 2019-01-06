import { Component, OnInit } from '@angular/core';
import { RssEntry,RssEntrySentiment,RssEntrySentimentSummary,SentimentEvaluationResult } from '../shared/rss-insight/rss-entry';
import { RssInsightService } from '../shared/rss-insight/rss-insight.service'


@Component({
  selector: 'app-rss-insight',
  templateUrl: './rss-insight.component.html',
  styleUrls: ['./rss-insight.component.css']
})
export class RssInsightComponent implements OnInit {


  title = 'app';
  data : SentimentEvaluationResult;
  chart = [];

  constructor(private rssInsight: RssInsightService){
  }
  
  request24HourSentiment() : void {
      this.rssInsight.sentiment().subscribe(data => {
          console.log("test",data);
          this.data = data as SentimentEvaluationResult;
      });
  }
  
  ngOnInit(): void {
      this.request24HourSentiment();
  }
}
