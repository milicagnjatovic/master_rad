import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { User } from 'src/app/model/user.model';
import { AuthenticationService } from 'src/app/services/authentication.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.sass']
})
export class HeaderComponent {
  
  constructor(private router: Router, private authService: AuthenticationService){
  }

  isLoggedIn(): boolean {
    let user = this.authService.getCurrentUser()
    return user != null
  }

  logout(){
    this.authService.logout()
    this.router.navigate([''])
  }

  navigate(route: string) {
    let routeList = ['home']
    if(route != '')
      routeList.push(route)
    this.router.navigate(routeList)
  }
}
