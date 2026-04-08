package com.tenco.library.view;

import com.tenco.library.dto.Book;
import com.tenco.library.dto.Borrow;
import com.tenco.library.dto.Student;
import com.tenco.library.service.LibraryService;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;


public class LibraryView2 {

    // 원래는 swing 으로 페이지를 꾸며야 한다

    private final LibraryService service = new LibraryService();
    private final Scanner scanner = new Scanner(System.in);


    private Integer currentStudentId = null; // 로그인 중인 학생의 DB ID 저장
    private String currentStudentName = null; // 로그인 중인 학생이름

    // 프로그램 메인 루프
    public void start() {
        System.out.println("도서관리 시스템 시작....");
        while (true) {
            printMenu();
            int choice = readInt("선택 : ");

            try {
                switch (choice) {
                    case 1:
                        addBook();
                        break;
                    case 2:
                        listBooks();
                        break;
                    case 3:
                        searchBooks();
                        break;
                    case 4:
                        addStudent();
                        break;
                    case 5:
                        listStudents();
                        break;
                    case 6:
                        borrowBooks();
                        break;
                    case 7:
                        listBorrowedBooks();
                        break;
                    case 8:
                        returnBook();
                        break;
                    case 9:
                        login();
                        break;
                    case 10:
                        logout();
                        break;
                    case 11:
                        System.out.println("프로그램을 종료합니다");
                        scanner.close();
                        return;
                    default:
                        System.out.println("1 ~ 11 사이의 숫자를 입력하세요");
                }
            } catch (Exception e) {
                System.out.println("오류 : " + e.getMessage());
            }
        }
    }

    // 10
    private void logout() {

        if (currentStudentId == null) {
            System.out.println("로그인 상태가 아닙니다.");
        } else {
            System.out.println(currentStudentName + "님이 로그아웃 되었습니다");
            currentStudentId = null;
            currentStudentName = null;
        }


    }

    //9 <-- 스캐너 에서 9번 눌러졌음
    private void login() throws SQLException {
        if (currentStudentId != null) {
            System.out.println("이미 로그인 중 입니다. (" + currentStudentName + ")");
            return;
        }
        System.out.print("학번 : ");
        String studentId = scanner.nextLine().trim(); // 학번 PK아님

        // 유효성 검사
        if (studentId.isEmpty()) {
            System.out.println("학번을 입력해 주세요");
            return;
        }
        Student student = service.authenticateStudent(studentId);
        if (student == null) {
            System.out.println("존재하지 않는 학번입니다.");
        } else {
            currentStudentId = student.getId();
            currentStudentName = student.getName();
            System.out.println(currentStudentName + "님 환영합니다.");
        }
    }

    //8 도서 반납
    private void returnBook() throws SQLException {

        if(currentStudentId == null){
            System.out.println("먼저 로그인 해주세요");
            return;
        }
        int bookId = readInt("반납할 도서ID : ");

        service.returnBook(currentStudentId,bookId);
        System.out.println("책이 반납 되었습니다 " + bookId + "번");
    }


    //7 대출 중인 도서 목록
    private void listBorrowedBooks() throws SQLException {

      List<Borrow> borrowList = service.borrowsList();

      for (int i = 0; i < borrowList.size(); i++){
          System.out.println("학번: " + borrowList.get(i).getStudent_Id());
          System.out.println("책번호: " + borrowList.get(i).getBook_Id());
          System.out.println("빌린날짜: " + borrowList.get(i).getBorrow_Date());
          System.out.println("===================");
      }
    }

    //6 도서 대출
    private void borrowBooks() throws SQLException {
        // 대출 하면 책 업데이트

        // 책 아이디 입력
        int bookId = readInt("빌릴 책번호 입력:");

        // borrowBook 에는 학생 아이디가 등록된 아이디인지 검사하는 로직이 없다

        if(currentStudentId == null){
            System.out.println("먼저 로그인 해주세요");
        }
        // 서비스에서 학생
        // 포랜키가 학번이 아니네?
        service.borrowBook(bookId,currentStudentId);
        System.out.println("책을 빌렸습니다 빌린 책번호: " + bookId + "번");


    }

