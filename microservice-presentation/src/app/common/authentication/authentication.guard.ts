
import {map} from 'rxjs/operators';
import { Injectable } from '@angular/core';
import { CanActivate, Router, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Observable, of } from 'rxjs';
import { AuthenticationService } from './authentication.service'
import { catchError } from 'rxjs/operators';


@Injectable()
export class AuthenticationGuard implements CanActivate {
    
  constructor(private authentication : AuthenticationService, private router : Router) { }
    
  canActivate(
      next: ActivatedRouteSnapshot,
      state: RouterStateSnapshot ): Observable<boolean> | Promise<boolean> | boolean {
      return this.authentication.isLoggedIn().pipe(map( isIn => {
          console.log("isIn? ",isIn)
          if ( isIn ) {
              return true;
          } else {
              this.router.navigate( ['/login'] );
          }
      }
      ),
      catchError(val => this.router.navigate( ['/login'])));

  }
}
