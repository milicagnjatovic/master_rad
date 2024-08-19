import { Message } from "./message.model";
import { Notification } from "./notification.model";
import { Professor } from "./professor.model";
import { Rank } from "./rank.model";
import { Submisson } from "./submission.model";
import { Task } from "./task.model";

export class User {
    public static PROFESSOR_ROLES: [string] = ['2']
    public professors: Professor[]
    public notifications: Notification[]
    private timestamp

    public messagesForProfessor: Message[] = []
    public taskSolutions = new Map<Number, string>() 

    constructor(
        public id: number,
        public username: string,
        public password: string,
        public email: string,
        public name: string,
        public lastName: string,
        public roleId: string,
        public tasks: Task[],
        public userRank: Rank[],
        public taskRank: Rank[],
        submissions: Submisson[]
    ) {
      this.timestamp = new Date()
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
      if (user!=null)
        user.timestamp = new Date()
      console.log("store")
      console.log(user)
      localStorage.setItem(User.STORE_USER_KEY, JSON.stringify(user));
    }

    public isProfessor(){
      return User.PROFESSOR_ROLES.indexOf(this.roleId+'') != -1
    }

    static retreiveUser(): User | null {
        const storedUser = localStorage.getItem(this.STORE_USER_KEY);
        if (storedUser == null || storedUser == 'null')
            return null

        let data = JSON.parse(storedUser);

        let dateChanged = new Date(data['timestamp'])
        console.log("dateChanged")
        console.log(dateChanged)
        let twoHoursBackDate = new Date()
        twoHoursBackDate.setHours(twoHoursBackDate.getHours()-2)
        // twoHoursBackDate.setMinutes(twoHoursBackDate.getMinutes()-1)
        console.log(twoHoursBackDate)
        console.log(twoHoursBackDate > dateChanged)
        if (twoHoursBackDate > dateChanged){
          this.logout()
          return null
        }


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

        let user = new User(data.id, data.username, data.password,  data.email, data.name, data.lastName, data.roleId, tasksList, [], [], []);

        let professors = []
        if(data.professors != null) {
          for(let p of data.professors){
            professors.push(new Professor(p.username, p.id))
          }
          user.professors = professors
        }

        let notificationList: Notification[] = []
        if(data.notifications != null) {
          for(let n of data.notifications){
            let notification = new Notification(n)
            notificationList.push(notification)
          }
          user.notifications = notificationList
        }

        // task rank
        let taskRank: Rank[] = []
        if (data != null && data.taskRank != null){
          for(let r of data.taskRank){
            let rank = new Rank(r)
            taskRank.push(rank)
          }
        }
        user.taskRank = taskRank;

        // task rank
        let userRank: Rank[] = []
        if (data != null && data.userRank != null){
          for(let r of data.userRank){
            let rank = new Rank(r)
            userRank.push(rank)
          }
        }
        user.userRank = userRank;


      if(data != null && 'messagesForProfessor' in data) {
        let questionsForProfessor = []
        for(let m of data.messagesForProfessor){
          let message = new Message(m);
          questionsForProfessor.push(message)        
        }
        user.messagesForProfessor = questionsForProfessor
      }

      if(data != null && 'taskSolutions' in data) {
        let taskSolutions = new Map<Number, string>()
        for(let key in data.taskSolutions){
            taskSolutions.set(Number.parseInt(key), data.taskSolutions[key])
        }
        user.taskSolutions = taskSolutions
      }

        return user
    }

    static logout(): void {
      localStorage.removeItem(User.STORE_USER_KEY);
    }

    private static STORE_USER_KEY : string = "dbGradeUser"
}

