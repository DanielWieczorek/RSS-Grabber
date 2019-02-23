import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { MicroserviceStatus} from './microservice-status'
import {environment} from '../../../environments/environment';
import {HttpHelperService} from '../../common/http-helper/http-helper.service';
import { catchError } from 'rxjs/operators';

@Injectable()
export class MicroserviceStatusService {

    private port = 10000;
    
    private cache: Observable<MicroserviceStatus[]>;

    constructor( private http: HttpClient, private helper: HttpHelperService ) { }
    
    getCachedData() : Observable<MicroserviceStatus[]>{
        if(!this.cache) {
            this.status();
        }
        return this.cache;
    }

    status(): Observable<MicroserviceStatus[]> {

        const result: Observable<MicroserviceStatus[]> = this.http.get<MicroserviceStatus[]>( this.helper.buildPath( '/getstatus',this.port ) )
         .pipe(catchError(this.helper.handleError));
        this.cache = result;
        return result;
    } 

    start( serviceName: string ): Observable<MicroserviceStatus[]> {
        return this.http.post<MicroserviceStatus[]>( this.helper.buildPath( `/start/${serviceName}`, this.port) ,{})
         .pipe(catchError(this.helper.handleError));
    }

    stop( serviceName: string ): Observable<MicroserviceStatus[]>{
        return this.http.post<MicroserviceStatus[]>( this.helper.buildPath( `/stop/${serviceName}`, this.port),{})
        .pipe(catchError(this.helper.handleError));
    }
}
