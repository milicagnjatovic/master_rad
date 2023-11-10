package org.example;


//import org.hibernate.annotations.Table;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;


@Entity
@Table(name="ZADACI")
public class Task {
//    --     ID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY,
//--     TASK VARCHAR(10000),
//--     ENDPPOINT VARCHAR(100),
//--     CATEGORY VARCHAR(50),
//--     CREATED

    @Id
    @Column(name = "ID")
    public Integer Id;
    @Column(name="task")
    public String Task;
    @Column(name="endpoint")
    public String Endpoint;
    @Column(name="created_date")
    public Date Created_date;

    @Column(name = "solution")
    public String Solution;
    public Task(){}
}
