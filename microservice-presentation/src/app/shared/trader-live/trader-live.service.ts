import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LiveTrade } from './live-trade';
import { LiveAccount } from './live-account';
import { environment } from '../../../environments/environment';
import {HttpHelperService} from '../../common/http-helper/http-helper.service';
import { catchError } from 'rxjs/operators';

@Injectable()
export class TraderLiveService {

    constructor( private http: HttpClient, private helper: HttpHelperService) { }

    getTrades24h(): Observable<LiveTrade[]> {
        return this.http.get<LiveTrade[]>( this.helper.buildPath( 'routing/trader-live/trading/24h') )
        .pipe(catchError(this.helper.handleError));
    }

    getAccount24h(): Observable<LiveAccount[]> {
        return this.http.get<LiveAccount[]>( this.helper.buildPath( 'routing/trader-live/account/24h') )
        .pipe(catchError(this.helper.handleError));
    }
}