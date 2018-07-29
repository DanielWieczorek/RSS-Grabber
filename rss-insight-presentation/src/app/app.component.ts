import { RssEntry,RssEntrySentiment,RssEntrySentimentSummary,SentimentEvaluationResult } from './rss-entry';
import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Chart } from 'chart.js';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  providers:[RssEntry]
})
export class AppComponent {

  title = 'app';
  data : SentimentEvaluationResult;
  chart = [];

  constructor(private http: HttpClient){
  }
  
  
  
  request24HourSentiment() : void {
      this.http.get<SentimentEvaluationResult>('http://localhost:11020/sentiment').subscribe(data => {
          console.log("test",data);
          this.data = data as SentimentEvaluationResult;
          
          var canvas = <HTMLCanvasElement> document.getElementById("canvas");
          var ctx = canvas.getContext("2d");
          
          this.chart = new Chart(ctx, {
              type: 'doughnut',
              data: {
                labels: ['positive','negative'],
                datasets: [
                  {
                    data:[ data.summary.positiveProbability,
                           data.summary.negativeProbability,
                    ],
                    backgroundColor: [
                                      '#b2ff59',
                                     '#ff6e40',
                                  ],
                  }
                ]
              },
              options: {
                  maintainAspectRatio: false,
                  responsive: true,
                  position: 'left',
                  legend: {
                      position: 'top',
                  },
                  title: {
                      display: false,
                      text: 'Chart.js Doughnut Chart'
                  },
                  animation: {
                      animateScale: true,
                      animateRotate: true
                  }
              }
            });
          console.log("hiiii ", this.chart);
      });
  }
  
  ngOnInit(): void {
      this.request24HourSentiment();
  }
}
