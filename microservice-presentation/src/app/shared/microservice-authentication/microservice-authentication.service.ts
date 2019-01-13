import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SessionInfo } from './session-info';


@Injectable()
export class MicroserviceAuthenticationService {

    private protocol = "http";
    private hostname = "localhost";
    private port = 32000;

    constructor( private http: HttpClient ) { }

    login( username: string, password: string ): Observable<string> {
        const base64String = btoa( `${username}:${password}` );
        const authHeaders = new HttpHeaders({'Authorization': `Basic ${base64String}`});
        
        return this.http.get( this.buildPath( '/login' ), { headers: authHeaders,
            responseType: 'text' } )
       ;
    } 

    logout( username: string, token: string ) {
        const info = new SessionInfo();
        info.username = username;
        info.token = token;

        this.http.post( this.buildPath( '/logout' ), info ).subscribe(response => console.log(response));
    }

    validate( username: string, token: string ): Observable<boolean> {
        const info = new SessionInfo();
        info.username = username;
        info.token = token;
       
        return this.http.post( this.buildPath( '/validate' ), info ,{ responseType: 'text'}).map (resp =>  {
            console.log("validate ",resp=== 'true');
            return resp === 'true'});
    }
 
    private buildPath( path: string ): string {
        let result = `${this.protocol}://${this.hostname}:${this.port}${path}`;
        return result;
    }
}
