import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';

@Injectable()
export class AuthenticationService {

    
    desiredUsername="admin";
    desiredPassword="123";
    
  constructor() { 
      
      
  }
    
    public login(username : string, password : string) : boolean    {
        if(username === this.desiredUsername && password === this.desiredPassword){
            localStorage.setItem('isLoggedIn',"true");
           
        }
        
        return localStorage.getItem('isLoggedIn') === "true";
    }
    
    public isLoggedIn() : boolean {
        return localStorage.getItem('isLoggedIn') === "true";
    }
    
    public logout() {
        localStorage.removeItem('isLoggedIn');
    }
}
