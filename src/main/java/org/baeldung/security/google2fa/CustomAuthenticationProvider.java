package main.java.org.baeldung.security.google2fa;

import main.java.org.baeldung.persistence.dao.UserRepository;
import main.java.org.baeldung.persistence.model.User;

import javax.annotation.Resource;

import org.jboss.aerogear.security.otp.Totp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

//@Component
public class CustomAuthenticationProvider extends DaoAuthenticationProvider {

    @Autowired
    private UserRepository userRepository;

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        
    	final String verificationCode = ((CustomWebAuthenticationDetails) auth.getDetails()).getVerificationCode();
    	final User user = userRepository.findByEmail(auth.getName());

    	if ((user == null)) {
        
    		throw new BadCredentialsException("Invalid username or password");
        }
    	
        // to verify verification code
        if (user.isUsing2FA()) {
        	
            final Totp totp = new Totp(user.getSecret());
            
            if (!isValidLong(verificationCode) || !totp.verify(verificationCode)) {
            	
                throw new BadCredentialsException("Invalid verfication code");
            }

        }
        
        final Authentication result = super.authenticate(auth);
        
        return new UsernamePasswordAuthenticationToken(user, result.getCredentials(), result.getAuthorities());
    }

    private boolean isValidLong(String code) {
        
    	try {
        
    		Long.parseLong(code);
        } 
    	catch (final NumberFormatException e) {
            
        	return false;
        }
    	
        return true;
    }

    @Override
    public boolean supports(Class<?> authentication) {
    	
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
