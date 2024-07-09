package org.zerock.ziczone.exception.mypage;

public class PersonalNotFoundException extends RuntimeException{
    public PersonalNotFoundException(String message){
        super(message);
    }
}
