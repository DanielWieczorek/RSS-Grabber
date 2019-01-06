import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ChartEntry } from './chart-entry';

@Injectable()
export class ChartReaderService {
    private protocol = "http"
    private hostname = "localhost";
    private port = 12000;

    constructor( private http: HttpClient ) { }

    get24hOhlcv(): Observable<ChartEntry[]> {
        return this.http.get<ChartEntry[]>( this.buildPath( '/ohlcv/24h' ) );
    }

    private buildPath( path: string ): string {
        return `${this.protocol}://${this.hostname}:${this.port}${path}`
    }

}
