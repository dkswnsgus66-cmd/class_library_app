package myliberary.dao;


import com.tenco.library.dto.Book;
import com.tenco.library.util.DatabaseUtil;
import myliberary.dto.Books;
import myliberary.dto.Borrow;
import myliberary.util.DataBaseUtil;

import javax.imageio.stream.ImageInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// 대출/반납 관련 SQL 을 실행하는 DAO
public class BorrowDAO {






    // 테스트 메서드
    public List<Books> testBorrow() throws SQLException {

        String testSql = """
                select * from books where available = true;
                """;

        try (Connection conn = DataBaseUtil.databaseUtil()) {

            PreparedStatement testPsmt = conn.prepareStatement(testSql);

            try (ResultSet rs = testPsmt.executeQuery()) {

                List<Books> booksList = new ArrayList<Books>();
                while (rs.next()) { // 데이터 있는지 확인
                    Books books = new Books();
                   books.setId(rs.getInt("id"));
                   books.setTitle(rs.getString("title"));
                   books.setAuthor(rs.getString("author"));
                   books.setPublisher(rs.getString("publisher"));
                   books.setPulication_Year(rs.getInt("publication_year"));
                   books.setIsbn(rs.getString("isbn"));
                   books.setAvailable(rs.getBoolean("available"));

                   booksList.add(books);
                }
                return booksList;
            }
        }
    }

// 도서 대출 처리
    // 대출 가능 여부 확인 --> borrow 테이블에 기록 --- 북 테이블 으로 변경
    // try-with-resource 블록 문법 - 블록인 끝나는 순간 무조건 자원을 먼저 닫아 버림
    // 이게 트랜잭션 처리할 때는 값을 확인해서 commit 또는 rollback 해야 하기 때문에 사용하면 안됨
    // 즉, 직접 close() 처리 해야 함 - 트랜잭션 처리를 위해서.

    /**
     *
     * @param bookId books의 PK
     * @throws SQLException
     */
    public void checkBorrow(int bookId, int studentId) throws SQLException {
        // 트랜잭션 구현
        Connection conn = null;

        try {
            conn = DataBaseUtil.databaseUtil(); // 데이터 베이스 연결
            // sql 접근 쿼리 사용
            // 대출 가능 여부 확인 필요
            String checkSql = """
                    select availiable from books where id = ?
                    """;
            // availiable 을 출력 하는데 이게 해당 북 리스트에 빌릴수 있는지 확인
            try (PreparedStatement checkPtmt = conn.prepareStatement(checkSql)) {
                checkPtmt.setInt(1,bookId); // 번호를 넣어서 현재 책번호가 대출이 가능하냐 확인
                try (ResultSet rs = checkPtmt.executeQuery()) {

                    if(rs.next() == false){ // 책 아이디를 넣었을때 availiable 이 나오고 근데 데이터가 없으면 책존재 자체가 없기때문에
                        System.out.println("해당 도서는 존재하지 않습니다.");
                    }
                    if(rs.getBoolean("availiable") == false){ // availiable 컬럼이 false면 이미 대출 한거임
                        System.out.println("해당 도서는 이미 대출 했습니다.");
                    }
                }
            }// checkBook

            // 대출 안되는건 전부 파악 했으니 이제 대출 해봄
            // 대출하면은 뭐가 바뀌냐? 대출 가능상태가 바뀌고 Borrows 테이블에 해당 책 아이디 학생아이디 대출 아아디 빌린날 추가

            String updateBorrow = """
                    insert into borrows (book_id, student_id, borrow_date) values
                    (? , ? , now())
                    """;
            try (PreparedStatement insertBorrow = conn.prepareStatement(updateBorrow)) {

                insertBorrow.setInt(1,bookId);
                insertBorrow.setInt(2,studentId);
                insertBorrow.executeUpdate();
            } // 업데이트 끝

            // 책테이블에 빌릴수 없는 상태로 업데이트
            String availiableSql = """
                    update books set availiable = false where = ?;
                    """;
            PreparedStatement bookavAilbleFalse = conn.prepareStatement(availiableSql);
            bookavAilbleFalse.setInt(1,bookId);
            bookavAilbleFalse.executeUpdate();
            conn.commit();

        }catch (SQLException e){
            if(conn != null){ // 연결은 되었음 근데 오류날경우 이러면 트랜잭션 오류
                conn.rollback(); // 트랜잭션이 중간에 오류났기에 롤백
            }


        }finally {// 무조건 실행되는 구문 즉 트랜잭션이 성공이든 실패든 실행
            conn.setAutoCommit(true); // 트랜잭션이 잘 마무리 되었고 이걸 다시 true로 돌려놔야 다른 코드에서 commit 자동으로 가능
            conn.close(); // 볼장 다 봤고 연결 끊기
        }


        // 1. 대출 가능 여부 확인

    }

    public static void main(String[] args) {
        BorrowDAO borrowDAO = new BorrowDAO();
        try {


            System.out.println(borrowDAO.testBorrow().toString());


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }



    }


}
