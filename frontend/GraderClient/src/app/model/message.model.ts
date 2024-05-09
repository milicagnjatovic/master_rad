export class Message {
    public professor: string
    public question: string
    public professorId: string
    public response: string

    constructor(obj: any) {
        this.professor = obj.professor
        this.question = obj.question
        this.professorId = obj.professorId
        this.response = obj.response
    }
}