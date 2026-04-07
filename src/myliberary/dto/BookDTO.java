package myliberary.dto;


import lombok.Data;

@Data
public class BookDTO {

    private int id;
    private String title;
    private String author;
    private String publisher;
    private int publicationYear;
    private String isbn;
    private String available;

}
