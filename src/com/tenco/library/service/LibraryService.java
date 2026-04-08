package com.tenco.library.service;

// 비즈니스 로직을 처리 하는 서비스 클래스
// VIEW 계층(화면) -> Service 계층 --> Data 계층
// 뷰 계층에서는 DAO를 직접 호출하지 말고 항상 Service를 통해서 접근한다.

import com.tenco.library.dao.AdminDAO;
import com.tenco.library.dao.BookDAO;
import com.tenco.library.dao.BorrowDAO;
import com.tenco.library.dao.StudentDAO;
import com.tenco.library.dto.Admin;
import com.tenco.library.dto.Book;
import com.tenco.library.dto.Borrow;
import com.tenco.library.dto.Student;

import java.sql.SQLException;
import java.util.List;

public class LibraryService {
    private final AdminDAO adminDAO = new AdminDAO();
    private final BookDAO bookDAO = new BookDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final BorrowDAO borrowDao = new BorrowDAO();

    // 만약 화면 단에서 도서 추가 기능요청이 (화면단 코드) 발생한다면
    // 서비스 단에서는 사용자가 입력한 데이터가 유효한지 유효성 검사도 하고
    // 입력한 데이터가 정상적이라면 DB에 반영할 예정


    // 도서 추가기능(제목, 저자 필수검증)
    public void addBook(Book book) throws SQLException {
        // 1. 유효성 검사
        if(book.getTitle() == null || book.getTitle().trim().isEmpty()){
            throw new SQLException("도서 제목은 필수 입력항목 입니다.");
        }
        if(book.getAuthor() == null || book.getAuthor().trim().isEmpty()){
            throw new SQLException("도서 저자는 필수 입력항목 입니다.");
        }

        bookDAO.addBook(book);
    }

    // 전체 도서목록 조회(대출 여부상관없이 다 출력)
    public List<Book> getAllBooks() throws SQLException {
        return bookDAO.getAllBooks();
    }

    // 책 제목으로 검색
    public List<Book> searchBooksByTitle(String title) throws SQLException{
        if(title == null || title.trim().isEmpty()){
            throw new SQLException("검색어를 입력해 주세요");
        }
        return bookDAO.searchBooksByTitle(title);
    }

    // 학생 등록 기능 (이름, 학번 필수 검증)
    public void addStudent(Student student) throws SQLException{

        if (student.getName() == null || student.getName().trim().isEmpty())
            throw new SQLException("학생의 이름은 필수 항목입니다.");
        if (student.getStudent_Id() ==  null || student.getStudent_Id().trim().isEmpty()){
            throw new SQLException("학생의 학번은 필수 항목입니다.");
        }
        studentDAO.addStudent(student);
    }

    // 전체 학생 목록 조회
    public List<Student> getAllStudents() throws SQLException {
        return studentDAO.getAllStudents();
    }

    // 학번이 유효한지 조회(로그인 처리)

    /**
     *
     * @param studentId - String(PK 아님)
     * @return
     */
    public Student authenticateStudent(String studentId) throws SQLException{
        if(studentId == null || studentId.trim().isEmpty()){
            throw new SQLException("학번을 입력해 주세요");
        }

        Student check = studentDAO.authenticateStudent(studentId);

        if(check == null){ // 학생 조회했을때 데이터가 없을때
            System.out.println("없는 학번입니다.");
        }
        return check;
    }

    // 도서 대출 요청

    /**
     *
     * @param bookId
     * @param studentId : 학번이 아니라 PK값
     * @throws SQLException
     */
    public void borrowBook(int bookId,int studentId)throws SQLException{
        if(bookId <= 0 || studentId <= 0){
            throw new SQLException("유효한 도서 아이디와 학생 아이디를 입력해 주세요.");
        }
        borrowDao.borrowBook(bookId,studentId);
    }

    // 도서 반납처리

    /**
     *
     * @param bookId
     * @param studentId PK(학번 아님)
     * @throws SQLException
     */
    public void returnBook(int bookId, int studentId) throws SQLException{
        if(bookId <=0 || studentId <= 0){
            throw new SQLException("유효한 도서 아이디와 학생 아이디를 입력해 주세요.");
        }
        borrowDao.returnBook(bookId,studentId);

    }

    // 도서대출 목록 조회
    public List<Borrow> borrowsList() throws SQLException {

        BorrowDAO borrowDAO = new BorrowDAO();
        System.out.println("===== 현재 대출 목록 =====");
       return borrowDAO.getBorrowedBooks();

    }

    // TODO 관리자 기능 추가 예정
    // 관리자 인증 서비스 기능 추가
    public Admin authenticateAdmin(String adminId, String password) throws SQLException{
        if(adminId == null || adminId.trim().isEmpty()){
            throw new SQLException("관리자 아이디를 입력 하세요"); // 어차피 오류 던지니까 리턴 아써도 된다
        }
        if(password == null || password.trim().isEmpty()){
            throw new SQLException("관리자 아이디를 입력 하세요");
        }

        return adminDAO.authenAdmin(adminId,password);

    }

}

