import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {environment} from '../../../environments/environment';
import {HttpHelperService} from '../../common/http-helper/http-helper.service';
import { catchError } from 'rxjs/operators';

@Injectable()
export class MicroserviceStatusDetailService {
    
    constructor( private http: HttpClient, private helper: HttpHelperService ) { }

    performGetAction(path : string, port: number) : any {
        return this.http.get( this.helper.buildPath( path, port) );
    }
    
    performPostAction(path : string, data: string, port: number) : any {
        return this.http.post( this.helper.buildPath( path, port) ,data, {headers:{'Content-Type': 'application/json'}});
    }
}
