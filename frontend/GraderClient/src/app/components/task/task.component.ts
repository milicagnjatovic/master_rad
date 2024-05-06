import { Component, Input } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
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

  checkTaskForm: FormGroup

  constructor(private submissionService: TaskSubmissionsService, private authService: AuthenticationService){
    this.checkTaskForm = new FormGroup({
      file: new FormControl("", [])
    })
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

  onFilePicked(event: any){
    let reader = new FileReader()
    reader.onload = () => {
      console.log(reader.result)
      this.task?.setSubmissionSolution(reader.result as string)
    }
    reader.readAsText(event.target.files[0])    
  }
}
 