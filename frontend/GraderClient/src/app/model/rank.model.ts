export class Rank {
    public subject: string
    public score: number

    constructor(obj: any) {
        if ('user' in obj)
            this.subject = obj.user
        else if('task' in obj)
            this.subject = obj.task
        else 
            this.subject = obj.subject
        

        if ('successPercantage' in obj)
            this.score = obj.successPercantage
        else 
            this.score = obj.score
    }
}