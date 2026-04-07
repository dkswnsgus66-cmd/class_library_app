package com.tenco.library.dto;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@ToString
@NoArgsConstructor
@Builder
public class Student {

    private int id;
    private String name;
    private String student_Id;


    @Builder
    public Student(String name , String student_Id){
        this.name = name;
        this.student_Id = student_Id;
    }

}
