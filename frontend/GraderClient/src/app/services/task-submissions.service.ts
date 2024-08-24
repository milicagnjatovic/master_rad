import { Injectable } from '@angular/core';
import { Submisson } from '../model/submission.model';
import { BehaviorSubject, Observable, map, tap } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { AuthenticationService } from './authentication.service';

@Injectable({
  providedIn: 'root'
})
export class TaskSubmissionsService {
  private readonly urls = {
    checkTask: `${AuthenticationService.SERVER_ADDRESS}/server/checkTask`,
    askQuestion: `${AuthenticationService.SERVER_ADDRESS}/message/askQuestion`,
    deleteQuestion: `${AuthenticationService.SERVER_ADDRESS}/message/deleteQuestion`,
    respondToQuestion: `${AuthenticationService.SERVER_ADDRESS}/message/respondToQuestion`,
    addTask: `${AuthenticationService.SERVER_ADDRESS}/server/addTasks`,
    updateTask: `${AuthenticationService.SERVER_ADDRESS}/server/updateTasks`
  }

  constructor(private http : HttpClient, private auth: AuthenticationService) { }

  public checkTask(request: string, submissoin: Submisson){
    const body = JSON.parse(request);
    const obs: Observable<string> = this.http.post<string>(this.urls.checkTask, body);
    
    this.auth.setSubmissionWaiting(submissoin);

    return obs.pipe(
      map((response: string) => {
        this.parseResponse(response, submissoin)
    })
    )

  }

  public askQuestion(request: string){
    const body = JSON.parse(request)

    const obs: Observable<string> = this.http.post<string>(this.urls.askQuestion, body);

    return obs.pipe(
      tap((response:string) => {
        console.log("ask question response")
        console.log(response)
      })
    )
  }

  public deleteQuestion(request: string){
    const body = JSON.parse(request)

    const obs: Observable<string> = this.http.post<string>(this.urls.deleteQuestion, body);

    return obs.pipe(
      tap((response:string) => {
        console.log("ask question response")
        console.log(response)
      })
    )
  }
  
  public respondToQuestion(request: string){
    const body = JSON.parse(request)

    const obs: Observable<string> = this.http.post<string>(this.urls.respondToQuestion, body);

    return obs.pipe(
      tap((response:string) => {
        console.log(response)
      })
    )
  }

  private parseResponse(response: string, submission: Submisson): Submisson | null{
    console.log("parseResponse")
    console.log(response)
    console.log(JSON.parse(JSON.stringify(response)))
    // const obj = JSON.parse(response)
    const obj = JSON.parse(JSON.stringify(response))

    if('error' in obj){
      alert("Došlo je do greške " + obj.error)
      submission.message = obj.error
      return null;
    };

    console.log("old")
    console.log(submission)
    submission.isWaiting = false 
    submission.isCorrect = obj.ok;
    submission.message = obj.message
    submission.totalSubmissions = submission.totalSubmissions+1
    if (submission.isCorrect)
      submission.correctSubmissions += 1


    console.log("new")
    console.log(submission)
    this.auth.updateSubmission(submission);
    return submission
  }

  addTask(body: string, update: boolean){
    const obs: Observable<string> = this.http.post<string>(update? this.urls.updateTask : this.urls.addTask, JSON.parse(body));

    return obs.pipe(
      tap((response:string) => {
        console.log("Add task")
        console.log(response)
      })
    )
  }
}
