package myliberary.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Borrow {

    private int id;
    private int book_Id;
    private int student_Id;
    private LocalDate borrow_Date;
    private LocalDate return_Date;

}
