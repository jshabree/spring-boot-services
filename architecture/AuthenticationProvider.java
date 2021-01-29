import javax.security.sasl.AuthenticationException;

public interface AuthenticationProvider {
    Authentication authenticate(Authentication authentication)
        throws AuthenticationException;

    boolean supports(Class<?> authentication);
    // query

    // if Provider doesn't recognize Authentication
    // instance type, it is skipped
}
