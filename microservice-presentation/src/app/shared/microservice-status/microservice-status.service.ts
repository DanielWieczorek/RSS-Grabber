import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class MicroserviceStatusService {

    private protocol = "http"
    private hostname = "localhost";
    private port = 10000;

    constructor( private http: HttpClient ) { }

    status(): Observable<Object> {
        return this.http.get<Object>( this.buildPath( '/getstatus' ) );
    }

    start( serviceName: string ): Observable {
        return this.http.post( this.buildPath( `/start/${serviceName}`) ,{});
    }

    stop( serviceName: string ): Observable{
        return this.http.post( this.buildPath( `/stop/${serviceName}`),{});
    }

    private buildPath( path: string ): string {
        return `${this.protocol}://${this.hostname}:${this.port}${path}`
    }

}
