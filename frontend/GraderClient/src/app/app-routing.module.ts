import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UserPageComponent } from './components/user-page/user-page.component';
import { StartPageComponent } from './components/start-page/start-page.component';
import { TasksPageComponent } from './components/tasks-page/tasks-page.component';
import { InfoPageComponent } from './components/info-page/info-page.component';
import { RankPageComponent } from './components/rank-page/rank-page.component';

const routes: Routes = [
  { 
    path: 'home', 
    component: UserPageComponent,
    children: [
      { path: '', component: InfoPageComponent},
      { path: 'tasks', component: TasksPageComponent},
      { path: 'rank', component: RankPageComponent}
    ]
  },
  { path: '', component: StartPageComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
