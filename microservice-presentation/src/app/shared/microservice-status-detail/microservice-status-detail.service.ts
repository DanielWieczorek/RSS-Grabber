import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {environment} from '../../../environments/environment';
import {HttpHelperService} from '../../common/http-helper/http-helper.service';
import { catchError } from 'rxjs/operators';

@Injectable()
export class MicroserviceStatusDetailService {
    
    constructor( private http: HttpClient, private helper: HttpHelperService ) { }

    performGetAction(path : string) : any {
        return this.http.get( this.helper.buildPath( path) );
    }
    
    performPostAction(path : string, data: string) : any {
        return this.http.post( this.helper.buildPath( path) ,data, {headers:{'Content-Type': 'application/json'}});
    }
}
