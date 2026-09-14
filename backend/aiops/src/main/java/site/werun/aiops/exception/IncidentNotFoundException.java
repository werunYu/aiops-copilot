package site.werun.aiops.exception;

/**
 * @author werun
 * @version 1.0
 * @date 2026/09/14 19:58
 * @description
 **/
public class IncidentNotFoundException extends RuntimeException {
    public IncidentNotFoundException(Long id) {
        super("The Incident with id " + id + " was not found.");
    }
}
