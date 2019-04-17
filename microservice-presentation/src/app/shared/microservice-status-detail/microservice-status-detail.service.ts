import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {HttpHelperService} from '../../common/http-helper/http-helper.service';

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
