package org.example.Tables;

import javax.persistence.*;

@Entity
@Table(name = "GRADERS")
public class Graders {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer Id;

    @Column(name = "NAME", unique = true)
    public String Name;

    @Column(name = "ENDPOINT", unique = true)
    public String Endpoint;

    @Column(name = "ACTIVE")
    public boolean Active;

    @Column(name = "CREATED_DATE")
    public String CreatedDate;
}
