import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Stock } from './stock';
import { environment } from '../../../environments/environment';
import {HttpHelperService} from '../../common/http-helper/http-helper.service';
import { catchError } from 'rxjs/operators';

@Injectable()
export class SkinbaronLiveService {

    constructor( private http: HttpClient, private helper: HttpHelperService) { }

    getAllStock(): Observable<Stock[]> {
        return this.http.get<Stock[]>( this.helper.buildPath( 'routing/skinbaron-live/stock/all') )
        .pipe(catchError(this.helper.handleError));
    }

 }