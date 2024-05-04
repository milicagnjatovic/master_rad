export class Submisson {
    constructor(
        public isWaiting: boolean,
        public totalSubmissions: number,
        public correctSubmissions: number,
        public taskId: number,
        public isCorrect: boolean
    ) {}
}