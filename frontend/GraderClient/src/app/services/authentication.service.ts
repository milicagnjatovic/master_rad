import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http'
import { Observable, tap, map, Subject, BehaviorSubject } from 'rxjs';
import { User } from '../model/user.model';
import { Task } from '../model/task.model';
import { Submisson } from '../model/submission.model';
import { Message } from '../model/message.model';
import { Professor } from '../model/professor.model';
import { Notification } from '../model/notification.model';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  public static SERVER_ADDRESS = "http://grader.matf.bg.ac.rs:5200"

  private readonly urls = {
    login: `${AuthenticationService.SERVER_ADDRESS}/user/login`,
    signUp: `${AuthenticationService.SERVER_ADDRESS}/user/create`,
    updateUser: `${AuthenticationService.SERVER_ADDRESS}/user/update`
  }

  private readonly userSubject: BehaviorSubject<User | null> = new BehaviorSubject<User | null>(null);
  public readonly user: Observable<User|null> = this.userSubject.asObservable(); 

  constructor(private http: HttpClient) { }

  public login(username: string, password: string): Observable<User | null>{
    const body = {username, password}

    const obs: Observable<string> = this.http.post<string>(this.urls.login, body);

    return obs.pipe(
      map((response: string) => { 
        let user = this.parseUser(response)
        this.userSubject.next(user)
        return user
      })
    )
  }

  public signUp(username: string, password: string, firstName: string, lastName:string, email: string): Observable<User | null>{
    const body = {username, password, "firstname": firstName, "lastname": lastName, email}

    const obs: Observable<string> = this.http.post<string>(this.urls.signUp, body);

    return obs.pipe(
      map((response: string) =>  {
        let user = this.parseUser(response) 
        this.userSubject.next(user)
        return user
      })
    )
  }

  public updateUser(id: Number, username: string, password: string, firstName: string, lastName:string, email: string): Observable<User | null>{
    const body = {username, password, "firstname": firstName, "lastname": lastName, email, id}

    const obs: Observable<string> = this.http.post<string>(this.urls.updateUser, body);

    return obs.pipe(
      map((response: string) =>  {
        let resp = JSON.parse(JSON.stringify(response))
        if('error' in resp){
          alert(resp.error)
          return null;
        }

        let user = this.parseUser(response) 
        this.userSubject.next(user)
        return user
      })
    )
  }

  public logout(){
    this.userSubject.next(null)
    User.logout()
  }

  public parseUser(payload : string) : User | null {
    console.log("payload")
    console.log(payload)
    console.log(typeof payload)

    if(!payload){
      return null 
    }

    const obj = JSON.parse(JSON.stringify(payload));

    if('error' in obj){
      alert(obj.error);
      return null;
    }

    let tasksList: Task[] = [];
    if ('tasks' in obj.tasks) {
      for(let t of obj.tasks.tasks){
        tasksList.push(new Task(t.taskId, t.graderId, t.text, t.name, t.ordering))
      }
    }

    let submissionsList: Submisson[] = []
    for(let s of obj.submissions){
      let submission = new Submisson(s.isWaitingForResponse, s.noTotalSubmissions, s.noCorrect, s.taskId, s.isCorrect, s.message)
      if ('question' in s && s.question != null) {
        submission.question = new Message(s.question)
      }
      submissionsList.push(submission)
    }

    let storedUser = new User(obj.id, obj.username, obj.password, obj.email, obj.firstname, obj.lastname, obj.roleId, tasksList, submissionsList)

    let professors: Professor[] = []
    if('professors' in obj.tasks){
      for(let p of obj.tasks.professors){
        professors.push(new Professor(p.username, p.id))
      }
    }

    storedUser.professors = professors;

    let notificationList: Notification[] = []
    if(obj.tasks != null && 'notifications' in obj.tasks){
      for(let n of obj.tasks.notifications){
        let notification = new Notification(n)
        notificationList.push(notification)
      }
    }
    storedUser.notifications = notificationList

    console.log('retreived')
    console.log(storedUser)
    this.userSubject.next(storedUser);
    return storedUser
  }

  public updateSubmission(submission: Submisson): void {
    let user = this.getCurrentUser();

    if(user?.tasks == null)
      return

    for(let t of user.tasks){
      if(t.id == submission.taskId) {
        t.submission = submission;

        this.userSubject.next(user)
        User.storeUser(user)
        break
      } 
    }
  }

  public setSubmissionWaiting(submission: Submisson){
    let user = this.getCurrentUser();

    if(user?.tasks == null)
      return

    for(let t of user.tasks){
      if(t.id == submission.taskId) {
        if(t.submission){
          t.submission.isWaiting = true;
          break;
        }
    }}
    this.userSubject.next(user)
  }

  public askQuestion(submission: Submisson){
    let user = this.getCurrentUser();

    if(user?.tasks == null)
      return

    for(let t of user.tasks){
      if(t.id == submission.taskId && t.submission) {
        t.submission.question = submission.question
    }}
    this.userSubject.next(user)
  }

  getCurrentUser() : User | null {
    let user = this.userSubject.value;
    if(user == null){
      user = User.retreiveUser();
    }
    this.userSubject.next(user)
    return user;
  }

}
