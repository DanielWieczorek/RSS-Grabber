import { Component } from '@angular/core';
import { Chart } from 'chart.js';

import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { AuthenticationService } from './common/authentication/authentication.service';


@Component( {
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css'],
} )
export class AppComponent{

    isLoggedIn = false;

    constructor( private http: HttpClient, private authentication: AuthenticationService ) {
    }
    
    public logout() {
        this.authentication.logout();
        window.location.reload();

    } 
    
    ngOnInit() {
        this.authentication.isLoggedIn().subscribe(data => this.isLoggedIn = data,
                err => this.isLoggedIn = false);
    }
    
}
