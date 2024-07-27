import { Component } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { User } from 'src/app/model/user.model';
import { AuthenticationService } from 'src/app/services/authentication.service';

@Component({
  selector: 'app-profile-page',
  templateUrl: './profile-page.component.html',
  styleUrls: ['./profile-page.component.sass']
})
export class ProfilePageComponent {
  updateUserForm: FormGroup

  constructor (private auth: AuthenticationService, private router: Router){
    let user = auth.getCurrentUser()

    this.updateUserForm = new FormGroup({
      username: new FormControl(user?.username, [Validators.required]),
      password: new FormControl(user?.password, [Validators.required]),
      email: new FormControl(user?.email, [Validators.email, Validators.required]),
      firstName: new FormControl(user?.name),
      lastName: new FormControl(user?.lastName),
    })
  }

  changeInformation(){
    const data = this.updateUserForm.value;
    // console.log(data)

    if(this.updateUserForm.invalid) {
      window.alert("Formular nije validan");
      return
    }

    let user = this.auth.getCurrentUser()

    if (user == null){
      alert("User missing")
      return
    }

    const observer: Observable<User | null> = this.auth.updateUser(user.id, data.username, data.password, data.firstName, data.lastName, data.email);
      observer.subscribe((user: User | null) => {
        console.log("user is updated")
        if(user==null)
          return
        User.storeUser(user)
        this.router.navigate(['home'])
      }, 
      error => {
        console.log(error)
        alert(error)
      })
  }
}
