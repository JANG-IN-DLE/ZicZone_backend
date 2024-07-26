package org.zerock.ziczone.exception.resume;

public class ResumeNotFoundException extends RuntimeException{
    public ResumeNotFoundException(String message){
        super(message);
    }
}
