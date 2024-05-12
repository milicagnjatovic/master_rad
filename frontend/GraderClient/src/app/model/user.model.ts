import { Message } from "./message.model";
import { Notification } from "./notification.model";
import { Professor } from "./professor.model";
import { Submisson } from "./submission.model";
import { Task } from "./task.model";

export class User {
    public professors: Professor[]
    public notifications: Notification[]
    constructor(
        public id: number,
        public username: string,
        public password: string,
        public email: string,
        public name: string,
        public lastName: string,
        public roleId: string,
        public tasks: Task[],
        submissions: Submisson[]
    ) {
      this.professors =[]
      this.notifications = []
      let taskIdToSubmission : Map<number, Submisson> = new Map<number, Submisson>(); 
      
      if(submissions.length>0){
        for(let submission of submissions){
          taskIdToSubmission.set(submission.taskId, submission);
        }
        
        for(let task of tasks){
          let submission : Submisson | undefined = taskIdToSubmission.get(task.id)
          if(submission != undefined) {
            task.setSubmission(submission);
          }
        }
      }
    }

    static storeUser(user: User | null): void {
        localStorage.setItem(User.STORE_USER_KEY, JSON.stringify(user));
    }

    static retreiveUser(): User | null {
        const storedUser = localStorage.getItem(this.STORE_USER_KEY);
        if (storedUser == null || storedUser == 'null')
            return null
        let data = JSON.parse(storedUser);


        let tasksList: Task[] = [];
        if(data.tasks != null) {
          for(let t of data.tasks){
            let task = new Task(t.id, t.graderId, t.text, t.name, t.ordering)
            tasksList.push(task)
            if(t.submission != null){
              let s = t.submission
              let submission = new Submisson(s.isWaiting, s.totalSubmissions, s.correctSubmissions, s.id, s.isCorrect, s.message)
              if ('question' in s && s.question != null) {
                submission.question = new Message(s.question)
              }
              task.setSubmission(submission)
            }
          } 
        }

        let user = new User(data.id, data.username, data.password,  data.email, data.name, data.lastName, data.roleId, tasksList, []);

        let professors = []
        for(let p of data.professors){
          professors.push(new Professor(p.username, p.id))
        }
        user.professors = professors

        let notificationList: Notification[] = []
        for(let n of data.tasks.notifications){
          let notification = new Notification(n)
          notificationList.push(notification)
        }
        user.notifications = notificationList

        return user
    }

    static logout(): void {
      localStorage.removeItem(User.STORE_USER_KEY);
    }

    private static STORE_USER_KEY : string = "dbGradeUser"
}

