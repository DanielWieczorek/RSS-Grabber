import { Component, OnInit, AfterViewInit } from '@angular/core';
import { Chart } from 'chart.js';

import {Observable} from 'rxjs';
import { ChartEntry } from '../shared/chart-reader/chart-entry';
import { LiveTrade } from '../shared/trader-live/live-trade';
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


    chart = []; // This will hold our chart info
    data: ChartEntry[];
    trades: LiveTrade[];
    error: string;



    ngAfterViewInit(): void {
        this.chartReader.get24hOhlcv().subscribe(res => {
            console.log(res)
            this.data = res as ChartEntry[];

            this.traderLive.get24h().subscribe(trades => {
                this.trades = trades as LiveTrade[];


                let close = this.data.map(item => item.close);
                let open = this.data.map(item => item.open);
                let alldates = this.data.map(item => item.date)
                let sells = this.trades.filter(x => x.type === 'SELL' && x.status === 'PLACED')
                let buys = this.trades.filter(x => x.type === 'BUY' && x.status === 'PLACED')

                let cancelledSells = this.trades.filter(x => x.type === 'SELL' && x.status === 'CANCELLED')
                let cancelledBuys = this.trades.filter(x => x.type === 'BUY' && x.status === 'CANCELLED')

                let sellData = []
                let buyData = []

                let sellDataCancelled = []
                let buyDataCancelled = []

                console.log(alldates)

                let weatherDates = []
                alldates.forEach((res) => {
                    let jsdate = new Date(res[0], res[1], res[2], res[3], res[4])
                    weatherDates.push(jsdate.toLocaleTimeString('en', { year: 'numeric', month: 'short', day: 'numeric' }))

                    let sellFound = sells.filter(sell => {
                        let d = new Date(sell.time[0], sell.time[1], sell.time[2], sell.time[3], sell.time[4])
                        return (d.getTime() === jsdate.getTime())
                    })

                    if (sellFound[0] !== undefined) {
                        sellData.push(sellFound[0].price)
                    } else {
                        sellData.push(NaN)
                    }



                    let cancelledSellFound = cancelledSells.filter(sell => {
                        let d = new Date(sell.time[0], sell.time[1], sell.time[2], sell.time[3], sell.time[4])
                        return (d.getTime() === jsdate.getTime())
                    })

                    if (cancelledSellFound[0] !== undefined) {
                        sellDataCancelled.push(cancelledSellFound[0].price)
                    } else {
                        sellDataCancelled.push(NaN)
                    }







                    let buyFound = buys.filter(buy => {
                        let d = new Date(buy.time[0], buy.time[1], buy.time[2], buy.time[3], buy.time[4])
                        return (d.getTime() === jsdate.getTime())
                    })

                    if (buyFound[0] !== undefined) {
                        buyData.push(buyFound[0].price)
                    } else {
                        buyData.push(NaN)
                    }


                    let cancelledBuyFound = cancelledBuys.filter(buy => {
                        let d = new Date(buy.time[0], buy.time[1], buy.time[2], buy.time[3], buy.time[4])
                        return (d.getTime() === jsdate.getTime())
                    })

                    if (cancelledBuyFound[0] !== undefined) {
                        buyDataCancelled.push(cancelledBuyFound[0].price)
                    } else {
                        buyDataCancelled.push(NaN)
                    }
                })

                console.log('trades:', trades)
                console.log('sells', sells)
                console.log('buys', buys)
                console.log('sellsData', sellData)
                console.log('buyData', buyData)

                this.chart = new Chart('canvas', {
                    type: 'line',
                    data: {
                        labels: weatherDates,
                        datasets: [

                            {
                                data: sellData,
                                borderColor: "red",
                                backgroundColor: "#ef5350",
                                pointRadius: 5,
                                pointHoverRadius: 8,
                                fill: false,
                                showLine: false
                            },
                            {
                                data: sellDataCancelled,
                                borderColor: "rgba(255, 0, 0, 0.35)",
                                backgroundColor: "rgba(239, 83, 80, 0.35)",
                                pointRadius: 5,
                                pointHoverRadius: 8,
                                fill: true,
                                showLine: false
                            },
                            {
                                data: buyData,
                                borderColor: "teal",
                                backgroundColor: "#26a69a",
                                pointRadius: 5,
                                pointHoverRadius: 8,
                                fill: false,
                                showLine: false
                            },
                            {
                                data: buyDataCancelled,
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
                                display: true
                            }],
                            yAxes: [{
                                display: true
                            }],
                        }
                    }
                });
            },
                err => this.error = err)
        },
            err => this.error = err)
    }

}
