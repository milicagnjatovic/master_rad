import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http'
import { Observable, tap, map, Subject } from 'rxjs';
import { User } from '../model/user.model';

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
      tap( (response: string) => console.log(response)),
      map((response: string) => this.parseUser(response))
    )
  }

  public signUp(username: string, password: string, firstName: string, lastName:string, email: string): Observable<User | null>{
    const body = {username, password, "firstname": firstName, "lastname": lastName, email}

    const obs: Observable<string> = this.http.post<string>(this.urls.signUp, body);

    return obs.pipe(
      tap( (response: string) => console.log(response)),
      map((response: string) => this.parseUser(response))
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

    return new User(obj.id, obj.username, obj.password, obj.email, obj.firstname, obj.lastname, obj.roleId)

  }
}
