import { Injectable } from '@angular/core';
import { CanActivate, Router, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs/Observable';
import { AuthenticationService } from './authentication.service'

@Injectable()
export class AuthenticationGuard implements CanActivate {
    
  constructor(private authentication : AuthenticationService, private router : Router) { }
    
  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {
    if(this.authentication.isLoggedIn()) {
        return true;
    } else {
        this.router.navigate(['/login']);
    }
    
  }
}
