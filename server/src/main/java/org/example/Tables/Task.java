package org.example.Tables;

import org.json.JSONObject;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "TASKS")
public class Task {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer Id;

    @ManyToOne
    @JoinColumn(name = "GRADER_ID", referencedColumnName = "ID", nullable = false)
    public org.example.Tables.Grader Grader;

    @Column(name = "TEXT", nullable = false)
    public String Text;

    @Column(name = "SOLUTION", nullable = false)
    public String Solution;

    @Column(name = "CORRECT_SUBMISSIONS")
    public Integer CorrectSubmission = 0;

    @Column(name = "INCORRECT_SUBMISSION")
    public Integer IncorrectSubmission = 0;

    @Column(name = "LAST_GENERATED_DATE")
    public Date LastGeneratedDate;

    @Column(name = "ORDERING")
    public String Ordering;
    public Task(){}
    public Task(JSONObject obj){
        this.Text = obj.getString("task");
        this.Solution = obj.getString("solution");
        this.Ordering = obj.optString("ordering", "");
    }
}
