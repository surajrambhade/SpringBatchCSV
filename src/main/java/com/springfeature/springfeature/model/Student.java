package com.springfeature.springfeature.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "Students_data")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Student {

    //id,firstName,lastName,email,gender,contactNo,Country,dob

    @Id
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private String gender;
    private String contactNo;
    private String country;
    private String dob;


}
