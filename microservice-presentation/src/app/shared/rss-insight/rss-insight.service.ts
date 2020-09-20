import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RssEntry,RssEntrySentiment,RssEntrySentimentSummary,SentimentEvaluationResult } from './rss-entry';
import {environment} from '../../../environments/environment';
import {HttpHelperService} from '../../common/http-helper/http-helper.service';
import { catchError } from 'rxjs/operators';


@Injectable()
export class RssInsightService {

    constructor( private http: HttpClient, private helper: HttpHelperService) { }

    sentiment(): Observable<SentimentEvaluationResult> {
        return this.http.get<SentimentEvaluationResult>( this.helper.buildPath( 'routing/rss-insight/sentiment/now') )
        .pipe(catchError(this.helper.handleError));

    }
}
