import { Submisson } from "./submission.model";

export class Task {
    public submission: null | Submisson = null
    constructor(
        public id: number,
        public graderId: number,
        public text: string,
        public name: string,
        public ordering: string
    ){
        this.submission = null
    }

    setSubmissionSolution(solution : string){
        if (this.submission == null)
            this.submission = new Submisson(false, 0, 1, this.id, false, "");
        this.submission.solution = solution;
    }

    setSubmission(submission : Submisson){
        this.submission = submission
    }
}