    //5. 학생목록
    private List<Student> listStudents() throws SQLException {


        List<Student> studentList = service.getAllStudents();

        if(studentList.isEmpty()){
            System.out.println("등록된 학생이 없습니다.");
            return null;
        }else {
            for (Student s : studentList){
                System.out.println("학번: " + s.getStudent_Id()); // 학번 PK아님
                System.out.println("이름: " + s.getName());

            }
        }

        return studentList;
    }

    //4. 학생등록
    private void addStudent() throws SQLException {

        System.out.print("학번입력: ");
        String studentId = scanner.nextLine();
        if(studentId.isEmpty()){
            System.out.println("학번입력은 필수 입니다.");
            return;
        }

        System.out.print("학생이름 입력: ");
        String studentName = scanner.nextLine().trim();
        if(studentName.isEmpty()){
            System.out.println("학생이름 입력은 필수 입니다.");
            return;
        }
        Student student = Student
                .builder()
                .student_Id(studentId)
                .name(studentName)
                .build();

        service.addStudent(student);
        System.out.println("학생 등록이 완료되었습니다 ");
        System.out.println("학번: " + studentId);
        System.out.println("이름: " +studentName);

    }

    //3 . 검색 제목
    private List<Book> searchBooks() throws SQLException {
        System.out.print("검색제목: ");
        String title = scanner.nextLine();
        if(title.isEmpty()){
            System.out.println("검색어를 입력하세요");
            return null;
        }
       List<Book> bookList = service.searchBooksByTitle(title);
        if(bookList.isEmpty()){
            System.out.println("검색 결과가 없습니다.");
        }else {
            for (Book b : bookList) {
                System.out.printf("ID: %2d | %-30s | %-15s | &s%n",
                        b.getId(),
                        b.getTitle(),
                        b.getAuthor(),
                        b.isAvailable() ? "대출가능" :"대출중"
                );
            }

        }
        return bookList;
    }

    //2. 도서목록
    private List<Book> listBooks()throws SQLException {
       List<Book> bookList = service.getAllBooks();
       if(bookList.isEmpty()){
           System.out.println("등록된 도서가 없습니다.");
       }else {
           System.out.println("--------------");
           for(Book b : bookList){
               System.out.printf("ID: %2d | %-30s | %-15s | &s%n",
                       b.getId(),
                       b.getTitle(),
                       b.getAuthor()
               );

           }
       }
       return bookList;
    }

    //1 . 도서추가
    private void addBook() throws SQLException {

        // 제목
        System.out.print("제목 : ");
        String title = scanner.nextLine().trim();
        if (title.isEmpty()) {
            System.out.println("제목은 필수 입니다.");
            return;
        }
        // 저자
        System.out.print("저자 : ");
        String author = scanner.nextLine().trim();
        if (title.isEmpty()) {
            System.out.println("저자는 필수 입니다.");
            return;
        }

        // 출판사
        System.out.print("출판사 : ");
        String publisher = scanner.nextLine().trim();

        // 출판연도
        int publisherYear = readInt("출판연도 : ");

        // ISBN
        System.out.print("ISBN : ");
        String isbn = scanner.nextLine().trim();

        Book book = Book.builder()
                .title(title)
                .author(author)
                .publisher(publisher.isEmpty() ? null : publisher)
                .pulication_Year(publisherYear)
                .isbn(isbn.isEmpty() ? null : isbn)
                .available(true)
                .build();
        service.addBook(book);
        System.out.println("도서 추가 : " + title);

    }


    private void printMenu() {
        System.out.println("\n===== 도서관리 시스템 =======");

        System.out.println("--------------------------");
        System.out.println("1. 도서 추가");
        System.out.println("2. 도서 목록");
        System.out.println("3. 도서 검색");
        System.out.println("4. 학생 등록");
        System.out.println("5. 학생 목록");
        System.out.println("6. 도서 대출");
        System.out.println("7. 대출 중인 도서");
        System.out.println("8. 도서 반납");
        System.out.println("9. 로그인");
        System.out.println("10. 로그아웃");
        System.out.println("11. 종료");


    }


    // 숫자입력을 안전하게 처리(잘못된 입력시 재요청)
    private int readInt(String prompt) {
        while (true) {
            System.out.println(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("숫자를 입력해 주세요");
            }

        }
    }


}// end of class
