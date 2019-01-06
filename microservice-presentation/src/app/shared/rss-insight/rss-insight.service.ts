import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RssEntry,RssEntrySentiment,RssEntrySentimentSummary,SentimentEvaluationResult } from './rss-entry';

@Injectable()
export class RssInsightService {

    private protocol = "http"
    private hostname = "localhost";
    private port = 11020;

    constructor( private http: HttpClient ) { }

    sentiment(): Observable<SentimentEvaluationResult> {
        return this.http.get<SentimentEvaluationResult>( this.buildPath( '/sentiment' ) );
    }

    private buildPath( path: string ): string {
        return `${this.protocol}://${this.hostname}:${this.port}${path}`
    }

}
