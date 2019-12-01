import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RssEntry } from './rss-entry';
import { ClassificationStatistics } from './classification-statistics';

import {environment} from '../../../environments/environment';
import {HttpHelperService} from '../../common/http-helper/http-helper.service';
import { catchError } from 'rxjs/operators';


@Injectable()
export class RssClassificationService {
  
    private port = 10020;

    constructor( private http: HttpClient, private helper: HttpHelperService) { }

    find(): Observable<RssEntry[]> {
        return this.http.get<RssEntry[]>( this.helper.buildPath( 'routing/rss-classification/find') )
                 .pipe(catchError(this.helper.handleError));
    }

    statistics(): Observable<ClassificationStatistics[]> {
        return this.http.get<ClassificationStatistics[]>( this.helper.buildPath( 'routing/rss-classification/statistics') )
                 .pipe(catchError(this.helper.handleError));
    }

    classify( entry: RssEntry ): Observable<RssEntry> {
        return this.http.post<RssEntry>( this.helper.buildPath( 'routing/rss-classification/classify'), entry )
                 .pipe(catchError(this.helper.handleError));
    }
}
