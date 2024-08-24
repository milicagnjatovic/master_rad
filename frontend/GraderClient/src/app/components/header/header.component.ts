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
  public user: User | null = null

  constructor(private router: Router, private authService: AuthenticationService){
    this.user = this.authService.getCurrentUser()
  }

  isLoggedIn(): boolean {
    this.user = this.authService.getCurrentUser()
    return this.user != null
  }

  logout(){
    this.authService.logout()
    this.router.navigate(['/'])
  }

  navigate(route: string) {
    if (route == 'start') {
      this.router.navigate(['/'])
      return
    }

    let routeList = ['home']

    if(route != '')
      routeList.push(route)
    this.router.navigate(routeList)
  }
}
