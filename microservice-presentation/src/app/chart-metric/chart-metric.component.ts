import { Component, OnInit } from '@angular/core';
import { ChartMetricRecord } from '../shared/chart-metric/chart-metric-record';
import { ChartMetricService } from '../shared/chart-metric/chart-metric.service';
import { ChartAdvisorService } from '../shared/chart-advisor/chart-advisor.service';
import { ChartAdvisorPrediction } from '../shared/chart-advisor/chart-advisor-prediction';
import { Chart } from 'chart.js';

import { ChartEntry } from '../shared/chart-reader/chart-entry';
import { ChartReaderService } from '../shared/chart-reader/chart-reader.service'


@Component({
  selector: 'app-chart-metric',
  templateUrl: './chart-metric.component.html',
  styleUrls: ['./chart-metric.component.css']
})
export class ChartMetricComponent implements OnInit {

  title = 'app';
  data : ChartMetricRecord[];
  error: string;
  chart = []; // This will hold our chart info
  chartData: ChartEntry[];
  trades: ChartAdvisorPrediction[];


  constructor(private chartMetric: ChartMetricService, private chartAdvisor: ChartAdvisorService,
  private chartReader : ChartReaderService){
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


    ngAfterViewInit(): void {
          this.chartReader.get24hOhlcv().subscribe( res => {
              console.log( res )
              this.chartData = res as ChartEntry[];

              this.chartAdvisor.get24hAbsolute().subscribe( trades => {
                  this.trades = trades as ChartAdvisorPrediction[];


                  let close = this.chartData.map( item => item.close );
                  let alldates = this.chartData.map( item => item.date )

                  let predictions = this.trades;
                  let sellData = []

                  console.log( alldates )

                  let weatherDates = []
                  alldates.forEach(( res ) => {
                      let jsdate = new Date( res[0], res[1], res[2], res[3], res[4] )
                      weatherDates.push( jsdate.toLocaleTimeString( 'en', { year: 'numeric', month: 'short', day: 'numeric' } ) )

                      let sellFound = predictions.filter( sell => {
                          let d = new Date( sell.targetTime[0], sell.targetTime[1], sell.targetTime[2], sell.targetTime[3], sell.targetTime[4] )
                          return (d.getTime() === jsdate.getTime() )
                      } )

                      if ( sellFound[0] !== undefined ) {
                          sellData.push( sellFound[0].prediction )
                      } else {
                          sellData.push( NaN )
                      }
                  } )

                  this.chart = new Chart( 'canvas', {
                      type: 'line',
                      data: {
                          labels: weatherDates,
                          datasets: [

                              {
                                  data: sellData,
                                  borderColor: "rgba(00,150,136,0.5)",
                                  borderWidth: "2",
                                  pointRadius: 0,
                                  fill: false,
                                  showLine: true
                              },
                              {
                                  data: close,
                                  borderColor: "#bdbdbd",
                                  pointRadius: 0,

                                  fill: false,
                                  showPoints: false
                              }
                          ]
                      },
                      options: {
                          legend: {
                              display: false
                          },
                          scales: {
                              xAxes: [{
                                  display: true
                              }],
                              yAxes: [{
                                  display: true
                              }],
                          }
                      }
                  } );
              },
              err => this.error = err)
          },
          err => this.error = err)
      }
}
