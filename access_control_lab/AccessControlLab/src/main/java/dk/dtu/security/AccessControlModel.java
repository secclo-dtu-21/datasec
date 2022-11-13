package dk.dtu.security;

public interface AccessControlModel {

    boolean isMethodGranted(String username, String method);

}
