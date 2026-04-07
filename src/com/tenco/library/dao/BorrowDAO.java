package com.tenco.library.dao;


import com.tenco.library.dto.Borrow;
import com.tenco.library.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// 대출 반납 관련 SQL을 관련DB
public class BorrowDAO {

    // 도서대출 처리

    // 대출 도서 가능 여부 확인 --> borrow 테이블에 기록 -- 북 테이블에 0으로 변경 --> 만약 중간에
    // try-with-resource 블록 문법 - 블록이 끝나는 순간 무조건 자원을 먼저 닫아버림
    // 이게 트랜잭션 처리할때는 값을 확인해서 commit 또는 rollback을 해야하기 때문에 사용하면 안됨
    // 즉 직접 close() 처리 해야함 - 트랜잭션 처리를 위해서
    // 트랜잭션 처리 ==> 완전히 성공하면 성공 중간에 실패하면 롤백

    /**
     *
     * @param bookId
     * @param studentId : 학번이 아니라 student 테이블의 pk이다 int 형
     * @throws SQLException
     */
    public void borrowBook(int bookId, int studentId) throws SQLException {
        Connection conn = null;

        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false); // 트랜잭션 시작

            // 1. 대출 가능 여부 확인 -- 대출테이블의 아디를 찾을수 있다
            String checkSql = """
                    select available from books where id = ?;
                    """;
            try (PreparedStatement checkPstmt = conn.prepareStatement(checkSql)) {
                checkPstmt.setInt(1, bookId);

                try (ResultSet rs = checkPstmt.executeQuery()) {
                    if (rs.next() == false) {
                        throw new SQLException("존재하지 않는 도서 입니다: " + bookId);
                    }
                    if (rs.getBoolean("available") == false) {
                        throw new SQLException("현재 대출중인 도서 입니다. 반납후 이용가능");
                    }
                }


            }// end of Psmtcheck

            // 대출가능한 상태 --> 대출테이블에 학번, 책번호를 기록 해야함
            //2. 대출 기록 추가
            String borrowSql = """
                    INSERT INTO borrows (book_id, student_id, borrow_date) values (? , ? , ?)
                    """;
            try (PreparedStatement borrowPstmt = conn.prepareStatement(borrowSql)) {
                borrowPstmt.setInt(1, bookId);
                borrowPstmt.setInt(2, studentId);
                // LocalDate --> Date 타입으로 형변환
                borrowPstmt.setDate(3, Date.valueOf(LocalDate.now()));
                borrowPstmt.executeUpdate();
            } // end of borrowPsmt

            // 3. 도서상태 변경 (대출불가)
            String updateSql = """
                    update books set available = false where id = ?;
                    """;
            try (PreparedStatement UpdatePstmt = conn.prepareStatement(updateSql)) {
                UpdatePstmt.setInt(1, bookId);
                UpdatePstmt.executeUpdate();
            }// end of updatePsmt

