import { Component, OnInit, AfterViewInit } from '@angular/core';
import { Chart } from 'chart.js';

import {Observable} from 'rxjs';
import { ChartEntry } from '../shared/chart-reader/chart-entry';
import { LiveTrade } from '../shared/trader-live/live-trade';
import { LiveAccount } from '../shared/trader-live/live-account';
import { ChartReaderService } from '../shared/chart-reader/chart-reader.service'
import { TraderLiveService } from '../shared/trader-live/trader-live.service'


@Component({
    selector: 'app-trading-live',
    templateUrl: './trading-live.component.html',
    styleUrls: ['./trading-live.component.css']
})
export class TradingLiveComponent implements AfterViewInit {

    title = 'app';

    constructor(
        private chartReader: ChartReaderService,
        private traderLive: TraderLiveService) {

    }

    chart = {} as Chart; // This will hold our chart info
    data: ChartEntry[];
    trades: LiveTrade[];
    error: string;

    accountBefore: LiveAccount;
    accountAfter: LiveAccount;


    ngOnInit(): void {
         this.initAccountData(TraderLiveService.prototype.getAccount24h);
    }

    initAccountData(accountFunc: () => Observable<LiveAccount[]>):void{
         accountFunc.apply(this.traderLive).subscribe(res => {
            if(res.length > 0){
                this.accountBefore = res[0];
                this.accountAfter = res[res.length-1]
            }
        });
    }

    set30DayTimeframe() {
        this.initAccountData(TraderLiveService.prototype.getAccount30d);
        this.buildChart(ChartReaderService.prototype.get30dOhlcv,TraderLiveService.prototype.getTrades30d);
    }

   set24HourTimeframe() {
        this.initAccountData(TraderLiveService.prototype.getAccount24h);
        this.buildChart(ChartReaderService.prototype.get24hOhlcv,TraderLiveService.prototype.getTrades24h);
    }

    set7DayTimeframe() {
        this.initAccountData(TraderLiveService.prototype.getAccount7d);
        this.buildChart(ChartReaderService.prototype.get7dOhlcv,TraderLiveService.prototype.getTrades7d);
    }

    set365DayTimeframe() {
        this.initAccountData(TraderLiveService.prototype.getAccount365d);
        this.buildChart(ChartReaderService.prototype.get365dOhlcv,TraderLiveService.prototype.getTrades365d);
    }

    reloadConfig(): void {
        this.traderLive.reloadConfiguration();
    }

    ngAfterViewInit(): void {
        this.buildChart(ChartReaderService.prototype.get24hOhlcv,TraderLiveService.prototype.getTrades24h);
    }

    buildChart(chartFunc: () => Observable<ChartEntry[]>,tradesFunc: () => Observable<LiveTrade[]>) : void {
        chartFunc.apply(this.chartReader).subscribe(res => {
            console.log(res)
            this.data = res as ChartEntry[];

            tradesFunc.apply(this.traderLive).subscribe(trades => {
                this.trades = trades as LiveTrade[];

                this.trades.forEach((t) => t.time  = new Date(t.time[0], t.time[1]-1, t.time[2], t.time[3], t.time[4]));


                let labels = this.data.map(d => {  return new Date(d.date[0], d.date[1]-1, d.date[2], d.date[3], d.date[4]);});

                    
                  let close = this.data.map(d => {  
                     let time =  new Date(d.date[0], d.date[1]-1, d.date[2], d.date[3], d.date[4]);
                     return {x: time, y: d.close}});
                let open = this.data.map(item => item.open);
                let alldates = this.data.map(item => item.date)
                let sells = this.trades.filter(x => x.type === 'SELL' && x.status === 'PLACED')
                .map(d => {  return {x: d.time, y: d.price}});
                let buys = this.trades.filter(x => x.type === 'BUY' && x.status === 'PLACED')
                 .map(d => {  return {x: d.time, y: d.price}});

                let cancelledSells = this.trades.filter(x => x.type === 'SELL' && x.status === 'CANCELLED')
                 .map(d => {  return {x: d.time, y: d.price}});
                let cancelledBuys = this.trades.filter(x => x.type === 'BUY' && x.status === 'CANCELLED')
                 .map(d => {  return {x: d.time, y: d.price}});

        
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
                                data: cancelledSells,
                                borderColor: "rgba(255, 0, 0, 0.35)",
                                backgroundColor: "rgba(239, 83, 80, 0.35)",
                                pointRadius: 5,
                                pointHoverRadius: 8,
                                fill: true,
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
                                data: cancelledBuys,
                                borderColor: "rgba(0, 128, 128, 0,35)",
                                backgroundColor: "rgba(38, 166, 154, 0.35)",
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
