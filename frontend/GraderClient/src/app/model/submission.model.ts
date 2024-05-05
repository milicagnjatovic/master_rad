export class Submisson {
    public solution : string = ""
    
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
        console.log("prepared json")
        console.log(this)
        console.log(JSON.stringify(obj))
        return JSON.stringify(obj)
    }
}