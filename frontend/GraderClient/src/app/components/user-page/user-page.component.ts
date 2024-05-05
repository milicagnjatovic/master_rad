import { Component, OnInit } from '@angular/core';
import { User } from 'src/app/model/user.model';
import { AuthenticationService } from 'src/app/services/authentication.service';

@Component({
  selector: 'app-user-page',
  templateUrl: './user-page.component.html',
  styleUrls: ['./user-page.component.sass']
})
export class UserPageComponent implements OnInit{
  public user: User | null;
  constructor(private auth: AuthenticationService){
    this.user = null
    console.log('construct user compo')
  }

  ngOnInit(): void {
    console.log("inti")
  //  this.user = this.auth.currentUser()
   console.log(this.user)
   this.auth.user.subscribe(
     user => {
      this.user = user
 
    },
    error => {
      console.log(error)
    }
   )   
  }
}
