package myliberary.dao;


import com.tenco.library.dto.Book;
import myliberary.dto.BookDTO;
import myliberary.util.DataBaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// 데이터 베이스와 연결한 sql을 조회 수정 삭제등등 구현
public class BookDAO {


    // 모든 데이터 조회
    public List<BookDTO> searchAll(BookDTO book) throws SQLException {

        String sql = """
                select* from books;
                """;

         // 데이터 베이스 연결
        // 연결했으니 데이터 조회 기능 만들기
        try (   Connection connection = new DataBaseUtil().databaseUtil();
                PreparedStatement psmt = connection.prepareStatement(sql)) {

            // ResultSet에 sql 조회 결과 넣기
            ResultSet rs = psmt.executeQuery();

            // 넣은 결과 전부 출력하기
            // 먼저 결과를 넣을때 여러개의 Book이 들어가기에 List를 만든다
            List<BookDTO> bookList = new ArrayList<BookDTO>();

            while (rs.next()){

                rs.getInt("id");
                rs.getString("title");
                rs.getString("author");
                rs.getString("publisher");
                rs.getInt("publication_year");
                rs.getString("isbn");
                rs.getBoolean("available");

                // 값 을 BookDTO 할당후 리스트 추가
               bookList.add(book);
            }
            return bookList;

        }


    }

    public static void main(String[] args) {
        BookDTO bookDTO = new BookDTO();
        try {
            new BookDAO().searchAll(bookDTO);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

}
