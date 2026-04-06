CREATE DATABASE IF NOT EXISTS library;
USE library;

-- 도서 테이블
CREATE TABLE IF NOT EXISTS books (
    id               INT           PRIMARY KEY AUTO_INCREMENT,
    title            VARCHAR(255)  NOT NULL,
    author           VARCHAR(255)  NOT NULL,
    publisher        VARCHAR(255),
    publication_year INT,
    isbn             VARCHAR(13),
    available        BOOLEAN       DEFAULT TRUE --  대출가능 여부
);

-- 학생 테이블
CREATE TABLE IF NOT EXISTS students (
    id         INT          PRIMARY KEY AUTO_INCREMENT,
    name       VARCHAR(100) NOT NULL,
    student_id VARCHAR(20)  NOT NULL UNIQUE -- 
);

-- 대출 테이블
CREATE TABLE IF NOT EXISTS borrows (
    id          INT  PRIMARY KEY AUTO_INCREMENT,
    book_id     INT,
    student_id  INT,
    borrow_date DATE NOT NULL, -- 언제빌린ㅈ;
    return_date DATE, -- 언제 반납했는지
    FOREIGN KEY (book_id)    REFERENCES books(id),
    FOREIGN KEY (student_id) REFERENCES students(id)
);