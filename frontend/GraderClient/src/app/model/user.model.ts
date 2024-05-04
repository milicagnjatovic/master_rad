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
        public submission: Submisson[]
    ) {}
}