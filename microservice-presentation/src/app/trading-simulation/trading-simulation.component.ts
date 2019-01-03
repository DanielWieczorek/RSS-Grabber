import { Component, OnInit,AfterViewInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Chart } from 'chart.js';
import 'rxjs/add/operator/map';
import {Observable} from 'rxjs';
import { ChartEntry } from './chart-entry';
import { Trade } from './trade';


@Component({
  selector: 'app-trading-simulation',
  templateUrl: './trading-simulation.component.html',
  styleUrls: ['./trading-simulation.component.css']
})
export class TradingSimulationComponent implements AfterViewInit {

    title = 'app';

    constructor(private http: HttpClient){
        
    }
    
    
    chart = []; // This will hold our chart info
    data: ChartEntry[];
    trades: Trade[];

  
    ngAfterViewInit(): void {
        this.http.get( 'http://localhost:12000/ohlcv/24h' ).subscribe( res => {
            console.log( res )
            this.data = res as ChartEntry[];

            this.http.get( 'http://localhost:22020/simulate' ).subscribe( trades => {
                this.trades = trades as Trade[];


                let close = this.data.map( item => item.close );
                let open = this.data.map( item => item.open );
                let alldates = this.data.map( item => item.date )
                let sells = this.trades.filter( x => x.action === 'SELL' )
                let buys = this.trades.filter( x => x.action === 'BUY' )

                let sellData = []
                let buyData = []

                console.log( alldates )

                let weatherDates = []
                alldates.forEach(( res ) => {
                    let jsdate = new Date( res[0], res[1], res[2], res[3], res[4] )
                    weatherDates.push( jsdate.toLocaleTimeString( 'en', { year: 'numeric', month: 'short', day: 'numeric' } ) )

                    let sellFound = sells.filter( sell => {
                        let d = new Date( sell.date[0], sell.date[1], sell.date[2], sell.date[3], sell.date[4] )
                        return (d.getTime() === jsdate.getTime() )
                    } )

                    if ( sellFound[0] !== undefined ) {
                        sellData.push( sellFound[0].currentRate )
                    } else {
                        sellData.push( NaN )
                    }

                    let buyFound = buys.filter( buy => {
                        let d = new Date( buy.date[0], buy.date[1], buy.date[2], buy.date[3], buy.date[4] )
                        return ( d.getTime() === jsdate.getTime() )
                    } )

                    if ( buyFound[0] !== undefined ) {
                        buyData.push( buyFound[0].currentRate )
                    } else {
                        buyData.push( NaN )
                    }
                } )

                console.log( 'trades:', trades )
                console.log( 'sells', sells )
                console.log( 'buys', buys )
                console.log( 'sellsData', sellData )
                console.log( 'buyData', buyData)

                this.chart = new Chart( 'canvas', {
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
                                data: buyData,
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
                                display: true
                            }],
                            yAxes: [{
                                display: true
                            }],
                        }
                    }
                } );
            } )
        } )
    }
    
}
