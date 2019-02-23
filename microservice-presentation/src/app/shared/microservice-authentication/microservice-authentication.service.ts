
import {map} from 'rxjs/operators';
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SessionInfo } from './session-info';
import {environment} from '../../../environments/environment';
import {HttpHelperService} from '../../common/http-helper/http-helper.service';
import { catchError } from 'rxjs/operators';




@Injectable()
export class MicroserviceAuthenticationService {
    private port = 32000;

    constructor( private http: HttpClient, private helper: HttpHelperService ) { }

    login( username: string, password: string ): Observable<string> {
        const base64String = btoa( `${username}:${password}` );
        const authHeaders = new HttpHeaders({'Authorization': `Basic ${base64String}`});
        
        return this.http.get( this.helper.buildPath( '/login', this.port), { headers: authHeaders,
            responseType: 'text' } )
            .pipe(catchError(this.helper.handleError));
       ;
    } 

    logout( username: string, token: string ) {
        const info = new SessionInfo();
        info.username = username;
        info.token = token;

        this.http.post( this.helper.buildPath( '/logout', this.port ), info )
        .pipe(catchError(this.helper.handleError));
    }

    validate( username: string, token: string ): Observable<boolean> {
        const info = new SessionInfo();
        info.username = username;
        info.token = token;
        console.log('validate');
       
        return this.http.post( this.helper.buildPath( '/validate',this.port ), info ,{ responseType: 'text'})
        .pipe(catchError(this.helper.handleError))
        .pipe(map (resp =>  {
            console.log("validate ",resp=== 'true');
            return resp === 'true'}));
    }
}
