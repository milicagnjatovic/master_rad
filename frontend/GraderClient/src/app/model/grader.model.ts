export class Grader {
    public graderId: number
    public graderName: string 

    public static graders: Grader[] = [
        new Grader(1, 'stud2020')
    ]

    constructor(id: number, name: string){
        this.graderId = id
        this.graderName = name
    }
}