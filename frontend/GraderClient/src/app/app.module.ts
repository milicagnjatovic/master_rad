import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { StartPageComponent } from './components/start-page/start-page.component';
import { UserPageComponent } from './components/user-page/user-page.component';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { TasksPageComponent } from './components/tasks-page/tasks-page.component';
import { TaskComponent } from './components/task/task.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {MatProgressBarModule} from '@angular/material/progress-bar';
import { HeaderComponent } from './components/header/header.component';
import { InfoPageComponent } from './components/info-page/info-page.component';
import { RankPageComponent } from './components/rank-page/rank-page.component';
import { DatePipe } from '@angular/common';
@NgModule({
  declarations: [
    AppComponent,
    StartPageComponent,
    UserPageComponent,
    TasksPageComponent,
    TaskComponent,
    HeaderComponent,
    InfoPageComponent,
    RankPageComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule, 
    HttpClientModule, BrowserAnimationsModule,
    MatProgressBarModule
  ],
  providers: [
    DatePipe
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
