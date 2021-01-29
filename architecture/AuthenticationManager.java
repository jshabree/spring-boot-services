public interface AuthenticationManager {

    // returns true, if input is valid
    // throws an exception if its not
    // returns null if it can't decide 

    Authentication authenticate(Authentication authentication )
    throws AuthenticationException;
}