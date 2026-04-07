package myliberary.dao;

import myliberary.dto.Books;
import myliberary.util.DataBaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BorrowDAO {
    // 대출 도서 가능 여부 확인 --> borrow 테이블에 기록 -- 북 테이블에 0으로 변경 --> 만약 중간에
    // try-with-resource 블록 문법 - 블록이 끝나는 순간 무조건 자원을 먼저 닫아버림
    // 이게 트랜잭션 처리할때는 값을 확인해서 commit 또는 rollback을 해야하기 때문에 사용하면 안됨
    // 즉 직접 close() 처리 해야함 - 트랜잭션 처리를 위해서
    // 트랜잭션 처리 ==> 완전히 성공하면 성공 중간에 실패하면 롤백


    // 1. 대출 가능 여부 확인 -- 대출테이블의 아디를 찾을수 있다
    // 도서대출 기능

    public List<Books> testBorrow(){


        String checkSql = """
                   select available from books where available = true;
                    """;
        try (
                Connection conn = DataBaseUtil.getDataBaseConnection();
                PreparedStatement checkPsmt = conn.prepareStatement(checkSql)) {
            List<Books> borrowList = new ArrayList<Books>();

            try (ResultSet rs = checkPsmt.executeQuery()) {
                while (rs.next()) {
                    Books books = new Books();
                    books.setId(rs.getInt("id"));
                    books.setTitle(rs.getString("title"));
                    books.setAuthor(rs.getString("author"));
                    books.setPublisher(rs.getString("publisher"));
                    books.setIsbn(rs.getString("isbn"));
                    books.setAvailable(rs.getBoolean("available"));
                    borrowList.add(books);

                }
                return borrowList;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }




    }

    public void borrowBook() throws SQLException {
        Connection conn = null;

        // 여러개 동작시켜야 하기 때문에 트랜잭션 활용
        try {
            // 트랜잭션 연결관리
            conn = DataBaseUtil.getDataBaseConnection();
            conn.setAutoCommit(false);

            // 도서 대출 가능한지 확인



            // 학생아이디와 매칭

            // 책 빌릴수 없는 상태로 업데이트


        } catch (SQLException e) {

            if (conn != null) { // conn 이 null 이면 연결이 안된것이고 null 이 아닌데 오류가 떳다는건 트랜잭션중간에 오류가 떳단느 거임
                conn.rollback();// 트랜잭션이 중간에 오류가 떳으니 rollback 한다
            }
        } finally {
            conn.setAutoCommit(true); // 커밋을 자동적으로 하는 기능
            conn.close(); // 트랜잭션이 성공적으로 끝났으니 연결을 끊는다
        }
    }

    public static void main(String[] args) {

        BorrowDAO borrowDAO = new BorrowDAO();

            borrowDAO.testBorrow().toString();




    }


}
