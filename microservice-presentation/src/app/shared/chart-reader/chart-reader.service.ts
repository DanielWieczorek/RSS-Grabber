import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ChartEntry } from './chart-entry';
import {environment} from '../../../environments/environment';
import {HttpHelperService} from '../../common/http-helper/http-helper.service';
import { catchError } from 'rxjs/operators';


@Injectable()
export class ChartReaderService {

    constructor( private http: HttpClient, private helper: HttpHelperService) { }

    get24hOhlcv(): Observable<ChartEntry[]> {
        return this.http.get<ChartEntry[]>( this.helper.buildPath( 'routing/chart-datacollection/ohlcv/24h') )
        .pipe(catchError(this.helper.handleError));
    }

    get7dOhlcv(): Observable<ChartEntry[]> {
        return this.http.get<ChartEntry[]>( this.helper.buildPath( 'routing/chart-datacollection/ohlcv/7d?maxSize=1440') )
        .pipe(catchError(this.helper.handleError));
    }

    get30dOhlcv(): Observable<ChartEntry[]> {
        return this.http.get<ChartEntry[]>( this.helper.buildPath( 'routing/chart-datacollection/ohlcv/30d?maxSize=1440') )
        .pipe(catchError(this.helper.handleError));
    }

    get365dOhlcv(): Observable<ChartEntry[]> {
        return this.http.get<ChartEntry[]>( this.helper.buildPath( 'routing/chart-datacollection/ohlcv/365d?maxSize=1440') )
        .pipe(catchError(this.helper.handleError));

    }
}
