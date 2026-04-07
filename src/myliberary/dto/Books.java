package myliberary.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
@NoArgsConstructor
public class Books {

    private int id;
    private String title;
    private String author;
    private String publisher;
    private int pulication_Year;
    private String isbn;
    private boolean available;


}
