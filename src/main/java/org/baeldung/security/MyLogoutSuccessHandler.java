package main.java.org.baeldung.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

@Component("myLogoutSuccessHandler")
public class MyLogoutSuccessHandler implements LogoutSuccessHandler {

    private static final String VIEW_CONTROLLER_LOGOUT = "/logout.html?logSucc=true";

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        
    	final HttpSession session = request.getSession();
        
    	if (session != null) {
        
    		session.removeAttribute("user");
        }
    	
        response.sendRedirect(request.getContextPath() + VIEW_CONTROLLER_LOGOUT);
    }
}
