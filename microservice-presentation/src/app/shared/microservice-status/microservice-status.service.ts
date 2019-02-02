import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { MicroserviceStatus} from './microservice-status'

@Injectable()
export class MicroserviceStatusService {

    private protocol = "http"
    private hostname = "localhost";
    private port = 10000;
    
    private cache: Observable<MicroserviceStatus[]>;

    constructor( private http: HttpClient ) { }
    
    getCachedData() : Observable<MicroserviceStatus[]>{
        if(!this.cache) {
            this.status();
        }
        return this.cache;
    }

    status(): Observable<MicroserviceStatus[]> {

        const result: Observable<MicroserviceStatus[]> = this.http.get<MicroserviceStatus[]>( this.buildPath( '/getstatus' ) )
        this.cache = result;
        return result;
    } 

    start( serviceName: string ): Observable<MicroserviceStatus[]> {
        return this.http.post<MicroserviceStatus[]>( this.buildPath( `/start/${serviceName}`) ,{});
    }

    stop( serviceName: string ): Observable<MicroserviceStatus[]>{
        return this.http.post<MicroserviceStatus[]>( this.buildPath( `/stop/${serviceName}`),{});
    }

    private buildPath( path: string ): string {
        return `${this.protocol}://${this.hostname}:${this.port}${path}`
    }

}
