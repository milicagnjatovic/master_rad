package org.example.Tables;

import javax.persistence.*;

@Entity
@Table(name = "ROLE_GRADER_PERMISSION")
public class RoleGraderPermission {
    @EmbeddedId
    public RoleGraderPermissionID PermissionId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ROLE_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    public Roles PermissionRole;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "GRADER_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    public Grader PermissionGrader;
}
