import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { AuthenticationService } from '../common/authentication/authentication.service';
import { RouterModule, Routes, Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {


    
    form: FormGroup;
    
  constructor(private formBuilder: FormBuilder, 
          private authentication : AuthenticationService,
          private router : Router) { }

  ngOnInit() {
      this.form = this.formBuilder.group({
          username: [null, Validators.required],
          password: [null, Validators.required],
        });
          
      if(this.authentication.isLoggedIn()){
          this.router.navigate(['/introduction']);
      }
  }
  
  submit(){
      
      let wasLoginSuccessful = this.authentication.login(
              this.form.controls.username.value,
              this.form.controls.password.value)
      console.log('username: ',this.form.controls.username.value);
      console.log('password: ',this.form.controls.password.value);
      
      if(wasLoginSuccessful){
          console.log("successful");
          this.router.navigate(['/introduction']);
      } else {
         this.form.controls.username.markAsDirty();
         this.form.controls.password.markAsDirty();
          console.log("failed");
      }
  }

}
