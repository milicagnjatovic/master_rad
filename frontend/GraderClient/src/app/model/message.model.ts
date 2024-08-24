export class Message {
    public professor: string
    public question: string
    public professorId: string
    public response: string

    public createdDate: Date | null  = null

    public taskId: string | null = null
    public userId: string | null = null
    public studentUsername: string | null = null
    public submission: string | null = null

    constructor(obj: any) {
        this.professor = obj.professor
        this.question = obj.question
        this.professorId = obj.professorId
        this.response = obj.response

        if ('createdDate' in obj)
            this.createdDate = new Date(obj.createdDate)

        if ('taskId' in obj)
            this.taskId = obj.taskId
        
        if ('userId' in obj)
            this.userId = obj.userId

        if ('studentUsername' in obj)
            this.studentUsername = obj.studentUsername

        if ('submission' in obj)
            this.submission = obj.submission
    }
}