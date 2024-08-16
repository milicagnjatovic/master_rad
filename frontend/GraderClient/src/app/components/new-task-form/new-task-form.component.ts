import { Component } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { User } from 'src/app/model/user.model';
import { AuthenticationService } from 'src/app/services/authentication.service';
import { Grader } from 'src/app/model/grader.model';
import { TaskSubmissionsService } from 'src/app/services/task-submissions.service';
import { catchError, of } from 'rxjs';

@Component({
  selector: 'app-new-task-form',
  templateUrl: './new-task-form.component.html',
  styleUrls: ['./new-task-form.component.sass']
})
export class NewTaskFormComponent {
  public user: User | null = null
  public insertNewTaskForm: FormGroup

  public graders: Grader[] = []

  public serverResponse: string | unknown = ''

  public responseClass = 'green-border'

  constructor(private auth: AuthenticationService, private taskService: TaskSubmissionsService){
    this.user = auth.getCurrentUser()
    this.insertNewTaskForm = new FormGroup({
      name: new FormControl("abc", [Validators.maxLength(45), Validators.required]),
      text: new FormControl("c", [Validators.maxLength(2950), Validators.required]),
      solution: new FormControl("d", [Validators.maxLength(2950), Validators.required]),
      ordering: new FormControl("f", [Validators.maxLength(15)]),
      grader: new FormControl("1", [Validators.required]),    
      active: new FormControl(true, [])  
    })
    this.graders = Grader.graders
    console.log(Grader.graders)
  }

  insertTask(){
    if(this.insertNewTaskForm.invalid){
      let errorMessage = `Invalid form:
name: ${this.insertNewTaskForm.controls['name'].invalid ? 'invalid' : 'valid'}
text: ${this.insertNewTaskForm.controls['text'].invalid ? 'invalid' : 'valid'}
solution: ${this.insertNewTaskForm.controls['solution'].invalid ? 'invalid' : 'valid'}
ordering: ${this.insertNewTaskForm.controls['ordering'].invalid ? 'invalid' : 'valid'}
grader: ${this.insertNewTaskForm.controls['grader'].invalid ? 'invalid' : 'valid'}
      `
      window.alert(errorMessage)
      console.log(this.insertNewTaskForm.value.grader)
      return
    }

    let user = this.auth.getCurrentUser()
    if(user==null){
      alert("Korisnik nije pronadjen")
      return
    }

    const data = this.insertNewTaskForm.value;
    const requestObject = {
      graderId: Number.parseInt(data.grader),
      tasks: [
        {
          name: data.name,
          task: data.text,
          solution: data.solution,
          ordering: data.ordering,
          active: data.active
        }
      ]
    }
    console.log(requestObject)
    console.log(JSON.stringify(requestObject))

    this.taskService.addTask(JSON.stringify(requestObject)).pipe(
      catchError(error => {
        console.log(error)
        console.log(error.message)
        return of([error.message])
      })
    ).subscribe(
      ret => {
        console.log(ret)
        let retObj = JSON.parse(JSON.stringify(ret))
        console.log("parsed")
        console.log(retObj)
        if(retObj.errors.length > 0 || 'graderResponse' in retObj.graderResponse || ('tasks' in retObj.graderResponse && 'error' in retObj.graderResponse.tasks[0])){
          this.responseClass = 'red-border'
        } else {
          this.responseClass = 'green-border'
        }
        this.serverResponse = `
Odgovor:
Odgovor servera: ${retObj.errors.length > 0 ? retObj.errors[0] : "Nema serverskih grešaka"}

Odgovor ocenjivača: 
Greške: ${'tasks' in retObj.graderResponse &&  "error" in retObj.graderResponse.tasks[0] ? retObj.graderResponse.tasks[0].error : ('graderResponse' in retObj.graderResponse ? retObj.graderResponse.graderResponse : 'nema')}
Identifikator zadatka: ${'tasks' in retObj.graderResponse && "taskId" in retObj.graderResponse.tasks[0] ? retObj.graderResponse.tasks[0].taskId : 'x'}
Broj redova: ${'tasks' in retObj.graderResponse &&  "noRows" in retObj.graderResponse.tasks[0] ? retObj.graderResponse.tasks[0].noRows : 'x'}
Vreme izvršavanja: ${'tasks' in retObj.graderResponse && "time" in retObj.graderResponse.tasks[0] ? retObj.graderResponse.tasks[0].time : 'x'}
`
        
      }
    )
  }

}
