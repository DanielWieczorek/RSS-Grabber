import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Account } from './account';
import { Trade } from './trade';
import { environment } from '../../../environments/environment';
import {HttpHelperService} from '../../common/http-helper/http-helper.service';
import { catchError } from 'rxjs/operators';

@Injectable()
export class TraderSimulationService {
    private port = 22020;

    constructor( private http: HttpClient, private helper: HttpHelperService) { }

    simulate(): Observable<Trade[]> {

        return this.http.get<Trade[]>( this.helper.buildPath( '/simulate', this.port ) )
        .pipe(catchError(this.helper.handleError));
    }
}