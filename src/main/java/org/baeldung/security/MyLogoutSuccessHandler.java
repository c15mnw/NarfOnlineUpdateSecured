package main.java.org.baeldung.security;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

@Component("myLogoutSuccessHandler")
public class MyLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        
    	final HttpSession session = request.getSession();
        
    	if (session != null) {
        
    		session.removeAttribute("user");
        }
    	
    	/*
    	 * When Running under Tomcat, change this line to CONTEXT PATH + /logout.html?logSucc=true
    	 */
        response.sendRedirect("/NarfOnlineUpdateSecured/logout.html?logSucc=true");
        //response.sendRedirect("/logout.html?logSucc=true");
    }
}
