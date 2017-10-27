package main.java.org.baeldung.security;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import main.java.org.baeldung.persistence.DataSettings;

@Component("myLogoutSuccessHandler")
public class MyLogoutSuccessHandler implements LogoutSuccessHandler {

    @Autowired
    private DataSettings dataSettings;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        
    	final HttpSession session = request.getSession();
        
    	if (session != null) {
        
    		session.removeAttribute("user");
        }
    	
    	/*
    	 * When Running under Tomcat, change this line to CONTEXT PATH + /logout.html?logSucc=true
    	 */
        if (dataSettings.isTomcatTrue()) {

            response.sendRedirect("/NarfOnlineUpdateSecured/logout.html?logSucc=true");
        }
        else {
        	
            response.sendRedirect("/logout.html?logSucc=true");
        }
    }
}
