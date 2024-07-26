package org.zerock.ziczone.exception.resume;

import org.springframework.dao.DataIntegrityViolationException;

public class ResumeDataIntegrityViolationException extends DataIntegrityViolationException {
    public ResumeDataIntegrityViolationException(String message) {
        super(message);
    }
}
