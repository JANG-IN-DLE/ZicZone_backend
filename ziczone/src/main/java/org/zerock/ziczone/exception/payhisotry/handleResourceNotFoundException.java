package org.zerock.ziczone.exception.payhisotry;

import com.amazonaws.services.kms.model.NotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class handleResourceNotFoundException extends NotFoundException {
    public handleResourceNotFoundException(String message){
        super(message);
    }
}
