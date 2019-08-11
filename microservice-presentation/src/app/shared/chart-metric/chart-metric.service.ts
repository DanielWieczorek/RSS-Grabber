import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ChartMetricRecord } from './chart-metric-record';
import {environment} from '../../../environments/environment';
import {HttpHelperService} from '../../common/http-helper/http-helper.service';
import { catchError } from 'rxjs/operators';


@Injectable()
export class ChartMetricService {

    constructor( private http: HttpClient, private helper: HttpHelperService) { }

    getNow(): Observable<ChartMetricRecord[]> {
        return this.http.get<ChartMetricRecord[]>( this.helper.buildPath( 'routing/chart-metric/metric/now') )
        .pipe(catchError(this.helper.handleError));

    }
}
