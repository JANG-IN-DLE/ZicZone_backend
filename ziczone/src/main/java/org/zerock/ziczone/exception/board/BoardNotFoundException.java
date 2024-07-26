package org.zerock.ziczone.exception.board;

public class BoardNotFoundException extends RuntimeException{
    public BoardNotFoundException(String message) {
        super(message);
    }
}
