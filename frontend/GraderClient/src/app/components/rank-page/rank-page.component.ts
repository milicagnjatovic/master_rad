import { Component, OnInit } from '@angular/core';
import { Rank } from 'src/app/model/rank.model';
import { AuthenticationService } from 'src/app/services/authentication.service';

@Component({
  selector: 'app-rank-page',
  templateUrl: './rank-page.component.html',
  styleUrls: ['./rank-page.component.sass']
})
export class RankPageComponent implements OnInit{
  public userRank: Rank[] = []
  public taskRank: Rank[] = []

  constructor (private auth: AuthenticationService){
  }

  ngOnInit(): void {
    this.auth.user.subscribe(
      user => {
        console.log('RANK')
        console.log(user?.userRank)
        console.log(user)
        if(user?.userRank != null)
          this.userRank = user?.userRank

        if(user?.taskRank != null)
          this.taskRank = user?.taskRank
     },
     error => {
       console.log(error)
     }
    )   
   }

}
