import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ChartEntry } from './chart-entry';
import {environment} from '../../../environments/environment';
import {HttpHelperService} from '../../common/http-helper/http-helper.service';
import { catchError } from 'rxjs/operators';


@Injectable()
export class ChartReaderService {
    private port = 12000;

    constructor( private http: HttpClient, private helper: HttpHelperService) { }

    get24hOhlcv(): Observable<ChartEntry[]> {
        return this.http.get<ChartEntry[]>( this.helper.buildPath( '/ohlcv/24h', this.port ) )
        .pipe(catchError(this.helper.handleError));

    }
}
