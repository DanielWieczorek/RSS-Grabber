import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Account } from './account';
import { Trade } from './trade';
import { environment } from '../../../environments/environment';
import {HttpHelperService} from '../../common/http-helper/http-helper.service';
import { catchError } from 'rxjs/operators';

@Injectable()
export class TraderSimulationService {

    constructor( private http: HttpClient, private helper: HttpHelperService) { }

    simulate24h(): Observable<Trade[]> {
        return this.http.get<Trade[]>( this.helper.buildPath( 'routing/trader-simulation/simulate/24h') )
        .pipe(catchError(this.helper.handleError));
    }

    simulate7d(): Observable<Trade[]> {
        return this.http.get<Trade[]>( this.helper.buildPath( 'routing/trader-simulation/simulate/7d') )
        .pipe(catchError(this.helper.handleError));
    }

    simulate30d(): Observable<Trade[]> {
        return this.http.get<Trade[]>( this.helper.buildPath( 'routing/trader-simulation/simulate/30h') )
        .pipe(catchError(this.helper.handleError));
    }

    simulate365d(): Observable<Trade[]> {
        return this.http.get<Trade[]>( this.helper.buildPath( 'routing/trader-simulation/simulate/365d') )
        .pipe(catchError(this.helper.handleError));
    }
}