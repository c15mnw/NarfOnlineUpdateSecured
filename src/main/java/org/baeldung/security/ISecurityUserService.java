package main.java.org.baeldung.security;

public interface ISecurityUserService {

    String validatePasswordResetToken(long id, String token);

}
