import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RssEntry } from './rss-entry';
import {environment} from '../../../environments/environment';


@Injectable()
export class RssClassificationService {
  
    private protocol = "http"
    private hostname = "localhost";
    private port = 10020;

    constructor( private http: HttpClient ) { }

    find(): Observable<RssEntry[]> {
        return this.http.get<RssEntry[]>( this.buildPath( '/find' ) );
    }

    classify( entry: RssEntry ): Observable<RssEntry> {
        return this.http.post<RssEntry>( this.buildPath( '/classify' ), entry );
    }

    private buildPath( path: string ): string {
        return `${this.protocol}://${environment.backendHostname}:${this.port}${path}`
    }

}
