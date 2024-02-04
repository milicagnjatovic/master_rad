package org.example.Tables;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "TASKS")
public class Tasks {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer Id;

    @ManyToOne
    @JoinColumn(name = "GRADER_ID", referencedColumnName = "ID", nullable = false)
    public Graders Grader;

    @Column(name = "TEXT", nullable = false)
    public String Text;

    @Column(name = "SOLUTION", nullable = false)
    public String Solution;

    @Column(name = "CORRECT_SUBMISSIONS")
    public Integer CorrectSubmission;

    @Column(name = "INCORRECT_SUBMISSION")
    public Integer IncorrectSubmission;

    @Column(name = "LAST_GENERATED_DATE")
    public Date LastGeneratedDate;


}
