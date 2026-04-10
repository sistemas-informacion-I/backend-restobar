package org.restobar.gaira.exception;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class LockoutException extends RuntimeException {
    private final LocalDateTime lockedUntil;

    public LockoutException(String message, LocalDateTime lockedUntil) {
        super(message);
        this.lockedUntil = lockedUntil;
    }
}
