package org.example;


//import org.hibernate.annotations.Table;

import org.json.JSONObject;

import javax.persistence.*;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer Id;
    @Column(name="task")
    public String Task;
    @Column(name="endppoint")
    public String Endpoint;
    @Column(name="created_date")
    public Date Created_date;

    @Column(name = "solution")
    public String Solution;
    public Task(){}

    public Task(JSONObject obj){
        this.Task = obj.getString("z");
        this.Solution=obj.getJSONArray("r").getString(0);
    }
}