            // 1, 2, 3, 모두성공 -> 커밋
            conn.commit();


        } catch (SQLException e) {
            if (conn != null) {// 오류가 뜨면 커넥션이 연결되어 있으면
                conn.rollback(); // 하나라도 실패하면 전체 롤백
            }
            System.out.println("오류발생" + e.getMessage());
        } finally {// 실패해도 성공해도 실행
            if (conn != null) {
                // 혹시 중간에 오류가 나서 처리가 안된다면 롤백 처리 함.
                // conn.rollback(); -- 성공하더라도 무조건 롤백하게 된다... 그럼 반영 안됨.
                conn.setAutoCommit(true);// autocommit 복구
                conn.close();
            }
        }


    }

    // 현제 대출중인 도서목록
    public List<Borrow> getBorrowBook() throws SQLException {

        List<Borrow> borrowList = new ArrayList<Borrow>();
        String sql = """
                select * from borrows where return_date is null order by borrow_date
                """;

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement psmt = conn.prepareStatement(sql);
             ResultSet rs = psmt.executeQuery()
        ) {
            while (rs.next()) {
                Borrow borrow = Borrow.builder()
                        .id(rs.getInt("id"))
                        .book_Id(rs.getInt("book_id"))
                        .student_Id(rs.getInt("student_id"))
                        // rs.getDate() --> toLocalDate() --> LocalDate 변환 됨.
                        .borrow_Date(rs.getDate("borrow_date") != null
                                ? rs.getDate("borrow_date").toLocalDate()
                                : null // 값이 널이 아니면 borrow_date 출력 아니면 null 출력 nullpoint 익셉션 방지 코드
                        ) // SQL Date
                        .build();
                borrowList.add(borrow);
            }
        }
        return borrowList;
    }


    // 도서 반납 처리  대출 기록 확인--> return_date 업데이트 --> Book 도서 상태 업데이트

    /**
     *
     * @param bookId
     * @param studentId : student pk
     */
    // 내가원하는 책번호를 입력해서 대출 가능여부 확인 먼저
    public void returnBook(int bookId, int studentId) throws SQLException {
        Connection conn = null;

            // 트랜잭션 시작
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);

            // 1. 대출 기록확인 책 테이블에서 대출이 false인것 걸러내기
            String checkbookSql = """
                    select * from borrows where book_id = ? and student_id = ? and return_date is null
                    """;
            int borrowId;
            try (PreparedStatement psmt = conn.prepareStatement(checkbookSql)
            ) {

                 // 대출가능여부 false 인것만 걸러진다
                psmt.setInt(1,bookId);
                psmt.setInt(2,studentId);
                try (ResultSet rs = psmt.executeQuery()){

                    if(rs.next() == false){
                        throw new SQLException("해당 대출 기록이 없거나 이미 반납 되었습니다.");
                    }
                    // 대출 테이블에 해당하는 pk추출
                    borrowId = rs.getInt("id");
                }
            }// end of check of psmt



            // 2. 반납날짜 업데이트 -- 도서 대출가능여부가 불가능 할때 동작
            String borrowReturnSql = """
                    update borrows set return_date = ? where id = ?;
                    """;
            try (PreparedStatement borrowReturnPsmt = conn.prepareStatement(borrowReturnSql)) {

                borrowReturnPsmt.setDate(1,Date.valueOf(LocalDate.now())); // 현재 반납날짜
                borrowReturnPsmt.setInt(2,borrowId);
                borrowReturnPsmt.executeUpdate();
            }


            // 3. 도서상태 변경

            String changeBookSql = """
                    update books set available = true where = ?
                    """;
            try (PreparedStatement changeBookPsmt = conn.prepareStatement(changeBookSql)) {
                changeBookPsmt.setInt(1, bookId);
                changeBookPsmt.executeUpdate();
            }
            // 모두 성공 --> commit 처리
            conn.commit();

        } catch (SQLException e) { // 연결 오류
            System.out.println("오류 발생: " + e.getMessage());

            if(conn != null){ // 연결은 되었는데 트랜잭션이 중간에 실패할때
                conn.rollback();
            }

        }finally {// 성공하든 실패하든 메서드가 끝나면 닫아줘야함
            if(conn != null){
                conn.setAutoCommit(true);// setAutoCommit true로 바꾸고 닫기
                conn.close();
            }
        }
            // 트랜잭션 종료
    }



    // 테스트 코드작성
    public static void main(String[] args) {

        BorrowDAO borrowDAO = new BorrowDAO();

//        try {
//            //borrowDAO.borrowBook(1,1);
//            List<Borrow> borrowList = borrowDAO.getBorrowBook();
//            System.out.println(borrowList);
//        } catch (SQLException e) {
//            System.out.println("---------------");
//            System.out.println("오류발생 : " + e.getMessage());
//        }
        try {
            borrowDAO.returnBook(1,1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

}
