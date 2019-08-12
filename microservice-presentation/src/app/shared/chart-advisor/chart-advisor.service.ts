import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ChartAdvisorPrediction } from './chart-advisor-prediction';
import {environment} from '../../../environments/environment';
import {HttpHelperService} from '../../common/http-helper/http-helper.service';
import { catchError } from 'rxjs/operators';


@Injectable()
export class ChartAdvisorService {

    constructor( private http: HttpClient, private helper: HttpHelperService) { }

    get24hAbsolute(): Observable<ChartAdvisorPrediction[]> {
        return this.http.get<ChartAdvisorPrediction[]>( this.helper.buildPath( 'routing/chart-advisor/sentiment/24hAbsolute') )
        .pipe(catchError(this.helper.handleError));

    }
}
