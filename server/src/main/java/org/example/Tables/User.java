package org.example.Tables;

import javax.persistence.*;

@Entity
@Table(name = "USERS")
public class User {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer Id;

    @Column(name = "USERNAME", unique = true, nullable = false)
    public String Username;

    @Column(name = "EMAIL", unique = true, nullable = false)
    public String Email;

    @Column(name = "FIRST_NAME")
    public String FirstName;

    @Column(name = "LAST_NAME")
    public String LastName;

    @Column(name = "CREATED_DATE")
    public String CreatedDate;

    @Column(name = "CORRECT_SUBMISSIONS")
    public Integer CorrectSubmissions;

    @Column(name = "INCORRECT_SUBMISSIONS")
    public Integer IncorrectSubmissions;

    @ManyToOne()
    @JoinColumn(name = "ROLE_ID", referencedColumnName = "ID")
    public Roles Role;

    public User(Integer id){
        this.Id = id;
    }

    public User(){}

}
