import { Component, OnInit } from '@angular/core';
import { RssEntry,RssEntrySentiment,RssEntrySentimentSummary,SentimentEvaluationResult } from './rss-entry';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-rss-insight',
  templateUrl: './rss-insight.component.html',
  styleUrls: ['./rss-insight.component.css']
})
export class RssInsightComponent implements OnInit {


  title = 'app';
  data : SentimentEvaluationResult;
  chart = [];

  constructor(private http: HttpClient){
  }
  
  
  
  request24HourSentiment() : void {
      this.http.get<SentimentEvaluationResult>('http://localhost:11020/sentiment').subscribe(data => {
          console.log("test",data);
          this.data = data as SentimentEvaluationResult;
      });
  }
  
  ngOnInit(): void {
      this.request24HourSentiment();
  }
}
