import { Message } from "./message.model"
import { Task } from "./task.model"

export class Submisson {
    public solution : string = ""
    public question: Message | null = null
    
    constructor(
        public isWaiting: boolean,
        public totalSubmissions: number,
        public correctSubmissions: number,
        public taskId: number,
        public isCorrect: boolean,
        public message: string
    ) {
    }

    prepareJsonRequest(userId: number, ordering: string) : string {
        let obj = {
            userId: userId,
            taskId: this.taskId,
            solution: this.solution,
            ordering
        }
        return JSON.stringify(obj)
    }

    prepareJsonRequestForAskingQuestion(taskId: number, professorId: number, userId: number, question: string){
        let obj = {
            userId,
            professorId,
            taskId,
            question
        }
        return JSON.stringify(obj)
    }

    prepareJsonRequestForDeletingQuestion(taskId: number, userId: number){
        let obj = {
            userId, taskId
        }
        return JSON.stringify(obj)
    }
}