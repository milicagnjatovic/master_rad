import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http'
import { Observable, tap, map, Subject } from 'rxjs';
import { User } from '../model/user.model';
import { Task } from '../model/task.model';
import { Submisson } from '../model/submission.model';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  private readonly urls = {
    login: "http://0.0.0.0:53000/user/login",
    signUp: "http://localhost:53000/user/create"
  }

  private readonly userSubject: Subject<User | null> = new Subject<User | null>();
  public readonly user: Observable<User|null> = this.userSubject.asObservable(); 

  constructor(private http: HttpClient) { }

  public login(username: string, password: string): Observable<User | null>{
    const body = {username, password}

    const obs: Observable<string> = this.http.post<string>(this.urls.login, body);

    return obs.pipe(
      tap( (response: string) => {
        this.userSubject.next(this.parseUser(response))
        console.log(response)
  }),
      map((response: string) => this.parseUser(response))
    )
  }

  public signUp(username: string, password: string, firstName: string, lastName:string, email: string): Observable<User | null>{
    const body = {username, password, "firstname": firstName, "lastname": lastName, email}

    const obs: Observable<string> = this.http.post<string>(this.urls.signUp, body);

    return obs.pipe(
      tap( (response: string) => {
        this.userSubject.next(this.parseUser(response))
        // console.log(response)
  }),
      map((response: string) =>  this.parseUser(response) )
    )
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
    for(let t of obj.tasks){
      tasksList.push(new Task(t.taskId, t.graderId, t.text, t.name))
    }

    let submissionsList: Submisson[] = []
    for(let s of obj.submissions){
      submissionsList.push(new Submisson(s.isWaitingForResponse, s.noTotalSubmissions, s.noCorrect, s.taskId, s.isCorrect))
    }

    return new User(obj.id, obj.username, obj.password, obj.email, obj.firstname, obj.lastname, obj.roleId, tasksList, submissionsList)

  }

}
