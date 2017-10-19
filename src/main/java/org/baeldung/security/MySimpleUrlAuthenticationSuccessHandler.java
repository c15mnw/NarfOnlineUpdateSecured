package main.java.org.baeldung.security;

import java.io.IOException;
import java.util.Collection;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import main.java.org.baeldung.persistence.model.User;

@Component("myAuthenticationSuccessHandler")
public class MySimpleUrlAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
	
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Autowired
    ActiveUserStore activeUserStore;

    
    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) throws IOException {
    	
    	//System.out.println("onAuthenticationSuccess");
        
        handle(request, response, authentication);

        final HttpSession session = request.getSession(false);
        
        if (session != null) {
        
        	session.setMaxInactiveInterval(30 * 60);
            
        	LoggedUser user = new LoggedUser(authentication.getName(), activeUserStore);
            
        	session.setAttribute("user", user);
        }
        
        clearAuthenticationAttributes(request);
    }

    
    protected void handle(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) throws IOException {
    	
    	//System.out.println("handle");
        
        final String targetUrl = determineTargetUrl(authentication);

    	//System.out.println("targetUrl : " + targetUrl);
        
        if (response.isCommitted()) {
        
        	logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            
        	return;
        }

        redirectStrategy.sendRedirect(request, response, targetUrl);
    }

    
    protected String determineTargetUrl(final Authentication authentication) {
    	
    	//System.out.println("determineTargetUrl");
    	//System.out.println("authentication.toString() : " + authentication.toString());
        
        boolean isSuper = false;
        boolean isAdmin = false;
        boolean isEditor = false;
        boolean isBuyer = false;
        boolean isPublic = false;
        
        User user = (User) authentication.getPrincipal();
        
        final Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        
        for (final GrantedAuthority grantedAuthority : authorities) {
        
        	//System.out.println("grantedAuthority.getAuthority().toString() : " + grantedAuthority.getAuthority().toString());

        	if (grantedAuthority.getAuthority().equals("SUPER_PRIVILEGE")) {

        		isSuper = true;
            }
        	if (grantedAuthority.getAuthority().equals("ADMIN_PRIVILEGE")) {
                
            	isAdmin = true;
            }
        	if (grantedAuthority.getAuthority().equals("EDITOR_PRIVILEGE")) {
                
        		isEditor = true;
            }
        	if (grantedAuthority.getAuthority().equals("CUSTOMER_PRIVILEGE")) {
                
        		isBuyer = true;
            }
        	if (grantedAuthority.getAuthority().equals("PUBLIC_PRIVILEGE")) {
            
        		isPublic = true;
            } 
        }
        
        if (isSuper) {
            
        	//return "/home.html?user=" + authentication.getName();
        	return "/home.html?user=" + user.getFirstAndLastName();
        	//return "/console.html";
        } 
        if (isAdmin) {
            
        	//return "/home.html?user=" + authentication.getName();
        	return "/home.html?user=" + user.getFirstAndLastName();
        } 
        if (isEditor) {
            
        	//return "/home.html?user=" + authentication.getName();
        	return "/home.html?user=" + user.getFirstAndLastName();
        } 
        if (isBuyer) {
            
        	//return "/home.html?user=" + authentication.getName();
        	return "/home.html?user=" + user.getFirstAndLastName();
        }
        if (isPublic) {
            
        	//return "/home.html?user=" + authentication.getName();
        	return "/home.html?user=" + user.getFirstAndLastName();
        }
        
    	return "/home.html";
    	//return "/homepage.html?user=" + authentication.getName();
    }

    
    protected void clearAuthenticationAttributes(final HttpServletRequest request) {
    	
    	//System.out.println("clearAuthenticationAttributes");
        
        final HttpSession session = request.getSession(false);
        
        if (session == null) {

        	return;
        }
        
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }

    
    public void setRedirectStrategy(final RedirectStrategy redirectStrategy) {
    	
    	//System.out.println("setRedirectStrategy");
        
        this.redirectStrategy = redirectStrategy;
    }

    
    protected RedirectStrategy getRedirectStrategy() {

    	//System.out.println("getRedirectStrategy");
        
        return redirectStrategy;
    }
}