import { Component, OnInit, AfterViewInit } from '@angular/core';
import { Chart } from 'chart.js';

import {Observable} from 'rxjs';
import { ChartEntry } from '../shared/chart-reader/chart-entry';
import { Trade } from '../shared/trader-simulation/trade';
import { Account } from '../shared/trader-simulation/account';
import { ChartReaderService } from '../shared/chart-reader/chart-reader.service'
import { TraderSimulationService } from '../shared/trader-simulation/trader-simulation.service'


@Component({
    selector: 'app-trading-simulation',
    templateUrl: './trading-simulation.component.html',
    styleUrls: ['./trading-simulation.component.css']
})
export class TradingSimulationComponent implements AfterViewInit {

    title = 'app';

    constructor(
        private chartReader: ChartReaderService,
        private traderSimulation: TraderSimulationService) {

    }


    chart = {} as Chart; // This will hold our chart info
    data: ChartEntry[];
    trades: Trade[];
    error: string;
    lastTradeBalance: Account;
    initialTradeBalance: Account;

    public calculateVolume(trade: Trade) : number {
        return trade.before.eurEquivalent/trade.currentRate;
    }



    ngAfterViewInit(): void {
        this.buildChart(ChartReaderService.prototype.get24hOhlcv,TraderSimulationService.prototype.simulate24h);

    }

    set30DayTimeframe() {
        this.buildChart(ChartReaderService.prototype.get30dOhlcv,TraderSimulationService.prototype.simulate30d);
    }

   set24HourTimeframe() {
        this.buildChart(ChartReaderService.prototype.get24hOhlcv,TraderSimulationService.prototype.simulate24h);
    }

    set7DayTimeframe() {
        this.buildChart(ChartReaderService.prototype.get7dOhlcv,TraderSimulationService.prototype.simulate7d);
    }

    set365DayTimeframe() {
        this.buildChart(ChartReaderService.prototype.get365dOhlcv,TraderSimulationService.prototype.simulate365d);
    }


    buildChart(chartFunc: () => Observable<ChartEntry[]>,tradesFunc: () => Observable<Trade[]>) : void {
        chartFunc.apply(this.chartReader).subscribe(res => {
            console.log(res)
            this.data = res as ChartEntry[];

            tradesFunc.apply(this.traderSimulation).subscribe(trades => {
                this.trades = trades as Trade[];

                this.trades.forEach((t) => t.date  = new Date(t.date[0], t.date[1]-1, t.date[2], t.date[3], t.date[4]));


                let labels = this.data.map(d => {  return new Date(d.date[0], d.date[1]-1, d.date[2], d.date[3], d.date[4]);});

                    
                  let close = this.data.map(d => {  
                     let time =  new Date(d.date[0], d.date[1]-1, d.date[2], d.date[3], d.date[4]);
                     return {x: time, y: d.close}});
                let open = this.data.map(item => item.open);
                let alldates = this.data.map(item => item.date)
                let sells = this.trades.filter(x => x.action === 'SELL' )
                .map(d => {  return {x: d.date, y: d.currentRate}});
                let buys = this.trades.filter(x => x.action === 'BUY' )
                 .map(d => {  return {x: d.date, y: d.currentRate}});
        
                console.log(alldates)

                var stepping = Math.max(Math.floor( alldates.length / 1440 ),1);
                console.log("stepping: "+stepping)
                var i = 0;

                let param = {
                    type: 'line',
                    data: {
                        labels: labels,
                        datasets: [

                            {
                                data: sells,
                                borderColor: "red",
                                backgroundColor: "#ef5350",
                                pointRadius: 5,
                                pointHoverRadius: 8,
                                fill: false,
                                showLine: false
                            },
                            {
                                data: buys,
                                borderColor: "teal",
                                backgroundColor: "#26a69a",
                                pointRadius: 5,
                                pointHoverRadius: 8,
                                fill: false,
                                showLine: false
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
                                type: 'time',
                                display: true
                            }],
                            yAxes: [{
                                display: true
                            }],
                        },
                        tick: {
                            sampleSize: 1440
                        }
                    }
                };

                if(this.chart.data == undefined) {
                  this.chart = new Chart('canvas', param);
                } else {
                  this.chart.data.labels = param.data.labels;
                  this.chart.data.datasets = param.data.datasets;
                  this.chart.update();
                }
            },
                err => this.error = err)
        },
            err => this.error = err)
    }


}
