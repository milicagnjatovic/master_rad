import { DatePipe } from '@angular/common';
import { Component } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Message } from 'src/app/model/message.model';
import { User } from 'src/app/model/user.model';
import { AuthenticationService } from 'src/app/services/authentication.service';
import { TaskSubmissionsService } from 'src/app/services/task-submissions.service';

@Component({
  selector: 'app-professor-messages-page',
  templateUrl: './professor-messages-page.component.html',
  styleUrls: ['./professor-messages-page.component.sass']
})
export class ProfessorMessagesPageComponent {
  public user : User | null = null
  public currentMessage : Message | null = null

  respondForm:FormGroup

  constructor(public authService: AuthenticationService, public datepipe: DatePipe, public taskService: TaskSubmissionsService){
    this.user = authService.getCurrentUser()
    this.respondForm = new FormGroup({
      response: new FormControl("", [Validators.required])
    })
  }


  public setCurrentMessage(message: Message | null){
    console.log(message)
    this.currentMessage = message
  }

  public onResponde(){
    if(this.respondForm.invalid){
      alert("Odgovor je obavezan.")
      return
    }

    let request = {
      "userId": this.currentMessage?.userId,
      "professorId": this.user?.id,
      "taskId": this.currentMessage?.taskId,
      "response": this.respondForm.value.response
    }

    this.taskService.respondToQuestion(JSON.stringify(request)).subscribe(
      ret => {
        console.log("Odgovor")
        let response = JSON.parse(JSON.stringify(ret));
        if('error' in response && response.error != null && response.error != ''){
          alert(response.error)
        } else {
          if(this.user != null)
            this.user.messagesForProfessor = this.user.messagesForProfessor.filter(m => !(m.userId==this.currentMessage?.userId && m.taskId == this.currentMessage?.taskId))
          User.storeUser(this.user)
          this.setCurrentMessage(null)
        }
      }
    )

  }
}
