import { Component } from '@angular/core';
import { Chart } from 'chart.js';
import 'rxjs/add/operator/map';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { AuthenticationService } from './common/authentication/authentication.service';


@Component( {
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css'],
} )
export class AppComponent{


    constructor( private http: HttpClient, private authentication: AuthenticationService ) {
    }
    
    public logout() {
        this.authentication.logout();
        window.location.reload();

    } 
}
