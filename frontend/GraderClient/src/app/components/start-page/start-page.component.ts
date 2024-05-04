import { Component } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Observable, Subscription } from 'rxjs';
import { User } from 'src/app/model/user.model';
import { AuthenticationService } from 'src/app/services/authentication.service';

@Component({
  selector: 'app-start-page',
  templateUrl: './start-page.component.html',
  styleUrls: ['./start-page.component.sass']
})
export class StartPageComponent {
  loginForm: FormGroup
  signUpForm: FormGroup

  subscription: Subscription = new Subscription();

  constructor(private auth : AuthenticationService,
    private router: Router
  ){
    this.loginForm = new FormGroup({
      username: new FormControl("mi18009", [Validators.required]),
      password: new FormControl("12345", [Validators.required])
    })

    this.signUpForm = new FormGroup({
      username: new FormControl("mi18009", [Validators.required]),
      password: new FormControl("12345", [Validators.required]),
      email: new FormControl("", [Validators.email, Validators.required]),
      firstName: new FormControl(""),
      lastName: new FormControl(""),
    })
  }

  signIn(){
    const data = this.loginForm.value;

    if(this.loginForm.invalid) {
      window.alert("Oba polja su obavezna");
      return
    }

    const observer: Observable<User | null> = this.auth.login(data.username, data.password);

    this.subscription = observer.subscribe((user: User | null) => {
      console.log("logged in")
      this.router.navigate(['task-page'])

    }, 
  error => {
    console.log(error)
    alert(error)
  })
  }

  signUp(){
    const data = this.signUpForm.value;
    // console.log(data)

    if(this.loginForm.invalid) {
      window.alert("Oba polja su obavezna");
      return
    }

    const observer: Observable<User | null> = this.auth.signUp(data.username, data.password, data.firstName, data.lastName, data.email);

    this.subscription = observer.subscribe((user: User | null) => {
      console.log("retreived")
      this.router.navigate(['task-page'])
    }, 
  error => {
    console.log(error)
    alert(error)
  })
  }
}
