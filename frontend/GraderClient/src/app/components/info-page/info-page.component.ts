import { DatePipe } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Notification } from 'src/app/model/notification.model';
import { AuthenticationService } from 'src/app/services/authentication.service';

@Component({
  selector: 'app-info-page',
  templateUrl: './info-page.component.html',
  styleUrls: ['./info-page.component.sass']
})
export class InfoPageComponent implements OnInit{
  public notifications: Notification[] = []

  constructor (private auth: AuthenticationService, public datepipe: DatePipe){
    // this.notifications = auth.getCurrentUser()
  }

  ngOnInit(): void {
    this.auth.user.subscribe(
      user => {
        if(user?.notifications != null)
          this.notifications = user?.notifications
     },
     error => {
       console.log(error)
     }
    )   
   }
}
