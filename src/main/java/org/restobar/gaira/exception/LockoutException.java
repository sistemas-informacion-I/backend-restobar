package org.restobar.gaira.exception;

import java.time.Instant;
import lombok.Getter;

@Getter
public class LockoutException extends RuntimeException {
    private final Instant lockedUntil;

    public LockoutException(String message, Instant lockedUntil) {
        super(message);
        this.lockedUntil = lockedUntil;
    }
}
