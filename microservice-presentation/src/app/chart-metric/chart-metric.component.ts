import { Component, OnInit } from '@angular/core';
import { ChartMetricRecord } from '../shared/chart-metric/chart-metric-record';
import { ChartMetricService } from '../shared/chart-metric/chart-metric.service';


@Component({
  selector: 'app-chart-metric',
  templateUrl: './chart-metric.component.html',
  styleUrls: ['./chart-metric.component.css']
})
export class ChartMetricComponent implements OnInit {

  title = 'app';
  data : ChartMetricRecord[];
  error: string;

  constructor(private chartMetric: ChartMetricService){
  }
  
  requestCurrentHourSentiment() : void {
      this.chartMetric.getNow().subscribe(data => {
          console.log("test",data);
          this.data = data as ChartMetricRecord[];
      },
      err => this.error = err);
  }
  
  ngOnInit(): void {
      this.requestCurrentHourSentiment();
  }
}
