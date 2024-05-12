export class Notification {
    public title: string
    public text: string
    public createdDate: Date

    constructor(obj: any) {
        this.title = obj.title
        this.text = obj.text
        this.createdDate = obj.createdDate
    }
}