import { Injectable } from '@angular/core';

@Injectable()
export class AuthenticationService {

    
    desiredUsername="admin";
    desiredPassword="123";
    
  constructor() { }
    
    public login(username : string, password : string) : boolean {
        if(username === this.desiredUsername && password === this.desiredPassword){
            localStorage.setItem('isLoggedIn',"true");
        }
        return localStorage.getItem('isLoggedIn') === "true";
    }
    
    public isLoggedIn() : boolean {
        console.log("isLoggedIn: ", localStorage.getItem('isLoggedIn'))
        return localStorage.getItem('isLoggedIn')=== "true";
    }
}
