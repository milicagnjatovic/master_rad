import { Component, Input } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { Message } from 'src/app/model/message.model';
import { Professor } from 'src/app/model/professor.model';
import { Task } from 'src/app/model/task.model';
import { AuthenticationService } from 'src/app/services/authentication.service';
import { TaskSubmissionsService } from 'src/app/services/task-submissions.service';

@Component({
  selector: 'app-task',
  templateUrl: './task.component.html',
  styleUrls: ['./task.component.sass'],
})
export class TaskComponent {
  @Input() task : Task | null = null

  public professors : Professor[] = []

  checkTaskForm: FormGroup
  askQuestionForm: FormGroup

  constructor(private submissionService: TaskSubmissionsService, private authService: AuthenticationService){
    this.checkTaskForm = new FormGroup({
      file: new FormControl("", [])
    })
    this.askQuestionForm = new FormGroup({
      question: new FormControl("", []),
      professorId: new FormControl("", [])
    })
    let user = authService.getCurrentUser()
    if(user != null)
      this.professors = user.professors
  }

  checkTask(){
    if (this.checkTaskForm.invalid){
      alert("Nedostaje fajl")
      return
    }
    if(this.task?.submission == null){
      alert("Fali submission")
      return
    }
    let user = this.authService.getCurrentUser()
    if(user==null){
      alert("Korisnik nije pronadjen")
      return
    }
    let request = this.task.submission.prepareJsonRequest(user?.id, this.task.ordering);
    this.submissionService.checkTask(request, this.task.submission).subscribe(
      ret => {
        console.log('vracen odgovor')
        console.log(ret)
      }
    )
  }

  public showAskQuestion = false;
  showAskQuestionForm(){
    this.showAskQuestion = !this.showAskQuestion && this.task?.submission?.question==null
  }

  askQuestion(){
    if(this.task?.submission == null){
      alert("Fali submission")
      return
    }
    let user = this.authService.getCurrentUser()
    if(user==null){
      alert("Korisnik nije pronadjen")
      return
    }

    const data = this.askQuestionForm.value;

    let question = data.question;
    let professorId = data.professorId;
    let request = this.task.submission.prepareJsonRequestForAskingQuestion(this.task.id, professorId, user.id, question);
    let message = new Message(data)
    this.task.submission.question = message
    this.authService.askQuestion(this.task.submission)
    this.submissionService.askQuestion(request).subscribe(
      ret => {
        console.log('vracen odgovor')
        let response = JSON.parse(JSON.stringify(ret))
        if('error' in response && response.error != null && response.error != ""){
          alert(response.error)
        }
        this.showAskQuestion = false
        console.log(ret)
      }
    )
  }

  deleteQuestion(){
    if(this.task?.submission == null){
      alert("Fali submission")
      return
    }
    let user = this.authService.getCurrentUser()
    if(user==null){
      alert("Korisnik nije pronadjen")
      return
    }

    let request = this.task.submission.prepareJsonRequestForDeletingQuestion(this.task.id, user.id);
    // this.task.submission.question = null
    // this.authService.deleteQuestion(this.task.submission) 
    this.submissionService.deleteQuestion(request).subscribe(
      ret => {
        console.log('vracen odgovor')
        let response = JSON.parse(JSON.stringify(ret))
        if('error' in response && response.error != null && response.error != ""){
          alert(response.error)
        } else {
          if (this.task != null && this.task.submission != null) {
            this.task.submission.question = null
            this.authService.deleteQuestion(this.task.submission)
          }
        }
        this.showAskQuestion = false
        console.log(ret)
      }
    )
  }


  onFilePicked(event: any){
    let reader = new FileReader()
    reader.onload = () => {
      this.task?.setSubmissionSolution(reader.result as string)
    }
    reader.readAsText(event.target.files[0])    
  }
}
 