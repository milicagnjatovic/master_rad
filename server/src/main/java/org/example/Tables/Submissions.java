package org.example.Tables;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "SUBMISSIONS")
public class Submissions {
    @EmbeddedId
    public SubmissionsID SubmissionId;

    @ManyToOne
    @JoinColumn(name = "TASK_ID", referencedColumnName = "ID")
    public org.example.Tables.Task Task;

    @ManyToOne
    @JoinColumn(name = "USER_ID", referencedColumnName = "ID")
    public Users User;

    @Column(name = "IS_CORRECT")
    public Boolean IsCorrect;

    @Column(name = "MESSAGE")
    public String Message;

    @Column(name = "LAST_UPDATE_TIME")
    public Date LastUpdateTime;

    @Column(name = "WAITING_FOR_RESPONSE")
    public Date WaitingForResponse;
}
