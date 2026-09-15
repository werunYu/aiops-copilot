package site.werun.aiops.exception;

/**
 * @author werun
 * @version 1.0
 * @date 2026/09/15 13:25
 * @description
 **/
public class StatusException extends RuntimeException {
    public StatusException(Long id) {
        super("The Status of incident with id " + id + " was modified failed.");
    }
}
