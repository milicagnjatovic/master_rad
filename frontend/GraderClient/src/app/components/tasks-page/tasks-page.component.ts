import { Component, OnInit } from '@angular/core';
import { Task } from 'src/app/model/task.model';
import { User } from 'src/app/model/user.model';
import { AuthenticationService } from 'src/app/services/authentication.service';

@Component({
  selector: 'app-tasks-page',
  templateUrl: './tasks-page.component.html',
  styleUrls: ['./tasks-page.component.sass']
})
export class TasksPageComponent implements OnInit{
  public user: User | null = null;
  public currentTask: Task | null; 
  constructor(private auth: AuthenticationService){
    this.user = auth.getCurrentUser()
    this.currentTask = null
  }

  ngOnInit(): void {
   console.log(User.retreiveUser())
   this.auth.user.subscribe(
     user => {
      this.user = user
      // console.log(user)
    },
    error => {
      console.log(error)
    }
   )   
  }

  public setCurrentTask(task: Task | null){
    this.currentTask = task
    // console.log(this.currentTask)
  }
}
