import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Account } from './account';
import { Trade } from './trade';

@Injectable()
export class TraderSimulationService {
    private protocol = "http"
    private hostname = "localhost";
    private port = 22020;

    constructor( private http: HttpClient ) { }

    simulate(): Observable<Trade[]> {

        return this.http.get<Trade[]>( this.buildPath( '/simulate' ) );
    }

    private buildPath( path: string ): string {
        return `${this.protocol}://${this.hostname}:${this.port}${path}`
    }

}