import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LiveTrade } from './live-trade';
import { environment } from '../../../environments/environment';
import {HttpHelperService} from '../../common/http-helper/http-helper.service';
import { catchError } from 'rxjs/operators';

@Injectable()
export class TraderLiveService {

    constructor( private http: HttpClient, private helper: HttpHelperService) { }

    get24h(): Observable<LiveTrade[]> {
        return this.http.get<LiveTrade[]>( this.helper.buildPath( 'routing/trader-live/24h') )
        .pipe(catchError(this.helper.handleError));
    }
}