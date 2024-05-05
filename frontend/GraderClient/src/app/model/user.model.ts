import { Submisson } from "./submission.model";
import { Task } from "./task.model";

export class User {
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
      let taskIdToSubmission : Map<number, Submisson> = new Map<number, Submisson>(); 
      
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

    static storeUser(user: User | null): void {
        localStorage.setItem(User.STORE_USER_KEY, JSON.stringify(user));
    }

    static retreiveUser(): User | null {
        const storedUser = localStorage.getItem(this.STORE_USER_KEY);
        if (storedUser == null)
            return null
        let data = JSON.parse(storedUser);

        let tasksList: Task[] = [];
        for(let t of data.tasks){
          let task = new Task(t.id, t.graderId, t.text, t.name, t.ordering)
          tasksList.push(task)
          if(t.submission != null){
            let s = t.submission
            let submission = new Submisson(s.isWaiting, s.totalSubmissions, s.correctSubmissions, s.id, s.isCorrect, s.message)
            task.setSubmission(submission)
          }
        } 

        return new User(data.id, data.username, data.password,  data.email, data.name, data.lastName, data.roleId, tasksList, []);
    }

    logoutUser(): void {
        localStorage.removeItem(User.STORE_USER_KEY);
      }
    private static STORE_USER_KEY : string = "dbGradeUser"
}

