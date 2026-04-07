package com.tenco.library.dao;

import com.tenco.library.dto.Student;
import com.tenco.library.util.DatabaseUtil;
import myliberary.util.DataBaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    //  학생 등록
    public void addStudent(Student student) throws SQLException {

        String sql = """
                insert into students(name, student_id) values (? , ?)
                """;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setString(1, student.getName());
            pstmt.setString(2, student.getStudent_Id());
            pstmt.executeUpdate();
        }


    }

    // 전체 학생조회

    public List<Student> getAllStudents() throws SQLException {
        List<Student> studentList = new ArrayList<Student>();
        String sql = """
                select * from students order by id
                """;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement psmt = conn.prepareStatement(sql);
             ResultSet rs = psmt.executeQuery()
        ) {

            while (rs.next()) {
                Student student = new Student();

               mapToStudent(rs);
                studentList.add(student);
            }
        }
        return studentList;
    }


    // 학번으로 학생 조회 - 로그인

    public Student authenticateStudent(String student_id) throws SQLException {
        String sql = """
                select * from students where student_id = ?
                """;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement psmt = conn.prepareStatement(sql)) {
            psmt.setString(1, student_id);
            try (ResultSet rs = psmt.executeQuery()) {
                if (rs.next()) {
                    mapToStudent(rs);

                }
            }
        }
        return null;
    }

    // ResultSet -> Student 변환 메소드
    private Student mapToStudent(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setId(rs.getInt("id"));
        student.setName(rs.getString("name"));
        student.setStudent_Id(rs.getString("student_id"));

        return Student
                .builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .student_Id(rs.getString("student_id"))
                .build();

    }

    public static void main(String[] args) throws SQLException {

        new Student("이길동", "12345");

        Student student = Student.builder()
                .student_Id("202612345")
                .name("고길동")
                .build();

        StudentDAO studentDAO = new StudentDAO();

//            studentDAO.addStudent(student);
        System.out.println(studentDAO.getAllStudents().toString());
//       Student resultStudent = studentDAO.authenticateStudent("20230001");
//        System.out.println(resultStudent);

    }


}
