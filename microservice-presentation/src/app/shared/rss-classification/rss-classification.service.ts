import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RssEntry } from './rss-entry';
import {environment} from '../../../environments/environment';
import {HttpHelperService} from '../../common/http-helper/http-helper.service';
import { catchError } from 'rxjs/operators';


@Injectable()
export class RssClassificationService {
  
    private port = 10020;

    constructor( private http: HttpClient, private helper: HttpHelperService) { }

    find(): Observable<RssEntry[]> {
        return this.http.get<RssEntry[]>( this.helper.buildPath( '/find',this.port ) )
                 .pipe(catchError(this.helper.handleError));
    }

    classify( entry: RssEntry ): Observable<RssEntry> {
        return this.http.post<RssEntry>( this.helper.buildPath( '/classify', this.port), entry )
                 .pipe(catchError(this.helper.handleError));
    }
}
