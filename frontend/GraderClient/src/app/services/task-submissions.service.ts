import { Injectable } from '@angular/core';
import { Submisson } from '../model/submission.model';
import { BehaviorSubject, Observable, map, tap } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { AuthenticationService } from './authentication.service';

@Injectable({
  providedIn: 'root'
})
export class TaskSubmissionsService {
  private readonly url = "http://localhost:53000/server/checkTask"


  constructor(private http : HttpClient, private auth: AuthenticationService) { }

  public checkTask(request: string, submissoin: Submisson){
    const body = JSON.parse(request);
    console.log(body)
    const obs: Observable<string> = this.http.post<string>(this.url, body);
    
    this.auth.setSubmissionWaiting(submissoin);

    return obs.pipe(
      map((response: string) => {
        console.log("Pipe")
        console.log(response)
        this.parseResponse(response, submissoin)
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
}
