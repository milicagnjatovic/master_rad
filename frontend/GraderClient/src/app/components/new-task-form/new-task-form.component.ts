import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { User } from 'src/app/model/user.model';
import { AuthenticationService } from 'src/app/services/authentication.service';
import { Grader } from 'src/app/model/grader.model';
import { TaskSubmissionsService } from 'src/app/services/task-submissions.service';
import { catchError, of } from 'rxjs';
import { Task } from 'src/app/model/task.model';
import { TaskComponent } from '../task/task.component';

@Component({
  selector: 'app-new-task-form',
  templateUrl: './new-task-form.component.html',
  styleUrls: ['./new-task-form.component.sass']
})
export class NewTaskFormComponent implements OnChanges{
  @Input() task : Task | null = null

  public user: User | null = null
  public insertNewTaskForm: FormGroup

  public graders: Grader[] = []

  public serverResponse: string | unknown = ''

  public responseClass = 'green-border'

  constructor(private auth: AuthenticationService, private taskService: TaskSubmissionsService){
    this.user = auth.getCurrentUser()
    
    console.log("TASK")
    console.log(this.task)

    this.insertNewTaskForm = new FormGroup({
      name: new FormControl(this.task==null ? '' : this.task.name, [Validators.maxLength(45), Validators.required]),
      text: new FormControl(this.task==null ? '' : this.task.text, [Validators.maxLength(2950), Validators.required]),
      solution: new FormControl('', [Validators.maxLength(2950), Validators.required]),
      ordering: new FormControl(this.task==null ? '' : this.task.ordering, [Validators.maxLength(15)]),
      grader: new FormControl(this.task==null ? '' : this.task.graderId, [Validators.required]),    
      active: new FormControl(true, [])  
    })
    this.graders = Grader.graders
    console.log(Grader.graders)
  }

  ngOnChanges(changes: SimpleChanges): void {
    if(changes['task'] && changes['task']!=null){
      console.log('changed')
      console.log(this.task)
      this.insertNewTaskForm = new FormGroup({
        name: new FormControl(this.task==null ? '' : this.task.name, [Validators.maxLength(45), Validators.required]),
        text: new FormControl(this.task==null ? '' : this.task.text, [Validators.maxLength(2950), Validators.required]),
        solution: new FormControl(this.task != null ? this.user?.taskSolutions.get(this.task?.id) : '', [Validators.maxLength(2950), Validators.required]),
        ordering: new FormControl(this.task==null ? '' : this.task.ordering, [Validators.maxLength(15)]),
        grader: new FormControl(this.task==null ? '' : this.task.graderId, [Validators.required]),    
        active: new FormControl(true, [])  
      })
    }
  }

  sendRequest(){
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
    
    let requestObject = null
    
    if(this.task==null) {
      requestObject = {
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
    } else {
      requestObject = {
        graderId: Number.parseInt(data.grader),
        tasks: [
            {
              taskId: this.task.id,
              name: data.name,
              task: data.text,
              solution: data.solution,
              ordering: data.ordering,
              active: data.active
            }
          ]
        }
    }

    // console.log(requestObject)
    // console.log(JSON.stringify(requestObject))

    this.taskService.addTask(JSON.stringify(requestObject), this.task==null).pipe(
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

  cancel(){
    TaskComponent.changeTask = false
  }

}
