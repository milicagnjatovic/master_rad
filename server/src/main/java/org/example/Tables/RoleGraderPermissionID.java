package org.example.Tables;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class RoleGraderPermissionID implements Serializable {
    @Column(name = "ROLE_ID", nullable = false)
    public Integer RoleId;

    @Column(name = "Grader_ID", nullable = false)
    public Integer GraderId;


}
