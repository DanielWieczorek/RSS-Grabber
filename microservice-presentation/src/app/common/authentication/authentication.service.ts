
import {of as observableOf,  Observable, Subject } from 'rxjs';
import { Injectable } from '@angular/core';
import { MicroserviceAuthenticationService } from '../../shared/microservice-authentication/microservice-authentication.service'


@Injectable()
export class AuthenticationService {

  constructor(private microserviceAuthentication : MicroserviceAuthenticationService) { 
      
      
  }
      
    public login(username : string, password : string) : Observable<boolean>    {
        let result = new Subject<boolean>();
        this.microserviceAuthentication.login(username, password).subscribe(token => {
           console.log('token',token);
           if(token){
               localStorage.setItem('token',token);
               localStorage.setItem('username',username);
           }
           
           this.isLoggedIn().subscribe(res => {
               result.next(res);
           });
       })
        
        return result.asObservable();
    }
    
    public isLoggedIn() : Observable<boolean> {
        console.log("isLoggedIn? token=",localStorage.getItem('token'));
        if( localStorage.getItem('username') === null && localStorage.getItem('token') === null){
            return observableOf(false);
        }

        return this.microserviceAuthentication.validate(localStorage.getItem('username'),localStorage.getItem('token'));
    }
    
    public logout() {
        this.microserviceAuthentication.logout(localStorage.getItem('username'),localStorage.getItem('token'))
        localStorage.removeItem('token');
        localStorage.removeItem('username');
    }
}
