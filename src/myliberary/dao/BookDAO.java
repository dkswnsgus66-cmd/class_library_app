package myliberary.dao;


import com.tenco.library.dto.Book;
import com.tenco.library.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// book 테이블에 접근해서 조회 검색 추가등의 기능만 구현
public class BookDAO {


    // 도서 추가
    public void addBook(Book book) throws SQLException {

        String sql = """
                insert into books (title , author, publisher, publication_year,isbn)values
                (?, ? , ? , ? , ?)
                """;

        try (Connection conn = DatabaseUtil.getConnection()) {

            PreparedStatement psmt = conn.prepareStatement(sql); // 연결한 sql 데이터 베이스에서 어떤 쿼리를 쓸지 정한다

            psmt.setString(1, book.getTitle()); // PrepareStatement 의 기능이다 setString 1번째 ? 기준으로 행당 값을 넣는다는기능
            psmt.setString(2, book.getAuthor());
            psmt.setString(3, book.getPublisher());
            psmt.setInt(4, book.getPulication_Year());
            psmt.setString(5, book.getIsbn());
            psmt.executeUpdate();

        }


    }

    // 도서 전체조회
    public List<Book> getAllBooks() throws SQLException {
        List<Book> bookList = new ArrayList<Book>();

        String sql = """
                select * from books order by id
                """;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement psmt = conn.prepareStatement(sql)
        ) {
            ResultSet rs = psmt.executeQuery();

            while (rs.next()) {
                bookList.add(mapToBook(rs));
            }
        }
        return bookList;
    }


    // 제목으로 도서 검색
    public List<Book> searchBooksByTitle(String title) throws SQLException {
        List<Book> bookList = new ArrayList<Book>();

        String sql = """
                select * from  books where title like ?
                """;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement psmt = conn.prepareStatement(sql)
        ) {
            psmt.setString(1, "%" + title + "%");

            try (ResultSet rs = psmt.executeQuery()) {
                while (rs.next()) {
                    bookList.add(mapToBook(rs));
                }
            }
        }
        return bookList;

    }

    private Book mapToBook(ResultSet rs) throws SQLException {

        return Book.builder()
                .id(rs.getInt("id"))
                .title(rs.getString("title"))
                .author(rs.getString("author"))
                .publisher(rs.getString("publisher"))
                .pulication_Year(rs.getInt("publication_year"))
                .isbn(rs.getString("isbn"))
                .available(rs.getBoolean("available"))
                .build();
    }

    public static void main(String[] args) {
        try {
            List<Book> resultList = new BookDAO().searchBooksByTitle("입문");
            System.out.println(resultList);
            System.out.println("--------------");
            List<Book> resultList2 = new BookDAO().getAllBooks();
            System.out.println(resultList2);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
