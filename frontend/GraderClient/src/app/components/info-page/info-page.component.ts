import { DatePipe } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Notification } from 'src/app/model/notification.model';
import { AuthenticationService } from 'src/app/services/authentication.service';

@Component({
  selector: 'app-info-page',
  templateUrl: './info-page.component.html',
  styleUrls: ['./info-page.component.sass']
})
export class InfoPageComponent implements OnInit{
  public notifications: Notification[] = []

  newNotificationForm:FormGroup


  constructor (private auth: AuthenticationService, public datepipe: DatePipe){
    this.newNotificationForm = new FormGroup({
      title: new FormControl("", [Validators.required]),
      text: new FormControl("", [Validators.required])
    })  
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

   onSubmit(): void {
    if(this.newNotificationForm.invalid){
      alert("Oba polja su obavezna.")
      return
    }

    let body = {
      text: this.newNotificationForm.value.text, 
      title: this.newNotificationForm.value.title,
      createdDate: new Date()
    }
    
    let notification: Notification = new Notification(body)

    this.auth.addNotification(JSON.stringify(body)).subscribe(
      ret => {
        let response = JSON.parse(JSON.stringify(ret))
        if('error' in response && response.error != '')
          window.alert(response.error)
        else {
          this.newNotificationForm.reset()
          this.auth.addNotificationToLocalUser(notification)
          window.alert("Uspešno postavljeno obaveštenje.")
        }
      }
    )
   }
}
