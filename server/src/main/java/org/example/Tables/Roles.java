package org.example.Tables;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "ROLES")
public class Roles {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer Id;

    @Column(name = "NAME")
    public String Name;

    @Column(name = "DESCRIPTION")
    public String Description;


    @Column(name = "CREATED_DATE")
    public Date CreatedDate;

    public Roles() {}

}
