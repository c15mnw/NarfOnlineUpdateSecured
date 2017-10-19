package main.java.org.baeldung.web.controller;

import java.util.Locale;

import main.java.org.baeldung.persistence.model.User;
import main.java.org.baeldung.security.ActiveUserStore;
import main.java.org.baeldung.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class UserController {

    @Autowired
    ActiveUserStore activeUserStore;

    @Autowired
    IUserService userService;

    @RequestMapping(value = "/loggedUsers", method = RequestMethod.GET)
    public String getLoggedUsers(final Locale locale, final Model model) {
    	
        model.addAttribute("loggedin", whoIsLoggedIn());

        model.addAttribute("users", activeUserStore.getUsers());
    	model.addAttribute("sessionusers", false);
        
        return "users";
    }

    
    @RequestMapping(value = "/loggedUsersFromSessionRegistry", method = RequestMethod.GET)
    public String getLoggedUsersFromSessionRegistry(final Locale locale, final Model model) {

        model.addAttribute("loggedin", whoIsLoggedIn());

        model.addAttribute("users", userService.getUsersFromSessionRegistry());
    	model.addAttribute("sessionusers", true);
    	
        return "users";
    }

    private String whoIsLoggedIn() {
    	
        if ( SecurityContextHolder.getContext().getAuthentication().getPrincipal() != null) {
        	
        	User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        	return user.getFirstName() + " " + user.getLastName();
        }
        else {
        
            return "";
        }
    }
        

}
