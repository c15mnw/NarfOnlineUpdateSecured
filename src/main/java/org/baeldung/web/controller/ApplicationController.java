package main.java.org.baeldung.web.controller;

import org.hibernate.SessionFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.roslin.mwicks.spring.narf.dto.online.DTOOrder;

import com.roslin.mwicks.spring.narf.enums.EnumOrderStatus;
import com.roslin.mwicks.spring.narf.enums.EnumOrderCollection;
import com.roslin.mwicks.spring.narf.enums.EnumOrderType;

import com.roslin.mwicks.spring.narf.exception.ExceptionAntibodyNotFound;
import com.roslin.mwicks.spring.narf.exception.ExceptionAntibodyReferenceNotFound;
import com.roslin.mwicks.spring.narf.exception.ExceptionLineNotFound;
import com.roslin.mwicks.spring.narf.exception.ExceptionLineReferenceNotFound;
import com.roslin.mwicks.spring.narf.exception.ExceptionOrderNotFound;
import com.roslin.mwicks.spring.narf.exception.ExceptionStrainNotFound;
import com.roslin.mwicks.spring.narf.exception.ExceptionStrainReferenceNotFound;
import com.roslin.mwicks.spring.narf.exception.ExceptionStrainUseNotFound;

import com.roslin.mwicks.spring.narf.model.Antibody;
import com.roslin.mwicks.spring.narf.model.AntibodyReference;

import com.roslin.mwicks.spring.narf.model.BirdOrderLine;
import com.roslin.mwicks.spring.narf.model.BirdOrderLineDateFormat;
import com.roslin.mwicks.spring.narf.model.BirdOrderLineSex;

import com.roslin.mwicks.spring.narf.model.EggOrderLine;
import com.roslin.mwicks.spring.narf.model.EggOrderLineFertilised;

import com.roslin.mwicks.spring.narf.model.EmbryoOrderLine;
import com.roslin.mwicks.spring.narf.model.EmbryoOrderLineIncubation;
import com.roslin.mwicks.spring.narf.model.Line;
import com.roslin.mwicks.spring.narf.model.LineMhc;
import com.roslin.mwicks.spring.narf.model.LineReference;
import com.roslin.mwicks.spring.narf.model.LineResistant;
import com.roslin.mwicks.spring.narf.model.LineSusceptible;

import com.roslin.mwicks.spring.narf.model.Order;
import com.roslin.mwicks.spring.narf.model.OrderCollection;
import com.roslin.mwicks.spring.narf.model.OrderStatus;
import com.roslin.mwicks.spring.narf.model.OrderType;
import com.roslin.mwicks.spring.narf.model.Organism;
import com.roslin.mwicks.spring.narf.model.Strain;
import com.roslin.mwicks.spring.narf.model.StrainReference;
import com.roslin.mwicks.spring.narf.model.StrainUse;

import com.roslin.mwicks.spring.narf.serviceinterface.ServiceLineReference;
import com.roslin.mwicks.spring.narf.serviceinterface.ServiceLineResistant;
import com.roslin.mwicks.spring.narf.serviceinterface.ServiceLineSusceptible;

import com.roslin.mwicks.spring.narf.serviceinterface.ServiceOrder;
import com.roslin.mwicks.spring.narf.serviceinterface.ServiceOrderCollection;
import com.roslin.mwicks.spring.narf.serviceinterface.ServiceOrderStatus;
import com.roslin.mwicks.spring.narf.serviceinterface.ServiceOrderType;
import com.roslin.mwicks.spring.narf.serviceinterface.ServiceOrganism;
import com.roslin.mwicks.spring.narf.serviceinterface.ServiceStrain;
import com.roslin.mwicks.spring.narf.serviceinterface.ServiceStrainReference;
import com.roslin.mwicks.spring.narf.serviceinterface.ServiceStrainUse;

import com.roslin.mwicks.spring.narf.serviceinterface.ServiceAntibody;
import com.roslin.mwicks.spring.narf.serviceinterface.ServiceAntibodyReference;

import com.roslin.mwicks.spring.narf.serviceinterface.ServiceBirdOrderLine;
import com.roslin.mwicks.spring.narf.serviceinterface.ServiceBirdOrderLineDateFormat;
import com.roslin.mwicks.spring.narf.serviceinterface.ServiceBirdOrderLineSex;

import com.roslin.mwicks.spring.narf.serviceinterface.ServiceEggOrderLine;
import com.roslin.mwicks.spring.narf.serviceinterface.ServiceEggOrderLineFertilised;

import com.roslin.mwicks.spring.narf.serviceinterface.ServiceEmbryoOrderLine;
import com.roslin.mwicks.spring.narf.serviceinterface.ServiceEmbryoOrderLineIncubation;

import com.roslin.mwicks.spring.narf.serviceinterface.ServiceLine;
import com.roslin.mwicks.spring.narf.serviceinterface.ServiceLineMhc;

import com.roslin.mwicks.utility.ObjectConverter;

import main.java.org.baeldung.persistence.DataSettings;
import main.java.org.baeldung.persistence.dao.RoleRepository;
import main.java.org.baeldung.persistence.model.Role;
import main.java.org.baeldung.persistence.model.User;

import main.java.org.baeldung.security.ActiveUserStore;
import main.java.org.baeldung.service.IUserService;



/**
 * @author Mike Wicks
 */
@Controller
@SessionAttributes("references")
public class ApplicationController extends AbstractController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationController.class);

    @Autowired
    private DataSettings dataSettings;

    @Resource
    private MessageSource messageSource;
    
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private Environment env;

    @Autowired
    private MessageSource messages;

    @Resource
    private ServiceAntibody serviceantibody;
    @Resource
    private ServiceAntibodyReference serviceantibodyreference;
    @Resource
    private ServiceLine serviceline;
    @Resource
    private ServiceLineReference servicelinereference;
    @Resource
    private ServiceLineMhc servicelinemhc;
    @Resource
    private ServiceLineResistant servicelineresistant;
    @Resource
    private ServiceLineSusceptible servicelinesusceptible;
    @Resource
    private ServiceStrain servicestrain;
    @Resource
    private ServiceStrainReference servicestrainreference;
    @Resource
    private ServiceStrainUse servicestrainuse;
    @Resource
    private ServiceBirdOrderLine servicebirdorderline;
    @Resource
    private ServiceBirdOrderLineSex servicebirdorderlinesex;
    @Resource
    private ServiceBirdOrderLineDateFormat servicebirdorderlinedateformat;
    @Resource
    private ServiceEggOrderLine serviceeggorderline;
    @Resource
    private ServiceEggOrderLineFertilised serviceeggorderlinefertilised;
    @Resource
    private ServiceEmbryoOrderLine serviceembryoorderline;
    @Resource
    private ServiceEmbryoOrderLineIncubation serviceembryoorderlineincubation;
    @Resource
    private ServiceOrder serviceorder;
    @Resource
    private ServiceOrderStatus serviceorderstatus;
    @Resource
    private ServiceOrderCollection serviceordercollection;
    @Resource
    private ServiceOrderType serviceordertype;
    @Resource
    private ServiceOrganism serviceorganism;

    @Autowired
    SessionFactory sessionFactory;

    @Autowired
    ActiveUserStore activeUserStore;

    @Autowired
    IUserService userService;

    @Autowired
    private RoleRepository roleRepository;


    /*
     * HOME Page 
     */
    @RequestMapping(value = { "/home", "/" }, method = RequestMethod.GET)
    public String homePage(ModelMap model) {
    	
        LOGGER.debug("Rendering HOME page");

        model.addAttribute("loggedin", whoIsLoggedIn());

    	model.addAttribute("publicBool", false);
    	model.addAttribute("customerBool", false);
    	model.addAttribute("editorBool", false);
    	model.addAttribute("adminBool", false);
    	model.addAttribute("superBool", false);

    	if ( determinePrivilege() == 5 ) {

        	model.addAttribute("superBool", true);
        }
        if (determinePrivilege() == 4 ) {
        	
        	model.addAttribute("adminBool", true);
        }
        if ( determinePrivilege() == 3 ) {
        	
        	model.addAttribute("editorBool", true);
        }
        if ( determinePrivilege() == 2 ) {
        	
        	model.addAttribute("customerBool", true);
        }
        if ( determinePrivilege() == 1 ) {
        	
        	model.addAttribute("publicBool", true);
        }
        
        return "home";
    }
    

    /*
     * CRUD for User - LIST
     */
    @RequestMapping(value = { "/users-list" }, 
    		method = RequestMethod.GET)
    public String listUsers(ModelMap model) {
    	
        LOGGER.debug("Rendering /userslist - listUsers()");

        model.addAttribute("loggedin", whoIsLoggedIn());

    	if ( determinePrivilege() == 5 ) {

            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", true);
        }
        if (determinePrivilege() == 4 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("adminBool", true);
        }
        if ( determinePrivilege() == 3 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("editorBool", true);
        }
        if ( determinePrivilege() == 2 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("customerBool", true);
        }
        if ( determinePrivilege() == 1 ) {
        	
            model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("publicBool", true);
        }

        List<User> users = userService.getUsers();
    	Collections.sort(users, new User.OrderByIdAsc());
    	
        model.addAttribute("users", users);

        return "userslist";
    }
    
    
    /*
     * CRUD for User - READ 
     */
    @RequestMapping(value = { "/show-user-{oid}" }, 
    		method = RequestMethod.GET)
    public String showUser(@PathVariable String oid, ModelMap model) {
     
        LOGGER.debug("Rendering /show-user - showUser()");

        model.addAttribute("loggedin", whoIsLoggedIn());

        User user = userService.getUserByID( ObjectConverter.convert(oid, Long.class) );
        //User user = userRepository.getOne( ObjectConverter.convert(oid, Long.class) );
    	model.addAttribute("user", user);

        return "usershow";
    }
    
    /*
     * CRUD for User - UPDATE - Input
     */
    @RequestMapping(value = { "/edit-user-{oid}" }, 
    		method = RequestMethod.GET)
    public String editUser(@PathVariable String oid, ModelMap model) {
     
        LOGGER.debug("Rendering /edit-user-oid - editUser()");
        
        model.addAttribute("loggedin", whoIsLoggedIn());

        User user = userService.getUserByID( ObjectConverter.convert(oid, Long.class) );
        //User user = userRepository.getOne( ObjectConverter.convert(oid, Long.class) );

        model.addAttribute("user", user);

    	List<Role> userRoles = roleRepository.findAll();
    	Collections.sort(userRoles, new Role.OrderByIdAsc());
    	model.addAttribute("roles", userRoles);
    	
        model.addAttribute("editBool", true);

        return "user";
    }
    
    /*
     * CRUD for User - UPDATE - Process
     */
    @RequestMapping(value = { "/edit-user-{oid}" }, 
    		method = RequestMethod.POST)
    public String updateUser(@Valid User user, 
    		BindingResult result,
            ModelMap model, 
            @PathVariable String oid) {
 
        LOGGER.debug("Processing /edit-user-oid - updateUser()");

        model.addAttribute("loggedin", whoIsLoggedIn());

        if (result.hasErrors()) {
        	
            return "user";
        }
 
        /*
        if(!userService.isUserNameUnique( user.getOid(), user.getName() ) ){
        	
            FieldError nameError = new FieldError("user","name", messageSource.getMessage("non.unique.name.user", new String[]{ user.getName()}, Locale.getDefault()));
            
            result.addError(nameError);
            
            return "user";
        }

        */
        userService.saveRegisteredUser( user );

        model.addAttribute("message", "user " + user.getFirstName() + " " + user.getLastName() + " UPDATED successfully");

        return "usersuccess";
    }
    
    /*
     * CRUD for User - DELETE
     */
    @RequestMapping(value = { "/delete-user-{oid}" }, 
    		method = RequestMethod.GET)
    public String deleteUser(@PathVariable String oid, ModelMap model) {
    	
        LOGGER.debug("Processing /delete-user-oid - deleteUser()");

        
        model.addAttribute("loggedin", whoIsLoggedIn());
        

        User user = userService.getUserByID( ObjectConverter.convert(oid, Long.class) );

        userService.deleteUser( user );
        
        //model.addAttribute("message", "user " + user.getName() + " "+ user.getAntigen() + " DELETED successfully");

        return "usersuccess";
    }
     

    
    /*
     * CRUD for Antibody - LIST
     */
    @RequestMapping(value = { "/antibodys-list" }, 
    		method = RequestMethod.GET)
    public String listAntibodys(ModelMap model) {
    	
        LOGGER.debug("Rendering /antibodyslist - listAntibodys()");

        model.addAttribute("loggedin", whoIsLoggedIn());

    	if ( determinePrivilege() == 5 ) {

            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", true);
        }
        if (determinePrivilege() == 4 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("adminBool", true);
        }
        if ( determinePrivilege() == 3 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("editorBool", true);
        }
        if ( determinePrivilege() == 2 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("customerBool", true);
        }
        if ( determinePrivilege() == 1 ) {
        	
            model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("publicBool", true);
        }

        List<Antibody> antibodys = serviceantibody.findAll();
    	Collections.sort(antibodys, new Antibody.OrderByOidAsc());
    	
        model.addAttribute("antibodys", antibodys);

        return "antibodyslist";
    }
    
    /*
     * CRUD for Antibody - CREATE - Input
     */
    @RequestMapping(value = { "/new-antibody" }, 
    		method = RequestMethod.GET)
    public String newAntibody(ModelMap model) {
        
        LOGGER.debug("Rendering /new-antibody - newAntibody()");

        model.addAttribute("loggedin", whoIsLoggedIn());

        Antibody antibody = new Antibody();
    	model.addAttribute("antibody", antibody);
        
    	List<AntibodyReference> antibodyReferences = serviceantibodyreference.findAll();
    	Collections.sort(antibodyReferences, new AntibodyReference.OrderByReferenceAsc());
    	model.addAttribute("antibodyReferences", antibodyReferences);
    	
        model.addAttribute("editBool", false);
        
        return "antibody";
    }
    
    /*
     * CRUD for Antibody - CREATE - Process 
     */
    @RequestMapping(value = { "/new-antibody" }, 
    		method = RequestMethod.POST)
    public String saveAntibody(@Valid Antibody antibody, 
    		BindingResult result,
            ModelMap model
            ) {
 
        LOGGER.debug("Processing /new-antibody - saveAntibody()");

        model.addAttribute("loggedin", whoIsLoggedIn());

        if (result.hasErrors()) {
            
        	return "antibody";
        }
 
        if(!serviceantibody.isAntibodyNameUnique( antibody.getOid(), antibody.getName() ) ){
        	
            FieldError nameError = new FieldError("antibody","name", messageSource.getMessage("non.unique.name.antibody", new String[]{ antibody.getName()}, Locale.getDefault()));
            
            result.addError(nameError);
            
            return "antibody";
        }
         
        serviceantibody.save(antibody);

        model.addAttribute("message", "antibody " + antibody.getName() + " "+ antibody.getAntigen() + " ADDED successfully");

        return "antibodysuccess";
    }
    
    /*
     * CRUD for Antibody - READ 
     */
    @RequestMapping(value = { "/show-antibody-{oid}" }, 
    		method = RequestMethod.GET)
    public String showAntibody(@PathVariable String oid, ModelMap model) {
     
        LOGGER.debug("Rendering /show-antibody - showAntibody()");

        model.addAttribute("loggedin", whoIsLoggedIn());

        Antibody antibody = serviceantibody.findByOid( ObjectConverter.convert(oid, Long.class) );
    	model.addAttribute("antibody", antibody);

    	List<AntibodyReference> antibodyReferences = serviceantibodyreference.findAll();
    	Collections.sort(antibodyReferences, new AntibodyReference.OrderByReferenceAsc());
    	model.addAttribute("antibodyReferences", antibodyReferences);
    	
        return "antibodyshow";
    }
    
    /*
     * CRUD for Antibody - UPDATE - Input
     */
    @RequestMapping(value = { "/edit-antibody-{oid}" }, 
    		method = RequestMethod.GET)
    public String editAntibody(@PathVariable String oid, ModelMap model) {
     
        LOGGER.debug("Rendering /edit-antibody-oid - editAntibody()");

        model.addAttribute("loggedin", whoIsLoggedIn());

        Antibody antibody = serviceantibody.findByOid( ObjectConverter.convert(oid, Long.class) );
    	model.addAttribute("antibody", antibody);

    	List<AntibodyReference> antibodyReferences = serviceantibodyreference.findAll();
    	Collections.sort(antibodyReferences, new AntibodyReference.OrderByReferenceAsc());
    	model.addAttribute("antibodyReferences", antibodyReferences);
    	
        model.addAttribute("new", false);
        model.addAttribute("editBool", true);

        return "antibody";
    }
    
    /*
     * CRUD for Antibody - UPDATE - Process
     */
    @RequestMapping(value = { "/edit-antibody-{oid}" }, 
    		method = RequestMethod.POST)
    public String updateAntibody(@Valid Antibody antibody, 
    		BindingResult result,
            ModelMap model, 
            @PathVariable String oid) {
 
        LOGGER.debug("Processing /edit-antibody-oid - updateAntibody()");

        model.addAttribute("loggedin", whoIsLoggedIn());

        if (result.hasErrors()) {
        	
            return "antibody";
        }
 
        if(!serviceantibody.isAntibodyNameUnique( antibody.getOid(), antibody.getName() ) ){
        	
            FieldError nameError = new FieldError("antibody","name", messageSource.getMessage("non.unique.name.antibody", new String[]{ antibody.getName()}, Locale.getDefault()));
            
            result.addError(nameError);
            
            return "antibody";
        }

        try {
		
        	serviceantibody.update(antibody);
		} 
        catch (ExceptionAntibodyNotFound e) {
			
        	e.printStackTrace();
		}

        model.addAttribute("message", "antibody " + antibody.getName() + " "+ antibody.getAntigen() + " UPDATED successfully");

        return "antibodysuccess";
    }
    
    /*
     * CRUD for Antibody - DELETE
     */
    @RequestMapping(value = { "/delete-antibody-{oid}" }, 
    		method = RequestMethod.GET)
    public String deleteAntibody(@PathVariable String oid, ModelMap model) {
    	
        LOGGER.debug("Processing /delete-antibody-oid - deleteAntibody()");

        model.addAttribute("loggedin", whoIsLoggedIn());

        Antibody antibody = serviceantibody.findByOid( ObjectConverter.convert(oid, Long.class) );

    	try {
			
    		serviceantibody.delete( antibody );
		} 
    	catch (ExceptionAntibodyNotFound e) {
    		
			e.printStackTrace();
		}
        
        model.addAttribute("message", "antibody " + antibody.getName() + " "+ antibody.getAntigen() + " DELETED successfully");

        return "antibodysuccess";
    }
     
 
    /*
     * CRUD for AntibodyReference - LIST
     */
    @RequestMapping(value = { "/antibody-references-list" }, 
    		method = RequestMethod.GET)
    public String listAntibodyReferences(ModelMap model) {
    	
        LOGGER.debug("Rendering /antibody-references-list - listAntibodyReferences()");

        model.addAttribute("loggedin", whoIsLoggedIn());

    	if ( determinePrivilege() == 5 ) {

            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", true);
        }
        if (determinePrivilege() == 4 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("adminBool", true);
        }
        if ( determinePrivilege() == 3 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("editorBool", true);
        }
        if ( determinePrivilege() == 2 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("customerBool", true);
        }
        if ( determinePrivilege() == 1 ) {
        	
            model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("publicBool", true);
        }

        List<AntibodyReference> antibodyreferences = serviceantibodyreference.findAll();
    	Collections.sort(antibodyreferences, new AntibodyReference.OrderByReferenceAsc());
    	model.addAttribute("antibodyreferences", antibodyreferences);

        return "antibodyreferenceslist";
    }

    /*
     * CRUD for AntibodyReference - CREATE - Input
     */
    @RequestMapping(value = { "/new-antibodyreference" }, 
    		method = RequestMethod.GET)
    public String newAntibodyReference(ModelMap model) {
        
        LOGGER.debug("Rendering /new-antibodyreference - newAntibodyReference()");

        model.addAttribute("loggedin", whoIsLoggedIn());

        AntibodyReference antibodyReference = new AntibodyReference();
    	model.addAttribute("antibodyreference", antibodyReference);
        
        model.addAttribute("editBool", false);
        
        return "antibodyreference";
    }
    
    /*
     * CRUD for AntibodyReference - CREATE - Process 
     */
    @RequestMapping(value = { "/new-antibodyreference" }, 
    		method = RequestMethod.POST)
    public String saveAntibodyReference(AntibodyReference antibodyReference, 
    		BindingResult result,
            ModelMap model) {
 
        LOGGER.debug("Processing /new-antibodyreference - saveAntibodyReference()");

        model.addAttribute("loggedin", whoIsLoggedIn());

        if (result.hasErrors()) {
            
        	return "antibodyreference";
        }
 
        if(!serviceantibodyreference.isAntibodyReferenceReferenceUnique( antibodyReference.getOid(), antibodyReference.getReference() ) ){
        	
            FieldError nameError = new FieldError("antibodyReference", "antibodyReference", messageSource.getMessage("non.unique.name.antibodyReference", new String[]{ antibodyReference.getReference()}, Locale.getDefault()));
            
            result.addError(nameError);
            
            return "antibodyreference";
        }
         
        serviceantibodyreference.save(antibodyReference);
 
        model.addAttribute("success", "antibodyReference " + antibodyReference.getReference() + " "+ antibodyReference.getUrl() + " ADDED successfully");

        return "antibodyreferencesuccess";
    }
    
    /*
     * CRUD for AntibodyReference - READ 
     */
    @RequestMapping(value = { "/show-antibodyreference-{oid}" }, 
    		method = RequestMethod.GET)
    public String showAntibodyReference(@PathVariable String oid, ModelMap model) {
     
        LOGGER.debug("Rendering /show-antibodyreference-oid - showAntibodyReference()");

        model.addAttribute("loggedin", whoIsLoggedIn());

        AntibodyReference antibodyReference = serviceantibodyreference.findByOid( ObjectConverter.convert(oid, Long.class) );
    	model.addAttribute("antibodyreference", antibodyReference);

        return "antibodyreferenceshow";
    }
    
    /*
     * CRUD for AntibodyReference - UPDATE - Input
     */
    @RequestMapping(value = { "/edit-antibodyreference-{oid}" }, 
    		method = RequestMethod.GET)
    public String editAntibodyReference(@PathVariable String oid, ModelMap model) {
     
        LOGGER.debug("Rendering /edit-antibodyreference-oid - editAntibodyReference()");

        model.addAttribute("loggedin", whoIsLoggedIn());

        AntibodyReference antibodyReference = serviceantibodyreference.findByOid( ObjectConverter.convert(oid, Long.class) );
    	model.addAttribute("antibodyreference", antibodyReference);

        model.addAttribute("editBool", true);

        return "antibodyreference";
    }
    
    /*
     * CRUD for AntibodyReference - UPDATE - Process
     */
    @RequestMapping(value = { "/edit-antibodyreference-{oid}" }, 
    		method = RequestMethod.POST)
    public String updateAntibodyReference(AntibodyReference antibodyReference, 
    		BindingResult result,
            ModelMap model, 
            @PathVariable String oid) {
 
        LOGGER.debug("Processing /edit-antibodyreference-oid - updateAntibodyReference()");

        model.addAttribute("loggedin", whoIsLoggedIn());

        if (result.hasErrors()) {
        	
            return "antibodyreference";
        }
 
        if(!serviceantibodyreference.isAntibodyReferenceReferenceUnique( antibodyReference.getOid(), antibodyReference.getReference() ) ){
        	
            FieldError nameError = new FieldError("antibodyReference", "antibodyReference", messageSource.getMessage("non.unique.name.antibodyReference", new String[]{ antibodyReference.getReference()}, Locale.getDefault()));
            
            result.addError(nameError);
            
            return "antibodyreference";
        }

        try {
		
        	serviceantibodyreference.update(antibodyReference);
		} 
        catch (ExceptionAntibodyReferenceNotFound e) {
			
        	e.printStackTrace();
		}

        model.addAttribute("message", "antibodyReference " + antibodyReference.getReference() + " "+ antibodyReference.getUrl() + " updated successfully");

        return "antibodyreferencesuccess";
    }
    
    /*
     * CRUD for AntibodyReference - DELETE
     */
    @RequestMapping(value = { "/delete-antibodyreference-{oid}" }, 
    		method = RequestMethod.GET)
    public String deleteAntibodyReference(@PathVariable String oid, ModelMap model) {
    	
        LOGGER.debug("Processing /delete-antibodyreference-oid - deleteAntibodyReference()");

        model.addAttribute("loggedin", whoIsLoggedIn());

        AntibodyReference antibodyReference = serviceantibodyreference.findByOid( ObjectConverter.convert(oid, Long.class) );

    	try {
			
    		List<Antibody> antibodys = serviceantibody.findAllByAntibodyReference( antibodyReference );

        	Iterator<Antibody> iteratorAntibody = antibodys.iterator();
            
            while (iteratorAntibody.hasNext()) {
        		
            	Antibody antibody = iteratorAntibody.next();

            	antibody.removeAntibodyReference(antibodyReference);
            	
            	serviceantibody.save(antibody);
            }

    		serviceantibodyreference.deleteByOid( ObjectConverter.convert(oid, Long.class) );
		} 
    	catch (ExceptionAntibodyReferenceNotFound e) {
    		
			e.printStackTrace();
		}
        
        model.addAttribute("message", "antibodyReference " + antibodyReference.getReference() + " "+ antibodyReference.getUrl() + " updated successfully");

        return "antibodyreferencesuccess";
    }


    /*
     * CRUD for  Line - CREATE - Input
     */
    @RequestMapping(value = { "/new-line" }, 
    		method = RequestMethod.GET)
    public String newLine(ModelMap model) {
        
        LOGGER.debug("Rendering /new-line - newLine()");

        model.addAttribute("loggedin", whoIsLoggedIn());

        Line line = new Line();
    	model.addAttribute("line", line);
        
    	List<LineReference> lineReferences = servicelinereference.findAll();
    	Collections.sort(lineReferences, new LineReference.OrderByReferenceAsc());
    	model.addAttribute("LineReferences", lineReferences);
    	
    	List<LineMhc> lineMhcs = servicelinemhc.findAll();
    	Collections.sort(lineMhcs, new LineMhc.OrderByOidAsc());
    	model.addAttribute("LineMhcs", lineMhcs);

    	List<LineResistant> lineResistants = servicelineresistant.findAll();
    	Collections.sort(lineResistants, new LineResistant.OrderByOidAsc());
    	model.addAttribute("LineResistants", lineResistants);

    	List<LineSusceptible> lineSusceptibles = servicelinesusceptible.findAll();
    	Collections.sort(lineSusceptibles, new LineSusceptible.OrderByOidAsc());
    	model.addAttribute("LineSusceptibles", lineSusceptibles);
        
        model.addAttribute("editBool", false);
        
        return "line";
    }
    
    /*
     * CRUD for Line - CREATE - Process
     */
    @RequestMapping(value = { "/new-line" }, 
    		method = RequestMethod.POST)
    public String saveLine(@Valid Line line,
    		BindingResult result,
            ModelMap model) {
 
        LOGGER.debug("Processing /new-line - saveLine()");

        model.addAttribute("loggedin", whoIsLoggedIn());

        if (result.hasErrors()) {
            
        	return "line";
        }
 
        if(!serviceline.isLineNameUnique( line.getOid(), line.getLine() ) ){
        	
            FieldError nameError = new FieldError("Line","name", messageSource.getMessage("non.unique.name.Line", new String[]{ line.getLine()}, Locale.getDefault()));
            
            result.addError(nameError);
            
            return "line";
        }
         
        serviceline.save(line);
 
        model.addAttribute("message", " Line " + line.getLine() + " "+ line.getBreed() + " ADDED successfully");

        return "linesuccess";
    }
    
    /*
     * CRUD for  Line - READ 
     */
    @RequestMapping(value = { "/show-line-{oid}" }, 
    		method = RequestMethod.GET)
    public String showLine(@PathVariable String oid, ModelMap model) {
     
        LOGGER.debug("Rendering /show-line-oid - showLine()");

        model.addAttribute("loggedin", whoIsLoggedIn());

        Line Line = serviceline.findByOid( ObjectConverter.convert(oid, Long.class) );
    	model.addAttribute("line", Line);

        return "lineshow";
    }

    /*
     * CRUD for Line - LIST
     */
    @RequestMapping(value = { "/lines-list" }, 
    		method = RequestMethod.GET)
    public String listLines(ModelMap model) {
    	
        LOGGER.debug("Rendering /lines-list - listLines()");

        model.addAttribute("loggedin", whoIsLoggedIn());

    	if ( determinePrivilege() == 5 ) {

            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", true);
        }
        if (determinePrivilege() == 4 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("adminBool", true);
        }
        if ( determinePrivilege() == 3 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("editorBool", true);
        }
        if ( determinePrivilege() == 2 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("customerBool", true);
        }
        if ( determinePrivilege() == 1 ) {
        	
            model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("publicBool", true);
        }

        List<Line> lines = serviceline.findAll();
        
        model.addAttribute("lines", lines);

        return "lineslist";
    }

    /*
     * CRUD for Line - UPDATE - INPUT
     */
    @RequestMapping(value = { "/edit-line-{oid}" }, 
    		method = RequestMethod.GET)
    public String editLine(@PathVariable String oid, ModelMap model) {
     
        LOGGER.debug("Rendering /edit-line-oid - editLine()");

        model.addAttribute("loggedin", whoIsLoggedIn());

        Line line = serviceline.findByOid( ObjectConverter.convert(oid, Long.class) );
    	model.addAttribute("line", line);

    	List<LineReference> lineReferences = servicelinereference.findAll();
    	Collections.sort(lineReferences, new LineReference.OrderByReferenceAsc());
    	model.addAttribute("LineReferences", lineReferences);
    	
    	List<LineMhc> lineMhcs = servicelinemhc.findAll();
    	Collections.sort(lineMhcs, new LineMhc.OrderByOidAsc());
    	model.addAttribute("LineMhcs", lineMhcs);

    	List<LineResistant> lineResistants = servicelineresistant.findAll();
    	Collections.sort(lineResistants, new LineResistant.OrderByOidAsc());
    	model.addAttribute("LineResistants", lineResistants);

    	List<LineSusceptible> lineSusceptibles = servicelinesusceptible.findAll();
    	Collections.sort(lineSusceptibles, new LineSusceptible.OrderByOidAsc());
    	model.addAttribute("LineSusceptibles", lineSusceptibles);
        
        model.addAttribute("editBool", true);

        return "line";
    }
  
    /*
     * CRUD for Line - UPDATE - Process
     */
    @RequestMapping(value = { "/edit-line-{oid}" }, 
    		method = RequestMethod.POST)
    public String updateLine(@Valid Line line, 
    		BindingResult result,
            ModelMap model, 
            @PathVariable String oid) {
 
        LOGGER.debug("Processing /edit-line-oid - updateLine()");

        model.addAttribute("loggedin", whoIsLoggedIn());

        if (result.hasErrors()) {
        	
            return "line";
        }
 
        if(!serviceline.isLineNameUnique( line.getOid(), line.getLine() ) ){
        	
            FieldError nameError = new FieldError("Line","name", messageSource.getMessage("non.unique.name.Line", new String[]{ line.getLine()}, Locale.getDefault()));
            
            result.addError(nameError);
            
            return "line";
        }

        try {
		
        	serviceline.update(line);
		} 
        catch (ExceptionLineNotFound e) {
			
        	e.printStackTrace();
		}

        model.addAttribute("message", " Line " + line.getLine() + " "+ line.getBreed() + " UPDATED successfully");

        return "linesuccess";
    }
    
    /*
     * CRUD for Line - DELETE
     */
    @RequestMapping(value = { "/delete-line-{oid}" }, 
    		method = RequestMethod.GET)
    public String deleteLine(@PathVariable String oid, ModelMap model) {

        LOGGER.debug("Processing /delete-line-oid - deleteLine()");

        model.addAttribute("loggedin", whoIsLoggedIn());

        Line Line = serviceline.findByOid( ObjectConverter.convert(oid, Long.class) );

    	try {
			
    		serviceline.delete( Line );
		} 
    	catch (ExceptionLineNotFound e) {
    		
			e.printStackTrace();
		}
        
        model.addAttribute("message", " Line " + Line.getLine() + " "+ Line.getBreed() + " DELETED successfully");

        return "linesuccess";
    }
    
    
    
    /*
     * CRUD for LineReference - READ 
     */
    @RequestMapping(value = { "/show-linereference-{oid}" }, 
    		method = RequestMethod.GET)
    public String showLineReference(@PathVariable String oid, ModelMap model) {
     
        LOGGER.debug("Rendering /show-linereference-oid - showLineReference()");

        model.addAttribute("loggedin", whoIsLoggedIn());

        LineReference linereference = servicelinereference.findByOid( ObjectConverter.convert(oid, Long.class) );
    	model.addAttribute("linereference", linereference);

        return "linereferenceshow";
    }
    
    /*
     * CRUD for LineReference - LIST
     */
    @RequestMapping(value = { "/line-references-list" }, 
    		method = RequestMethod.GET)
    public String listLineReferences(ModelMap model) {
    	
        LOGGER.debug("Rendering /line-references-list - listLineReferences()");

        model.addAttribute("loggedin", whoIsLoggedIn());

    	if ( determinePrivilege() == 5 ) {

            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", true);
        }
        if (determinePrivilege() == 4 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("adminBool", true);
        }
        if ( determinePrivilege() == 3 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("editorBool", true);
        }
        if ( determinePrivilege() == 2 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("customerBool", true);
        }
        if ( determinePrivilege() == 1 ) {
        	
            model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("publicBool", true);
        }

        List<LineReference> linereferences = servicelinereference.findAll();
    	Collections.sort(linereferences, new LineReference.OrderByReferenceAsc());
    	model.addAttribute("linereferences", linereferences);

        return "linereferenceslist";
    }

    /*
     * CRUD for LineReference - CREATE - Input
     */
    @RequestMapping(value = { "/new-linereference" }, 
    		method = RequestMethod.GET)
    public String newLineReference(ModelMap model) {
        
        LOGGER.debug("Rendering /new-linereference - newLineReference()");

        model.addAttribute("loggedin", whoIsLoggedIn());

        LineReference LineReference = new LineReference();
    	model.addAttribute("linereference", LineReference);
        
        model.addAttribute("editBool", false);
        
        return "linereference";
    }
    
    /*
     * CRUD for LineReference - CREATE - Process
     */
    @RequestMapping(value = { "/new-linereference" }, 
    		method = RequestMethod.POST)
    public String saveLineReference(LineReference LineReference, 
    		BindingResult result,
            ModelMap model) {
 
        LOGGER.debug("Processing /new-linereference - saveLineReference()");

        model.addAttribute("loggedin", whoIsLoggedIn());

        if (result.hasErrors()) {
            
        	return "linereference";
        }
 
        if(!servicelinereference.isLineReferenceReferenceUnique( LineReference.getOid(), LineReference.getReference() ) ){
        	
            FieldError nameError = new FieldError("LineReference", "LineReference", messageSource.getMessage("non.unique.name.LineReference", new String[]{ LineReference.getReference()}, Locale.getDefault()));
            
            result.addError(nameError);
            
            return "linereference";
        }
         
        servicelinereference.save(LineReference);
 
        model.addAttribute("message", " Line Reference " + LineReference.getReference() + " "+ LineReference.getUrl() + " ADDED successfully");

        return "linereferencesuccess";
    }
    
    /*
     * CRUD for LineReference - UPDATE - Input
     */
    @RequestMapping(value = { "/edit-linereference-{oid}" }, 
    		method = RequestMethod.GET)
    public String editLineReference(@PathVariable String oid, ModelMap model) {
     
        LOGGER.debug("Rendering /edit-linereference-oid - editLineReference()");

        model.addAttribute("loggedin", whoIsLoggedIn());

        LineReference LineReference = servicelinereference.findByOid( ObjectConverter.convert(oid, Long.class) );
    	model.addAttribute("linereference", LineReference);

        model.addAttribute("editBool", true);

        return "linereference";
    }
    
    /*
     * CRUD for LineReference - UPDATE - Process
     */
    @RequestMapping(value = { "/edit-linereference-{oid}" }, 
    		method = RequestMethod.POST)
    public String updateLineReference(LineReference LineReference, 
    		BindingResult result,
            ModelMap model, 
            @PathVariable String oid) {
 
        LOGGER.debug("Processing /edit-linereference-oid - updateLineReference()");

        model.addAttribute("loggedin", whoIsLoggedIn());

        if (result.hasErrors()) {
        	
            return "linereference";
        }
 
        if(!servicelinereference.isLineReferenceReferenceUnique( LineReference.getOid(), LineReference.getReference() ) ){
        	
            FieldError nameError = new FieldError("LineReference", "LineReference", messageSource.getMessage("non.unique.name.LineReference", new String[]{ LineReference.getReference()}, Locale.getDefault()));
            
            result.addError(nameError);
            
            return "linereference";
        }

        try {
		
        	servicelinereference.update(LineReference);
		} 
        catch (ExceptionLineReferenceNotFound e) {
			
        	e.printStackTrace();
		}

        model.addAttribute("message", " Line Reference " + LineReference.getReference() + " "+ LineReference.getUrl() + " updated successfully");

        return "linereferencesuccess";
    }
    
    /*
     * CRUD for LineReference - DELETE
     */
    @RequestMapping(value = { "/delete-linereference-{oid}" }, 
    		method = RequestMethod.GET)
    public String deleteLineReference(@PathVariable String oid, ModelMap model) {
    	
        LOGGER.debug("Processing /delete-linereference-oid - deleteLineReference()");

        model.addAttribute("loggedin", whoIsLoggedIn());

        LineReference LineReference = servicelinereference.findByOid( ObjectConverter.convert(oid, Long.class) );

    	try {

    		List<Line> lines = serviceline.findAllByLineReference( LineReference );

        	Iterator<Line> iteratorLine = lines.iterator();
            
            while (iteratorLine.hasNext()) {
        		
            	Line line = iteratorLine.next();

            	line.removeLineReference(LineReference);
            	
            	serviceline.save(line);
            }

    		servicelinereference.delete( LineReference );
		} 
    	catch (ExceptionLineReferenceNotFound e) {
    		
			e.printStackTrace();
		}
        
        model.addAttribute("message", " Line Reference " + LineReference.getReference() + " "+ LineReference.getUrl() + " DELETED successfully");

        return "linereferencesuccess";
    }
     
    
    /*
     * CRUD for Strain - CREATE - Input 
     */
    @RequestMapping(value = { "/new-strain" }, 
    		method = RequestMethod.GET)
    public String newStrain(
        ModelMap model) {
        
        LOGGER.debug("Rendering /new-strain - newStrain()");

        model.addAttribute("loggedin", whoIsLoggedIn());

        Strain Strain = new Strain();
    	model.addAttribute("strain", Strain);
        
    	List<StrainReference> strainreferences = servicestrainreference.findAll();
    	Collections.sort(strainreferences, new StrainReference.OrderByReferenceAsc());
    	model.addAttribute("StrainReferences", strainreferences);
    	
    	List<StrainUse> strainuses = servicestrainuse.findAll();
    	Collections.sort(strainuses, new StrainUse.OrderByUseAsc());
    	model.addAttribute("StrainUses", strainuses);
        
        model.addAttribute("editBool", false);
        
        return "strain";
    }
    
    /*
     * CRUD for Strain - CREATE - Process 
     */
    @RequestMapping(value = { "/new-strain" }, 
    		method = RequestMethod.POST)
    public String saveStrain(
            @Valid Strain strain, 
    		BindingResult result,
            ModelMap model) {
 
        LOGGER.debug("Processing /new-strain - saveStrain()");

        model.addAttribute("loggedin", whoIsLoggedIn());

        if (result.hasErrors()) {
            
        	return "strain";
        }
 
        if(!servicestrain.isStrainNameUnique( strain.getOid(), strain.getStrain() ) ){
        	
            FieldError nameError = new FieldError("Strain","name", messageSource.getMessage("non.unique.name.Strain", new String[]{ strain.getStrain()}, Locale.getDefault()));
            
            result.addError(nameError);
            
            return "strain";
        }
         
        servicestrain.save(strain);
 
        model.addAttribute("message", " Strain " + strain.getStrain() + " "+ strain.getStrainLong() + " ADDED successfully");

        return "strainsuccess";
    }
    
    /*
     * CRUD for  Strain - READ 
     */
    @RequestMapping(value = { "/show-strain-{oid}" }, 
    		method = RequestMethod.GET)
    public String showStrain(@PathVariable String oid, ModelMap model) {
     
        LOGGER.debug("Rendering /show-strain-oid - showStrain()");

        model.addAttribute("loggedin", whoIsLoggedIn());

        Strain Strain = servicestrain.findByOid( ObjectConverter.convert(oid, Long.class) );
    	model.addAttribute("strain", Strain);

        return "strainshow";
    }

    /*
     * CRUD for Strain - LIST 
     */
    @RequestMapping(value = { "/strains-list" }, 
    		method = RequestMethod.GET)
    public String listStrains(
        ModelMap model) {
    	
        LOGGER.debug("Rendering /strains-list - listStrains()");

        model.addAttribute("loggedin", whoIsLoggedIn());

    	if ( determinePrivilege() == 5 ) {

            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", true);
        }
        if (determinePrivilege() == 4 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("adminBool", true);
        }
        if ( determinePrivilege() == 3 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("editorBool", true);
        }
        if ( determinePrivilege() == 2 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("customerBool", true);
        }
        if ( determinePrivilege() == 1 ) {
        	
            model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("publicBool", true);
        }

        List<Strain> strains = servicestrain.findAll();
        
        model.addAttribute("strains", strains);

        return "strainslist";
    }
    
    /*
     * CRUD for Strain - UPDATE - Input 
     */
    @RequestMapping(value = { "/edit-strain-{oid}" }, 
    		method = RequestMethod.GET)
    public String editStrain(
        @PathVariable String oid, 
        ModelMap model) {
     
        LOGGER.debug("Rendering /edit-strain-oid - editStrain()");

        model.addAttribute("loggedin", whoIsLoggedIn());

        Strain strain = servicestrain.findByOid( ObjectConverter.convert(oid, Long.class) );
    	model.addAttribute("strain", strain);

    	List<StrainReference> strainreferences = servicestrainreference.findAll();
    	Collections.sort(strainreferences, new StrainReference.OrderByReferenceAsc());
    	model.addAttribute("StrainReferences", strainreferences);
    	
    	List<StrainUse> strainuses = servicestrainuse.findAll();
    	Collections.sort(strainuses, new StrainUse.OrderByUseAsc());
    	model.addAttribute("StrainUses", strainuses);
        
        model.addAttribute("editBool", true);

        return "strain";
    }
    
    /*
     * CRUD for Strain - UPDATE - Process
     */
    @RequestMapping(value = { "/edit-strain-{oid}" }, 
    		method = RequestMethod.POST)
    public String updateStrain(
        @Valid Strain strain, 
    	BindingResult result,
        ModelMap model, 
        @PathVariable String oid) {
 
        LOGGER.debug("Processing /edit-strain-oid - updateStrain()");

        model.addAttribute("loggedin", whoIsLoggedIn());

        if (result.hasErrors()) {
        	
            return "strain";
        }
 
        if(!servicestrain.isStrainNameUnique( strain.getOid(), strain.getStrain() ) ){
        	
            FieldError nameError = new FieldError("Strain","name", messageSource.getMessage("non.unique.name.Strain", new String[]{ strain.getStrain()}, Locale.getDefault()));
            
            result.addError(nameError);
            
            return "strain";
        }

        try {
		
        	servicestrain.update(strain);
		} 
        catch (ExceptionStrainNotFound e) {
			
        	e.printStackTrace();
		}

        model.addAttribute("message", " Strain " + strain.getStrain() + " "+ strain.getStrainLong() + " updated successfully");

        return "strainsuccess";
    }
    
    /*
     * CRUD for Strain - UPDATE - Process
     */
    @RequestMapping(value = { "/delete-strain-{oid}" }, 
    		method = RequestMethod.GET)
    public String deleteStrain(@PathVariable String oid, ModelMap model) {
    	
        LOGGER.debug("Processing /delete-strain-oid - deleteStrain()");

        model.addAttribute("loggedin", whoIsLoggedIn());

        Strain Strain = servicestrain.findByOid( ObjectConverter.convert(oid, Long.class) );

    	try {
			
    		servicestrain.delete( Strain );
		} 
    	catch (ExceptionStrainNotFound e) {
    		
			e.printStackTrace();
		}
        
        model.addAttribute("message", " Strain " + Strain.getStrain() + " "+ Strain.getStrainLong() + " updated successfully");

        return "strainsuccess";
    }


    /*
     * CRUD for StrainReference - CREATE - Input
     */
    @RequestMapping(value = { "/new-strainreference" }, 
    		method = RequestMethod.GET)
    public String newStrainReference(ModelMap model) {
        
        LOGGER.debug("Rendering /new-strainreference - newStrainReference()");

        model.addAttribute("loggedin", whoIsLoggedIn());

        StrainReference strainreference = new StrainReference();
    	model.addAttribute("strainreference", strainreference);
        
        model.addAttribute("editBool", false);
        
        return "strainreference";
    }
    
    /*
     * CRUD for StrainReference - CREATE - Process
     */
    @RequestMapping(value = { "/new-strainreference" }, 
    		method = RequestMethod.POST)
    public String saveStrainReference(StrainReference strainreference, 
    		BindingResult result,
            ModelMap model) {
 
        LOGGER.debug("Processing /new-strainreference - saveStrainReference()");

        model.addAttribute("loggedin", whoIsLoggedIn());

        if (result.hasErrors()) {
            
        	return "strainreference";
        }
 
        if(!servicestrainreference.isStrainReferenceReferenceUnique( strainreference.getOid(), strainreference.getReference() ) ){
        	
            FieldError nameError = new FieldError("strainreference", "strainreference", messageSource.getMessage("non.unique.name.strainreference", new String[]{ strainreference.getReference()}, Locale.getDefault()));
            
            result.addError(nameError);
            
            return "strainreference";
        }
         
        servicestrainreference.save(strainreference);
 
        model.addAttribute("message", " Strain Reference " + strainreference.getReference() + " "+ strainreference.getUrl() + " ADDED successfully");

        return "strainreferencesuccess";
    }
    
    /*
     * CRUD for StrainReference - READ 
     */
    @RequestMapping(value = { "/show-strainreference-{oid}" }, 
    		method = RequestMethod.GET)
    public String showStrainReference(@PathVariable String oid, ModelMap model) {
     
        LOGGER.debug("Rendering /show-strainreference-oid - showStrainReference()");

        model.addAttribute("loggedin", whoIsLoggedIn());

        StrainReference strainreference = servicestrainreference.findByOid( ObjectConverter.convert(oid, Long.class) );
    	model.addAttribute("strainreference", strainreference);

        return "strainreferenceshow";
    }
    
    /*
     * CRUD for StrainReference - LIST
     */
    @RequestMapping(value = { "/strain-references-list" }, 
    		method = RequestMethod.GET)
    public String listStrainReferences(ModelMap model) {
    	
        LOGGER.debug("Rendering /strain-references-list - listStrainReferences()");

        model.addAttribute("loggedin", whoIsLoggedIn());

    	if ( determinePrivilege() == 5 ) {

            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", true);
        }
        if (determinePrivilege() == 4 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("adminBool", true);
        }
        if ( determinePrivilege() == 3 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("editorBool", true);
        }
        if ( determinePrivilege() == 2 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("customerBool", true);
        }
        if ( determinePrivilege() == 1 ) {
        	
            model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("publicBool", true);
        }

        List<StrainReference> strainreferences = servicestrainreference.findAll();
    	Collections.sort(strainreferences, new StrainReference.OrderByReferenceAsc());
    	model.addAttribute("strainreferences", strainreferences);

        return "strainreferenceslist";
    }

    /*
     * CRUD for StrainReference - UPDATE - Input
     */
    @RequestMapping(value = { "/edit-strainreference-{oid}" }, 
    		method = RequestMethod.GET)
    public String editStrainReference(@PathVariable String oid, ModelMap model) {
     
        LOGGER.debug("Rendering /edit-strainreference-oid - editStrainReference()");

        model.addAttribute("loggedin", whoIsLoggedIn());

        StrainReference strainreference = servicestrainreference.findByOid( ObjectConverter.convert(oid, Long.class) );
    	model.addAttribute("strainreference", strainreference);

        model.addAttribute("editBool", true);

        return "strainreference";
    }
    
    /*
     * CRUD for StrainReference - UPDATE - Process
     */
    @RequestMapping(value = { "/edit-strainreference-{oid}" }, 
    		method = RequestMethod.POST)
    public String updateStrainReference(StrainReference strainreference, 
    		BindingResult result,
            ModelMap model, 
            @PathVariable String oid) {
 
        LOGGER.debug("Processing /edit-strainreference-oid - updateStrainReference()");

        model.addAttribute("loggedin", whoIsLoggedIn());

        if (result.hasErrors()) {
        	
            return "strainreference";
        }
 
        if(!servicestrainreference.isStrainReferenceReferenceUnique( strainreference.getOid(), strainreference.getReference() ) ){
        	
            FieldError nameError = new FieldError("strainreference", "strainreference", messageSource.getMessage("non.unique.name.strainreference", new String[]{ strainreference.getReference()}, Locale.getDefault()));
            
            result.addError(nameError);
            
            return "strainreference";
        }

        try {
		
        	servicestrainreference.update(strainreference);
		} 
        catch (ExceptionStrainReferenceNotFound e) {
			
        	e.printStackTrace();
		}

        model.addAttribute("message", " Strain Reference " + strainreference.getReference() + " "+ strainreference.getUrl() + " UPDATED successfully");

        return "strainreferencesuccess";
    }
    
    /*
     * CRUD for StrainReference - DELETE
     */
    @RequestMapping(value = { "/delete-strainreference-{oid}" }, 
    		method = RequestMethod.GET)
    public String deleteStrainReference(@PathVariable String oid, ModelMap model) {
    	
        LOGGER.debug("Processing /delete-strainreference-oid - deleteStrainReference()");

        model.addAttribute("loggedin", whoIsLoggedIn());

        StrainReference strainreference = servicestrainreference.findByOid( ObjectConverter.convert(oid, Long.class) );

    	try {
			
    		List<Strain> strains = servicestrain.findAllByStrainReference( strainreference );

        	Iterator<Strain> iteratorStrain = strains.iterator();
            
            while (iteratorStrain.hasNext()) {
        		
            	Strain strain = iteratorStrain.next();

            	strain.removeStrainReference(strainreference);
            	
            	servicestrain.save(strain);
            }

    		servicestrainreference.delete( strainreference );
		} 
    	catch (ExceptionStrainReferenceNotFound e) {
    		
			e.printStackTrace();
		}
        
        model.addAttribute("message", " Strain Reference " + strainreference.getReference() + " "+ strainreference.getUrl() + " DELETED successfully");

        return "strainreferencesuccess";
    }
     

    /*
     * CRUD for StrainUse - CREATE - Input 
     */
    @RequestMapping(value = { "/new-strainuse" }, 
    		method = RequestMethod.GET)
    public String newStrainUse(ModelMap model) {
        
        LOGGER.debug("Rendering /new-strainuse - newStrainUse()");

        model.addAttribute("loggedin", whoIsLoggedIn());

        StrainUse strainuse = new StrainUse();
    	model.addAttribute("strainuse", strainuse);
        
        model.addAttribute("editBool", false);
        
        return "strainuse";
    }
    
    /*
     * CRUD for StrainUse - CREATE - Process 
     */
    @RequestMapping(value = { "/new-strainuse" }, 
    		method = RequestMethod.POST)
    public String saveStrainUse(StrainUse strainuse, 
    		BindingResult result,
            ModelMap model) {
 
        LOGGER.debug("Processing /new-strainuse - saveStrainUse()");

        model.addAttribute("loggedin", whoIsLoggedIn());

        if (result.hasErrors()) {
            
        	return "strainuse";
        }
 
        if(!servicestrainuse.isStrainUseUseUnique( strainuse.getOid(), strainuse.getUse() ) ){
        	
            FieldError nameError = new FieldError("strainuse", "strainuse", messageSource.getMessage("non.unique.name.strainuse", new String[]{ strainuse.getUse()}, Locale.getDefault()));
            
            result.addError(nameError);
            
            return "strainuse";
        }
         
        servicestrainuse.save(strainuse);
 
        model.addAttribute("message", " Strain Use " + strainuse.getUse() + " "+ strainuse.getProtocol() + " ADDED successfully");

        return "strainusesuccess";
    }
    
    /*
     * CRUD for StrainUse - READ 
     */
    @RequestMapping(value = { "/show-strainuse-{oid}" }, 
    		method = RequestMethod.GET)
    public String showStrainUse(@PathVariable String oid, ModelMap model) {
     
        LOGGER.debug("Rendering /show-strainuse-oid - showStrainUse()");

        model.addAttribute("loggedin", whoIsLoggedIn());

        StrainUse strainuse = servicestrainuse.findByOid( ObjectConverter.convert(oid, Long.class) );
    	model.addAttribute("strainuse", strainuse);

        return "strainuseshow";
    }
    
    /*
     * CRUD for StrainUse - LIST
     */
    @RequestMapping(value = { "/strainuses-list" }, 
    		method = RequestMethod.GET)
    public String listStrainUses(ModelMap model) {
    	
        LOGGER.debug("Rendering /strain-uses-list - listStrainUses()");

        model.addAttribute("loggedin", whoIsLoggedIn());

    	if ( determinePrivilege() == 5 ) {

            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", true);
        }
        if (determinePrivilege() == 4 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("adminBool", true);
        }
        if ( determinePrivilege() == 3 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("editorBool", true);
        }
        if ( determinePrivilege() == 2 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("customerBool", true);
        }
        if ( determinePrivilege() == 1 ) {
        	
            model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("publicBool", true);
        }
        
    	List<StrainUse> strainuses = servicestrainuse.findAll();
    	Collections.sort(strainuses, new StrainUse.OrderByUseAsc());
    	model.addAttribute("strainuses", strainuses);

        return "strainuseslist";
    }

    /*
     * CRUD for StrainUse - UPDATE - Input 
     */
    @RequestMapping(value = { "/edit-strainuse-{oid}" }, 
    		method = RequestMethod.GET)
    public String editStrainUse(@PathVariable String oid, ModelMap model) {
     
        LOGGER.debug("Rendering /edit-strainuse-oid - editStrainUse()");

        model.addAttribute("loggedin", whoIsLoggedIn());

        StrainUse strainuse = servicestrainuse.findByOid( ObjectConverter.convert(oid, Long.class) );
    	model.addAttribute("strainuse", strainuse);

        model.addAttribute("new", false);
        model.addAttribute("editBool", true);

        return "strainuse";
    }
    
    /*
     * CRUD for StrainUse - UPDATE - Process 
     */
    @RequestMapping(value = { "/edit-strainuse-{oid}" }, 
    		method = RequestMethod.POST)
    public String updateStrainUse(StrainUse strainuse, 
    		BindingResult result,
            ModelMap model, 
            @PathVariable String oid) {
 
        LOGGER.debug("Processing /edit-strainuse-oid - updateStrainUse()");

        model.addAttribute("loggedin", whoIsLoggedIn());

        if (result.hasErrors()) {
        	
            return "strainuse";
        }
 
        if(!servicestrainuse.isStrainUseUseUnique( strainuse.getOid(), strainuse.getUse() ) ){
        	
            FieldError nameError = new FieldError("strainuse", "strainuse", messageSource.getMessage("non.unique.name.strainuse", new String[]{ strainuse.getUse()}, Locale.getDefault()));
            
            result.addError(nameError);
            
            return "strainuse";
        }

        try {
		
        	servicestrainuse.update(strainuse);
		} 
        catch (ExceptionStrainUseNotFound e) {
			
        	e.printStackTrace();
		}

        model.addAttribute("message", " Strain Use " + strainuse.getUse() + " "+ strainuse.getProtocol() + " UDPATED successfully");

        return "strainusesuccess";
    }
    
    /*
     * CRUD for StrainUse - DELETE 
     */
    @RequestMapping(value = { "/delete-strainuse-{oid}" }, 
    		method = RequestMethod.GET)
    public String deleteStrainUse(@PathVariable String oid, ModelMap model) {
    	
        LOGGER.debug("Processing /delete-strainuse-oid - deleteStrainUse()");

        model.addAttribute("loggedin", whoIsLoggedIn());

        StrainUse strainuse = servicestrainuse.findByOid( ObjectConverter.convert(oid, Long.class) );

    	try {
			
    		List<Strain> strains = servicestrain.findAllByStrainUse( strainuse );

        	Iterator<Strain> iteratorStrain = strains.iterator();
            
            while (iteratorStrain.hasNext()) {
        		
            	Strain strain = iteratorStrain.next();

            	strain.removeStrainUse(strainuse);
            	
            	servicestrain.save(strain);
            }

    		servicestrainuse.delete( strainuse );
		} 
    	catch (ExceptionStrainUseNotFound e) {
    		
			e.printStackTrace();
		}
        
        model.addAttribute("message", " Strain Use " + strainuse.getUse() + " "+ strainuse.getProtocol() + " DELETED successfully");

        return "strainusesuccess";
    }
     

    /*
     * CRUD for Order - SHOW - Input
     */
    @RequestMapping(value = { "/show-combined-order-{oid}" }, 
    		method = RequestMethod.GET)
    public String showCombinedOrder(ModelMap model, 
    		@PathVariable String oid) {
        
        LOGGER.debug("Rendering /show-combined-order - showCombinedOrder()");

        model.addAttribute("loggedin", whoIsLoggedIn());

        Order order = serviceorder.findByOid( ObjectConverter.convert(oid, Long.class) );

        int birdRequired = 0;
        int birdSupplied = 0;
        int birdAge = 0;
        BirdOrderLineSex birdOrderLineSex = null;
        BirdOrderLineDateFormat birdOrderLineDateFormat = null;
        Organism birdOrganism = null;

        Set<BirdOrderLine> BirdOrderLines = order.getBirdOrderLines();
        
        if ( BirdOrderLines.isEmpty() ) {
        	
        	model.addAttribute("birdBool", false);
        }
        else {

        	model.addAttribute("birdBool", true);

        	Iterator<BirdOrderLine> iteratorBirdOrderLines = BirdOrderLines.iterator();
            
            while (iteratorBirdOrderLines.hasNext()) {
        		
            	BirdOrderLine birdorderline = iteratorBirdOrderLines.next();

            	birdRequired = birdorderline.getRequired();
            	birdSupplied = birdorderline.getSupplied();
            	birdAge = birdorderline.getAge();
            	birdOrderLineSex = birdorderline.getBirdOrderLineSex();
            	birdOrderLineDateFormat = birdorderline.getBirdOrderLineDateFormat();
            	birdOrganism = birdorderline.getOrganism();
            }
        }

        int eggRequired = 0;
        int eggSupplied = 0;
        EggOrderLineFertilised eggOrderLineFertilised = null;
        Organism eggOrganism = null;

        Set<EggOrderLine> EggOrderLines = order.getEggOrderLines();
        
        if ( EggOrderLines.isEmpty() ) {
        	
        	model.addAttribute("eggBool", false);
        }
        else {

        	model.addAttribute("eggBool", true);

        	Iterator<EggOrderLine> iteratorEggOrderLines = EggOrderLines.iterator();
            
            while (iteratorEggOrderLines.hasNext()) {
        		
            	EggOrderLine eggorderline = iteratorEggOrderLines.next();

            	eggRequired = eggorderline.getRequired();
            	eggSupplied = eggorderline.getSupplied();
            	eggOrderLineFertilised = eggorderline.getEggOrderLineFertilised();
            	eggOrganism = eggorderline.getOrganism();
            }
        }
        
        int embryoRequired = 0;
        int embryoSupplied = 0;
        EmbryoOrderLineIncubation embryoOrderLineIncubation = null;
        Organism embryoOrganism = null;

        Set<EmbryoOrderLine> EmbryoOrderLines = order.getEmbryoOrderLines();
        
        if ( EmbryoOrderLines.isEmpty() ) {
        	
        	model.addAttribute("embryoBool", false);
        }
        else {

        	model.addAttribute("embryoBool", true);

        	Iterator<EmbryoOrderLine> iteratorEmbryoOrderLines = EmbryoOrderLines.iterator();
            
            while (iteratorEmbryoOrderLines.hasNext()) {
        		
            	EmbryoOrderLine embryoorderline = iteratorEmbryoOrderLines.next();

            	embryoRequired = embryoorderline.getRequired();
            	embryoSupplied = embryoorderline.getSupplied();
            	embryoOrderLineIncubation = embryoorderline.getEmbryoOrderLineIncubation();
            	embryoOrganism = embryoorderline.getOrganism();
            }

        }

        DTOOrder dtoorder = new DTOOrder();

		dtoorder.setOid( order.getOid() );

		dtoorder.setRequiredDate( order.getRequiredDate() );
		dtoorder.setSuppliedDate( order.getSuppliedDate() );
		dtoorder.setStudy( order.getStudy() );
		dtoorder.setCode( order.getCode() );
		dtoorder.setCustomerComment( order.getCustomerComment() );
		dtoorder.setSupplierComment( order.getSupplierComment() );
		dtoorder.setCustomer( order.getCustomer() );
		dtoorder.setEditor( order.getEditor() );
		dtoorder.setSupplier( order.getSupplier() );

        if ( dtoorder.getCustomer() != (long) 0 ) {
    		
    		User customer = userService.getUserByID( dtoorder.getCustomer() );
    		dtoorder.setCustomerEmail( customer.getEmail() );
    		dtoorder.setCustomerOrganisation( customer.getOrganisation() );
    	}

    	if ( dtoorder.getEditor() != (long) 0 ) {
    		
    		User editor = userService.getUserByID( dtoorder.getEditor() );
    		dtoorder.setEditorEmail( editor.getEmail() );
    	}

    	if ( dtoorder.getSupplier() != (long) 0 ) {
    		
    		User supplier = userService.getUserByID( dtoorder.getSupplier() );
    		dtoorder.setSupplierEmail( supplier.getEmail() );
    	}

    	dtoorder.setOrderStatus( order.getOrderStatus() );
		dtoorder.setOrderCollection( order.getOrderCollection() );
		dtoorder.setOrderType( order.getOrderType() );

		dtoorder.setBirdRequired( birdRequired );
		dtoorder.setBirdSupplied( birdSupplied );
		dtoorder.setBirdAge( birdAge );
		dtoorder.setBirdOrderLineSex( birdOrderLineSex );
		dtoorder.setBirdOrderLineDateFormat( birdOrderLineDateFormat );
		dtoorder.setBirdOrganism( birdOrganism );

		dtoorder.setEggRequired( eggRequired );
		dtoorder.setEggSupplied (eggSupplied );
		dtoorder.setEggOrderLineFertilised( eggOrderLineFertilised );
		dtoorder.setEggOrganism( eggOrganism );

		dtoorder.setEmbryoRequired( embryoRequired );
		dtoorder.setEmbryoSupplied( embryoSupplied );
		dtoorder.setEmbryoOrderLineIncubation( embryoOrderLineIncubation );
		dtoorder.setEmbryoOrganism( embryoOrganism );

    	if ( determinePrivilege() == 5 ) {

        	model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", true);
        }
        if (determinePrivilege() == 4 ) {
        	
        	model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", true);
        	model.addAttribute("superBool", false);
        }
        if ( determinePrivilege() == 3 ) {
        	
        	model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", true);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        }
        if ( determinePrivilege() == 2 ) {
        	
        	model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", true);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        }
        if ( determinePrivilege() == 1 ) {
        	
        	model.addAttribute("publicBool", true);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        }

    	model.addAttribute("order", dtoorder);

        return "combinedordershow";
    }

    
    /*
     * CRUD for Order EGGS - CREATE - Input
     */
    @RequestMapping(value = { "/new-combined-egg-order" }, 
    		method = RequestMethod.GET)
    public String newCombinedEggOrder(ModelMap model,
            final HttpServletRequest request) {
        
        LOGGER.debug("Rendering /new-combined-egg-order - newCombinedEggOrder()");

        model.addAttribute("loggedin", whoIsLoggedIn());

        DTOOrder dtoorder = new DTOOrder();
        
        List<User> customers = new ArrayList<User>();
        List<User> editors = new ArrayList<User>();
        List<User> suppliers = new ArrayList<User>();

    	if ( determinePrivilege() == 5 ) {

        	model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", true);

        	customers.add( getNullUser() );
        	customers.addAll( listByPrivilege(2) );
        	
        	editors.add( getNullUser() );
        	editors.addAll( listByPrivilege(4) );
        	editors.addAll( listByPrivilege(3) );
        	
        	suppliers.add( getNullUser() );
        	suppliers.addAll( listByPrivilege(4) );
        	suppliers.addAll( listByPrivilege(3) );
        }
        if (determinePrivilege() == 4 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("adminBool", true);
        	
        	customers.add( getNullUser() );
        	customers.addAll( listByPrivilege(2) );
        	
        	editors.add( getNullUser() );
        	editors.addAll( listByPrivilege(4) );
        	editors.addAll( listByPrivilege(3) );
        	
        	suppliers.add( getNullUser() );
        	suppliers.addAll( listByPrivilege(4) );
        	suppliers.addAll( listByPrivilege(3) );
        }
        if ( determinePrivilege() == 3 ) {
        	
            dtoorder.setEditor( whoIsLoggedInOid() );

            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("editorBool", true);
        	
        	editors.add( getNullUser() );
        	editors.addAll( listByPrivilege(3) );
        	
        	suppliers.add( getNullUser() );
        	suppliers.addAll( listByPrivilege(3) );
        }
        if ( determinePrivilege() == 2 ) {
        	
            dtoorder.setCustomer( whoIsLoggedInOid() );

            model.addAttribute("publicBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("customerBool", true);
        }
        if ( determinePrivilege() == 1 ) {
        	
            model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("publicBool", true);
        }

    	model.addAttribute("customers", customers);
    	model.addAttribute("editors", editors);
    	model.addAttribute("suppliers", suppliers);
         
        OrderStatus orderStatus = serviceorderstatus.findByName(EnumOrderStatus.NEW.getEnumOrderStatus());
        OrderCollection orderCollection = serviceordercollection.findByName(EnumOrderCollection.UNKNOWN.getEnumOrderCollection());
        OrderType orderType = serviceordertype.findByName(EnumOrderType.EGG.getEnumOrderType());
        
        dtoorder.setOrderStatus(orderStatus);
        dtoorder.setOrderCollection(orderCollection);
        dtoorder.setOrderType(orderType);
        
        dtoorder.setRequiredDate(new Date());

    	model.addAttribute("dtoorder", dtoorder);
        
    	List<Organism> eggorganisms = serviceorganism.findAll();
    	model.addAttribute("eggorganisms", eggorganisms);

    	List<EggOrderLineFertilised> eggorderlineFertiliseds = serviceeggorderlinefertilised.findAll();
    	Collections.sort(eggorderlineFertiliseds, new EggOrderLineFertilised.OrderByOidAsc());
    	model.addAttribute("fertiliseds", eggorderlineFertiliseds);

        model.addAttribute("editBool", false);

    	model.addAttribute("eggBool", true);
    	model.addAttribute("birdBool", false);
    	model.addAttribute("embryoBool", false);

    	model.addAttribute("contextPath", request.getContextPath());

        return "combinedorder";
    }
    
    /*
     * CRUD for Order EGGS - CREATE - Process
     */
    @RequestMapping(value = { "/new-combined-egg-order" }, 
    		method = RequestMethod.POST)
    public String saveCombinedEggOrder(@Valid DTOOrder dtoOrder, 
    		BindingResult result,
            ModelMap model,
            final HttpServletRequest request) {
 
        LOGGER.debug("Processing /new-combined-egg-order - saveCombinedEggOrder()");

        if (result.hasErrors()) {
            
        	return "order";
        }
        
        model.addAttribute("loggedin", whoIsLoggedIn());

    	if ( determinePrivilege() == 5 ) {

        	model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", true);
        }
        if (determinePrivilege() == 4 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("adminBool", true);
        }
        if ( determinePrivilege() == 3 ) {

        	model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("editorBool", true);
        }
        if ( determinePrivilege() == 2 ) {

            model.addAttribute("publicBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("customerBool", true);
        }
        if ( determinePrivilege() == 1 ) {
        	
            model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("publicBool", true);
        }

        Set<EggOrderLine> EggOrderLines = new HashSet<EggOrderLine>();

        EggOrderLine EggOrderLine = new EggOrderLine();
        
        EggOrderLine.setRequired(dtoOrder.getEggRequired());
        EggOrderLine.setSupplied(0);
        EggOrderLine.setEggOrderLineFertilised(dtoOrder.getEggOrderLineFertilised());
        EggOrderLine.setOrganism(dtoOrder.getEggOrganism());
        
        EggOrderLine savedEggOrderLine = serviceeggorderline.create(EggOrderLine);

        EggOrderLines.add(savedEggOrderLine);
        
        Order order = new Order();
        
        order.setRequiredDate(dtoOrder.getRequiredDate());
        order.setSuppliedDate( null );
        order.setStudy(dtoOrder.getStudy());
        order.setCode(dtoOrder.getCode());
        order.setCustomerComment(dtoOrder.getCustomerComment());
        order.setSupplierComment("");
        order.setBirdOrderLines(null);
        order.setEggOrderLines(EggOrderLines);
        order.setEmbryoOrderLines(null);
        order.setOrderStatus(dtoOrder.getOrderStatus());
        order.setOrderCollection(dtoOrder.getOrderCollection());
        order.setOrderType(dtoOrder.getOrderType());
        
        String customerEmail = "";
        String editorEmail = "";
        String supplierEmail = "";
        
        if ( dtoOrder.getCustomer() == null ) {

        	User customer = getNullUser();
    		order.setCustomer( customer.getId() );
    		customerEmail = customer.getEmail();
        }
        else {
        	
            if ( dtoOrder.getCustomer() == (long) 0 ) {
            
            	User customer = getNullUser();
        		order.setCustomer( customer.getId() );
        		customerEmail = customer.getEmail();
            }
            else {
        		
        		User customer = userService.getUserByID( dtoOrder.getCustomer() );
        		order.setCustomer( customer.getId() );
        		customerEmail = customer.getEmail();
        	}
        }

        if ( dtoOrder.getEditor() == null ) {

        	User editor = getNullUser();
    		order.setEditor( editor.getId() );
    		editorEmail = editor.getEmail();
        }
        else {
        	
            if ( dtoOrder.getEditor() == (long) 0 ) {
        		
            	User editor = getNullUser();
        		order.setEditor( editor.getId() );
        		editorEmail = editor.getEmail();
        	}
            else {
            	
        		User editor = userService.getUserByID( dtoOrder.getEditor() );
        		order.setEditor( editor.getId() );
        		editorEmail = editor.getEmail();
            }
        }

        if ( dtoOrder.getSupplier() == null ) {

        	User supplier = getNullUser();
    		order.setSupplier( supplier.getId() );
    		supplierEmail = supplier.getEmail();
        }
        else {
        	
            if ( dtoOrder.getSupplier() == (long) 0 ) {
            
            	User supplier = getNullUser();
        		order.setSupplier( supplier.getId() );
        		supplierEmail = supplier.getEmail();
            }
            else {
        		
        		User supplier = userService.getUserByID( dtoOrder.getSupplier() );
        		order.setSupplier( supplier.getId() );
        		supplierEmail = supplier.getEmail();
        	}
        }
        
        Order savedOrder = serviceorder.create(order);
        
        if (dataSettings.isEmailTrue()) {
        	
            mailSender.send( constructEmail("NEW EGG ORDER", savedOrder.formatNewEmail(customerEmail) + "\n\n" +getShowUrl(request, savedOrder.getOidAsString())) );
        }

        model.addAttribute("message", "Order " + savedOrder.getOid() + " ADDED successfully");

    	model.addAttribute("contextPath", request.getContextPath());

        return "combinedordersuccess";
    }
    
    /*
     * CRUD for Order BIRDS - CREATE - Input
     */
    @RequestMapping(value = { "/new-combined-bird-order" }, 
    		method = RequestMethod.GET)
    public String newCombinedBirdOrder(ModelMap model,
            final HttpServletRequest request) {
        
        LOGGER.debug("Rendering /new-combined-bird-order - newCombinedBirdOrder()");

        model.addAttribute("loggedin", whoIsLoggedIn());

        DTOOrder dtoorder = new DTOOrder();
        
        List<User> customers = new ArrayList<User>();
        List<User> editors = new ArrayList<User>();
        List<User> suppliers = new ArrayList<User>();

    	if ( determinePrivilege() == 5 ) {

        	model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", true);

        	customers.add( getNullUser() );
        	customers.addAll( listByPrivilege(2) );
        	
        	editors.add( getNullUser() );
        	editors.addAll( listByPrivilege(4) );
        	editors.addAll( listByPrivilege(3) );
        	
        	suppliers.add( getNullUser() );
        	suppliers.addAll( listByPrivilege(4) );
        	suppliers.addAll( listByPrivilege(3) );
        }
        if (determinePrivilege() == 4 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("adminBool", true);
        	
        	customers.add( getNullUser() );
        	customers.addAll( listByPrivilege(2) );
        	
        	editors.add( getNullUser() );
        	editors.addAll( listByPrivilege(4) );
        	editors.addAll( listByPrivilege(3) );
        	
        	suppliers.add( getNullUser() );
        	suppliers.addAll( listByPrivilege(4) );
        	suppliers.addAll( listByPrivilege(3) );
        }
        if ( determinePrivilege() == 3 ) {
        	
            dtoorder.setEditor( whoIsLoggedInOid() );

            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("editorBool", true);
        	
        	editors.add( getNullUser() );
        	editors.addAll( listByPrivilege(3) );
        	
        	suppliers.add( getNullUser() );
        	suppliers.addAll( listByPrivilege(3) );
        }
        if ( determinePrivilege() == 2 ) {
        	
            dtoorder.setCustomer( whoIsLoggedInOid() );

            model.addAttribute("publicBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("customerBool", true);
        }
        if ( determinePrivilege() == 1 ) {
        	
            model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("publicBool", true);
        }

    	model.addAttribute("customers", customers);
    	model.addAttribute("editors", editors);
    	model.addAttribute("suppliers", suppliers);
        
        OrderStatus orderStatus = serviceorderstatus.findByName(EnumOrderStatus.NEW.getEnumOrderStatus());
        OrderCollection orderCollection = serviceordercollection.findByName(EnumOrderCollection.UNKNOWN.getEnumOrderCollection());
        OrderType orderType = serviceordertype.findByName(EnumOrderType.BIRD.getEnumOrderType());
        
        dtoorder.setOrderStatus(orderStatus);
        dtoorder.setOrderCollection(orderCollection);
        dtoorder.setOrderType(orderType);
        
        dtoorder.setRequiredDate(new Date());

    	model.addAttribute("dtoorder", dtoorder);
        
    	List<Organism> birdorganisms = serviceorganism.findAll();
    	model.addAttribute("birdorganisms", birdorganisms);

    	List<BirdOrderLineSex> birdorderlineSexs = servicebirdorderlinesex.findAll();
    	Collections.sort(birdorderlineSexs, new BirdOrderLineSex.OrderByOidAsc());
    	model.addAttribute("sexs", birdorderlineSexs);

    	List<BirdOrderLineDateFormat> birdorderlineDateFormats = servicebirdorderlinedateformat.findAll();
    	Collections.sort(birdorderlineDateFormats, new BirdOrderLineDateFormat.OrderByOidAsc());
    	model.addAttribute("dateformats", birdorderlineDateFormats);

        model.addAttribute("editBool", false);
        
    	model.addAttribute("eggBool", false);
    	model.addAttribute("birdBool", true);
    	model.addAttribute("embryoBool", false);

    	model.addAttribute("contextPath", request.getContextPath());

        return "combinedorder";
    }
    
    /*
     * CRUD for Order BIRDS - CREATE - Process
     */
    @RequestMapping(value = { "/new-combined-bird-order" }, 
    		method = RequestMethod.POST)
    public String saveCombinedBirdOrder(@Valid DTOOrder dtoOrder, 
    		BindingResult result,
            ModelMap model,
            final HttpServletRequest request) {
 
        LOGGER.debug("Processing /new-combined-bird-order - saveCombinedBirdOrder()");

        model.addAttribute("loggedin", whoIsLoggedIn());
        
        if (result.hasErrors()) {
            
        	return "combinedorder";
        }
        
        if ( determinePrivilege() == 5 ) {

        	model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", true);
        }
        if (determinePrivilege() == 4 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("adminBool", true);
        }
        if ( determinePrivilege() == 3 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("editorBool", true);
        }
        if ( determinePrivilege() == 2 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("customerBool", true);
        }
        if ( determinePrivilege() == 1 ) {
        	
            model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("publicBool", true);
        }

        Set<BirdOrderLine> BirdOrderLines = new HashSet<BirdOrderLine>();

        BirdOrderLine BirdOrderLine = new BirdOrderLine();
        
        BirdOrderLine.setRequired(dtoOrder.getBirdRequired());
        BirdOrderLine.setSupplied(0);
        BirdOrderLine.setBirdOrderLineSex(dtoOrder.getBirdOrderLineSex());
        BirdOrderLine.setAge(dtoOrder.getBirdAge());
        BirdOrderLine.setBirdOrderLineDateFormat(dtoOrder.getBirdOrderLineDateFormat());
        BirdOrderLine.setOrganism(dtoOrder.getBirdOrganism());

        BirdOrderLine savedBirdOrderLine = servicebirdorderline.create(BirdOrderLine);
        
        BirdOrderLines.add(savedBirdOrderLine);
        
        Order order = new Order();
        
        order.setRequiredDate(dtoOrder.getRequiredDate());
        order.setSuppliedDate( null );
        order.setStudy(dtoOrder.getStudy());
        order.setCode(dtoOrder.getCode());
        order.setCustomerComment(dtoOrder.getCustomerComment());
        order.setSupplierComment("");
        order.setBirdOrderLines(BirdOrderLines);
        order.setEggOrderLines(null);
        order.setEmbryoOrderLines(null);
        order.setOrderStatus(dtoOrder.getOrderStatus());
        order.setOrderCollection(dtoOrder.getOrderCollection());
        order.setOrderType(dtoOrder.getOrderType());
        
        String customerEmail = "";
        String editorEmail = "";
        String supplierEmail = "";
        
        if ( dtoOrder.getCustomer() == null ) {

        	User customer = getNullUser();
    		order.setCustomer( customer.getId() );
    		customerEmail = customer.getEmail();
        }
        else {
        	
            if ( dtoOrder.getCustomer() == (long) 0 ) {
            
            	User customer = getNullUser();
        		order.setCustomer( customer.getId() );
        		customerEmail = customer.getEmail();
            }
            else {
        		
        		User customer = userService.getUserByID( dtoOrder.getCustomer() );
        		order.setCustomer( customer.getId() );
        		customerEmail = customer.getEmail();
        	}
        }

        if ( dtoOrder.getEditor() == null ) {

        	User editor = getNullUser();
    		order.setEditor( editor.getId() );
    		editorEmail = editor.getEmail();
        }
        else {
        	
            if ( dtoOrder.getEditor() == (long) 0 ) {
        		
            	User editor = getNullUser();
        		order.setEditor( editor.getId() );
        		editorEmail = editor.getEmail();
        	}
            else {
            	
        		User editor = userService.getUserByID( dtoOrder.getEditor() );
        		order.setEditor( editor.getId() );
        		editorEmail = editor.getEmail();
            }
        }

        if ( dtoOrder.getSupplier() == null ) {

        	User supplier = getNullUser();
    		order.setSupplier( supplier.getId() );
    		supplierEmail = supplier.getEmail();
        }
        else {
        	
            if ( dtoOrder.getSupplier() == (long) 0 ) {
            
            	User supplier = getNullUser();
        		order.setSupplier( supplier.getId() );
        		supplierEmail = supplier.getEmail();
            }
            else {
        		
        		User supplier = userService.getUserByID( dtoOrder.getSupplier() );
        		order.setSupplier( supplier.getId() );
        		supplierEmail = supplier.getEmail();
        	}
        }

        Order savedOrder = serviceorder.create(order);
        
        if (dataSettings.isEmailTrue()) {
        	
            mailSender.send( constructEmail("NEW BIRD ORDER", savedOrder.formatNewEmail(customerEmail) + "\n\n" +getShowUrl(request, savedOrder.getOidAsString())) );
        }

        model.addAttribute("message", "Order " + savedOrder.getOid() + " ADDED successfully");

    	model.addAttribute("contextPath", request.getContextPath());

        return "combinedordersuccess";
    }
    
    /*
     * CRUD for Order EMBRYOS - CREATE - Input
     */
    @RequestMapping(value = { "/new-combined-embryo-order" }, 
    		method = RequestMethod.GET)
    public String newCombinedEmbryoOrder(ModelMap model,
            final HttpServletRequest request) {
        
        LOGGER.debug("Rendering /new-combined-embryo-order - newC	Order()");

        model.addAttribute("loggedin", whoIsLoggedIn());

        DTOOrder dtoorder = new DTOOrder();
        
        List<User> customers = new ArrayList<User>();
        List<User> editors = new ArrayList<User>();
        List<User> suppliers = new ArrayList<User>();

    	if ( determinePrivilege() == 5 ) {

        	model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", true);

        	customers.add( getNullUser() );
        	customers.addAll( listByPrivilege(2) );
        	
        	editors.add( getNullUser() );
        	editors.addAll( listByPrivilege(4) );
        	editors.addAll( listByPrivilege(3) );
        	
        	suppliers.add( getNullUser() );
        	suppliers.addAll( listByPrivilege(4) );
        	suppliers.addAll( listByPrivilege(3) );
        }
        if (determinePrivilege() == 4 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("adminBool", true);
        	
        	customers.add( getNullUser() );
        	customers.addAll( listByPrivilege(2) );
        	
        	editors.add( getNullUser() );
        	editors.addAll( listByPrivilege(4) );
        	editors.addAll( listByPrivilege(3) );
        	
        	suppliers.add( getNullUser() );
        	suppliers.addAll( listByPrivilege(4) );
        	suppliers.addAll( listByPrivilege(3) );
        }
        if ( determinePrivilege() == 3 ) {
        	
            dtoorder.setEditor( whoIsLoggedInOid() );

            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("editorBool", true);
        	
        	editors.add( getNullUser() );
        	editors.addAll( listByPrivilege(3) );
        	
        	suppliers.add( getNullUser() );
        	suppliers.addAll( listByPrivilege(3) );
        }
        if ( determinePrivilege() == 2 ) {
        	
            dtoorder.setCustomer( whoIsLoggedInOid() );

            model.addAttribute("publicBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("customerBool", true);
        }
        if ( determinePrivilege() == 1 ) {
        	
            model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("publicBool", true);
        }

    	model.addAttribute("customers", customers);
    	model.addAttribute("editors", editors);
    	model.addAttribute("suppliers", suppliers);
        
        OrderStatus orderStatus = serviceorderstatus.findByName(EnumOrderStatus.NEW.getEnumOrderStatus());
        OrderCollection orderCollection = serviceordercollection.findByName(EnumOrderCollection.UNKNOWN.getEnumOrderCollection());
        OrderType orderType = serviceordertype.findByName(EnumOrderType.EMBRYO.getEnumOrderType());
        
        dtoorder.setOrderStatus(orderStatus);
        dtoorder.setOrderCollection(orderCollection);
        dtoorder.setOrderType(orderType);
        
        dtoorder.setRequiredDate(new Date());

    	model.addAttribute("dtoorder", dtoorder);
        
    	List<Organism> embryoorganisms = serviceorganism.findAll();
    	model.addAttribute("embryoorganisms", embryoorganisms);

    	List<EmbryoOrderLineIncubation> embryoorderlineIncubations = serviceembryoorderlineincubation.findAll();
    	Collections.sort(embryoorderlineIncubations, new EmbryoOrderLineIncubation.OrderByOidAsc());
    	model.addAttribute("incubateds", embryoorderlineIncubations);

        model.addAttribute("editBool", false);
        
    	model.addAttribute("eggBool", false);
    	model.addAttribute("birdBool", false);
    	model.addAttribute("embryoBool", true);

    	model.addAttribute("contextPath", request.getContextPath());

        return "combinedorder";
    }
    
    /*
     * CRUD for Order EMBRYOS - CREATE - Process
     */
    @RequestMapping(value = { "/new-combined-embryo-order" }, 
    		method = RequestMethod.POST)
    public String saveCombinedEmbryoOrder(@Valid DTOOrder dtoOrder, 
    		BindingResult result,
            ModelMap model,
            final HttpServletRequest request) {
 
        LOGGER.debug("Processing /new-combined-embryo-order - saveCombinedEmbryoOrder()");

        model.addAttribute("loggedin", whoIsLoggedIn());

        if (result.hasErrors()) {
            
        	return "combinedorder";
        }
        
        Set<EmbryoOrderLine> EmbryoOrderLines = new HashSet<EmbryoOrderLine>();

        EmbryoOrderLine EmbryoOrderLine = new EmbryoOrderLine();
        
        EmbryoOrderLine.setRequired(dtoOrder.getEmbryoRequired());
        EmbryoOrderLine.setSupplied(0);
        EmbryoOrderLine.setEmbryoOrderLineIncubation(dtoOrder.getEmbryoOrderLineIncubation());
        EmbryoOrderLine.setOrganism(dtoOrder.getEmbryoOrganism());

        EmbryoOrderLine savedEmbryoOrderLine = serviceembryoorderline.create(EmbryoOrderLine);

        EmbryoOrderLines.add(savedEmbryoOrderLine);
        
        Order order = new Order();
        
        order.setRequiredDate(dtoOrder.getRequiredDate());
        order.setSuppliedDate( null );
        order.setStudy(dtoOrder.getStudy());
        order.setCode(dtoOrder.getCode());
        order.setCustomerComment(dtoOrder.getCustomerComment());
        order.setSupplierComment("");
        order.setBirdOrderLines(null);
        order.setEggOrderLines(null);
        order.setEmbryoOrderLines(EmbryoOrderLines);
        order.setOrderStatus(dtoOrder.getOrderStatus());
        order.setOrderCollection(dtoOrder.getOrderCollection());
        order.setOrderType(dtoOrder.getOrderType());

        String customerEmail = "";
        String editorEmail = "";
        String supplierEmail = "";
        
        if ( dtoOrder.getCustomer() == null ) {

        	User customer = getNullUser();
    		order.setCustomer( customer.getId() );
    		customerEmail = customer.getEmail();
        }
        else {
        	
            if ( dtoOrder.getCustomer() == (long) 0 ) {
            
            	User customer = getNullUser();
        		order.setCustomer( customer.getId() );
        		customerEmail = customer.getEmail();
            }
            else {
        		
        		User customer = userService.getUserByID( dtoOrder.getCustomer() );
        		order.setCustomer( customer.getId() );
        		customerEmail = customer.getEmail();
        	}
        }

        if ( dtoOrder.getEditor() == null ) {

        	User editor = getNullUser();
    		order.setEditor( editor.getId() );
    		editorEmail = editor.getEmail();
        }
        else {
        	
            if ( dtoOrder.getEditor() == (long) 0 ) {
        		
            	User editor = getNullUser();
        		order.setEditor( editor.getId() );
        		editorEmail = editor.getEmail();
        	}
            else {
            	
        		User editor = userService.getUserByID( dtoOrder.getEditor() );
        		order.setEditor( editor.getId() );
        		editorEmail = editor.getEmail();
            }
        }

        if ( dtoOrder.getSupplier() == null ) {

        	User supplier = getNullUser();
    		order.setSupplier( supplier.getId() );
    		supplierEmail = supplier.getEmail();
        }
        else {
        	
            if ( dtoOrder.getSupplier() == (long) 0 ) {
            
            	User supplier = getNullUser();
        		order.setSupplier( supplier.getId() );
        		supplierEmail = supplier.getEmail();
            }
            else {
        		
        		User supplier = userService.getUserByID( dtoOrder.getSupplier() );
        		order.setSupplier( supplier.getId() );
        		supplierEmail = supplier.getEmail();
        	}
        }

        Order savedOrder = serviceorder.create(order);
        
        if (dataSettings.isEmailTrue()) {
        	
            mailSender.send( constructEmail("NEW EMBRYO ORDER", savedOrder.formatNewEmail(customerEmail) + "\n\n" +getShowUrl(request, savedOrder.getOidAsString())) );
        }

        if ( determinePrivilege() == 5 ) {

        	model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", true);
        }
        if (determinePrivilege() == 4 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("adminBool", true);
        }
        if ( determinePrivilege() == 3 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("editorBool", true);
        }
        if ( determinePrivilege() == 2 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("customerBool", true);
        }
        if ( determinePrivilege() == 1 ) {
        	
            model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("publicBool", true);
        }

        model.addAttribute("message", "Order " + savedOrder.getOid() + " ADDED successfully");

    	model.addAttribute("contextPath", request.getContextPath());

        return "combinedordersuccess";
    }
    
    /*
     * CRUD for Order EGGS, BIRDS or EMBRYOS - CREATE - Input
     */
    @RequestMapping(value = { "/new-selection-order" }, 
    		method = RequestMethod.GET)
    public String newSelectionOrder(ModelMap model,
            final HttpServletRequest request) {
        
        LOGGER.debug("Rendering /new-selection-order - newSelectionOrder()");

        model.addAttribute("loggedin", whoIsLoggedIn());

        DTOOrder dtoorder = new DTOOrder();
        
        List<User> customers = new ArrayList<User>();
        List<User> editors = new ArrayList<User>();
        List<User> suppliers = new ArrayList<User>();

    	if ( determinePrivilege() == 5 ) {

        	model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", true);

        	customers.add( getNullUser() );
        	customers.addAll( listByPrivilege(2) );
        	
        	editors.add( getNullUser() );
        	editors.addAll( listByPrivilege(4) );
        	editors.addAll( listByPrivilege(3) );
        	
        	suppliers.add( getNullUser() );
        	suppliers.addAll( listByPrivilege(4) );
        	suppliers.addAll( listByPrivilege(3) );
        }
        if (determinePrivilege() == 4 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("adminBool", true);
        	
        	customers.add( getNullUser() );
        	customers.addAll( listByPrivilege(2) );
        	
        	editors.add( getNullUser() );
        	editors.addAll( listByPrivilege(4) );
        	editors.addAll( listByPrivilege(3) );
        	
        	suppliers.add( getNullUser() );
        	suppliers.addAll( listByPrivilege(4) );
        	suppliers.addAll( listByPrivilege(3) );
        }
        if ( determinePrivilege() == 3 ) {
        	
            dtoorder.setEditor( whoIsLoggedInOid() );

            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("editorBool", true);
        	
        	editors.add( getNullUser() );
        	editors.addAll( listByPrivilege(3) );
        	
        	suppliers.add( getNullUser() );
        	suppliers.addAll( listByPrivilege(3) );
        }
        if ( determinePrivilege() == 2 ) {
        	
            dtoorder.setCustomer( whoIsLoggedInOid() );

            model.addAttribute("publicBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("customerBool", true);
        }
        if ( determinePrivilege() == 1 ) {
        	
            model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("publicBool", true);
        }

        List<OrderType> orderTypes = serviceordertype.findAll();
    	model.addAttribute("ordertypes", orderTypes);
        
        OrderStatus orderStatus = serviceorderstatus.findByName(EnumOrderStatus.NEW.getEnumOrderStatus());
        OrderCollection orderCollection = serviceordercollection.findByName(EnumOrderCollection.UNKNOWN.getEnumOrderCollection());

        dtoorder.setOrderStatus(orderStatus);
        dtoorder.setOrderCollection(orderCollection);
        
        dtoorder.setRequiredDate(new Date());
        
    	List<Organism> embryoorganisms = serviceorganism.findAll();
    	model.addAttribute("embryoorganisms", embryoorganisms);

    	List<EmbryoOrderLineIncubation> embryoorderlineIncubations = serviceembryoorderlineincubation.findAll();
    	Collections.sort(embryoorderlineIncubations, new EmbryoOrderLineIncubation.OrderByOidAsc());
    	model.addAttribute("incubateds", embryoorderlineIncubations);

    	List<Organism> birdorganisms = serviceorganism.findAll();
    	model.addAttribute("birdorganisms", birdorganisms);

    	List<BirdOrderLineSex> birdorderlineSexs = servicebirdorderlinesex.findAll();
    	Collections.sort(birdorderlineSexs, new BirdOrderLineSex.OrderByOidAsc());
    	model.addAttribute("sexs", birdorderlineSexs);

    	List<BirdOrderLineDateFormat> birdorderlineDateFormats = servicebirdorderlinedateformat.findAll();
    	Collections.sort(birdorderlineDateFormats, new BirdOrderLineDateFormat.OrderByOidAsc());
    	model.addAttribute("dateformats", birdorderlineDateFormats);

    	List<Organism> eggorganisms = serviceorganism.findAll();
    	model.addAttribute("eggorganisms", eggorganisms);

    	List<EggOrderLineFertilised> eggorderlineFertiliseds = serviceeggorderlinefertilised.findAll();
    	Collections.sort(eggorderlineFertiliseds, new EggOrderLineFertilised.OrderByOidAsc());
    	model.addAttribute("fertiliseds", eggorderlineFertiliseds);

    	model.addAttribute("dtoorder", dtoorder);

    	model.addAttribute("contextPath", request.getContextPath());

        return "selectionorder";
    }
    
    
    /*
     * CRUD for Order EGGS, BIRDS or EMBRYOa - CREATE - Process
     */
    @RequestMapping(value = { "/new-selection-order" }, 
    		method = RequestMethod.POST)
    public String saveSelectionOrder(@Valid DTOOrder dtoOrder, 
    		BindingResult result,
            ModelMap model,
            final HttpServletRequest request) {
 
        LOGGER.debug("Processing /new-selection-order - saveSelectionOrder()");

        if (result.hasErrors()) {
            
        	return "order";
        }
        
        model.addAttribute("loggedin", whoIsLoggedIn());

    	if ( determinePrivilege() == 5 ) {

        	model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", true);
        }
        if (determinePrivilege() == 4 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("adminBool", true);
        }
        if ( determinePrivilege() == 3 ) {

        	model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("editorBool", true);
        }
        if ( determinePrivilege() == 2 ) {

            model.addAttribute("publicBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("customerBool", true);
        }
        if ( determinePrivilege() == 1 ) {
        	
            model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("publicBool", true);
        }

        Set<BirdOrderLine> birdOrderLines = new HashSet<BirdOrderLine>();
   	    Set<EggOrderLine> eggOrderLines = new HashSet<EggOrderLine>();
        Set<EmbryoOrderLine> embryoOrderLines = new HashSet<EmbryoOrderLine>();

        if ( dtoOrder.getOrderType().isBird() ) {

    	    BirdOrderLine birdOrderLine = new BirdOrderLine();
        
        	birdOrderLine.setRequired(dtoOrder.getBirdRequired());
	        birdOrderLine.setSupplied(0);
    	    birdOrderLine.setBirdOrderLineSex(dtoOrder.getBirdOrderLineSex());
        	birdOrderLine.setAge(dtoOrder.getBirdAge());
	        birdOrderLine.setBirdOrderLineDateFormat(dtoOrder.getBirdOrderLineDateFormat());
    	    birdOrderLine.setOrganism(dtoOrder.getBirdOrganism());

        	BirdOrderLine savedBirdOrderLine = servicebirdorderline.create( birdOrderLine );
        
	        birdOrderLines.add(savedBirdOrderLine);
        }

        if ( dtoOrder.getOrderType().isEgg() ) {

	        EggOrderLine eggOrderLine = new EggOrderLine();
        
        	eggOrderLine.setRequired(dtoOrder.getEggRequired());
    	    eggOrderLine.setSupplied(0);
	        eggOrderLine.setEggOrderLineFertilised(dtoOrder.getEggOrderLineFertilised());
        	eggOrderLine.setOrganism(dtoOrder.getEggOrganism());
        
    	    EggOrderLine savedEggOrderLine = serviceeggorderline.create( eggOrderLine );

	        eggOrderLines.add(savedEggOrderLine);
        }

        if ( dtoOrder.getOrderType().isEmbryo() ) {

	        EmbryoOrderLine embryoOrderLine = new EmbryoOrderLine();
        
    	    embryoOrderLine.setRequired(dtoOrder.getEmbryoRequired());
        	embryoOrderLine.setSupplied(0);
	        embryoOrderLine.setEmbryoOrderLineIncubation(dtoOrder.getEmbryoOrderLineIncubation());
    	    embryoOrderLine.setOrganism(dtoOrder.getEmbryoOrganism());

        	EmbryoOrderLine savedEmbryoOrderLine = serviceembryoorderline.create( embryoOrderLine );

	        embryoOrderLines.add(savedEmbryoOrderLine);
        }

        Order order = new Order();
        
        order.setRequiredDate(dtoOrder.getRequiredDate());
        order.setSuppliedDate( null );
        order.setStudy(dtoOrder.getStudy());
        order.setCode(dtoOrder.getCode());
        order.setCustomerComment(dtoOrder.getCustomerComment());
        order.setSupplierComment("");
        
        order.setBirdOrderLines(birdOrderLines);
        order.setEggOrderLines(eggOrderLines);
        order.setEmbryoOrderLines(embryoOrderLines);
        
        order.setOrderStatus(dtoOrder.getOrderStatus());
        order.setOrderCollection(dtoOrder.getOrderCollection());
        order.setOrderType(dtoOrder.getOrderType());
        
        String customerEmail = "";
        String editorEmail = "";
        String supplierEmail = "";
        
        if ( dtoOrder.getCustomer() == null ) {

        	User customer = getNullUser();
    		order.setCustomer( customer.getId() );
    		customerEmail = customer.getEmail();
        }
        else {
        	
            if ( dtoOrder.getCustomer() == (long) 0 ) {
            
            	User customer = getNullUser();
        		order.setCustomer( customer.getId() );
        		customerEmail = customer.getEmail();
            }
            else {
        		
        		User customer = userService.getUserByID( dtoOrder.getCustomer() );
        		order.setCustomer( customer.getId() );
        		customerEmail = customer.getEmail();
        	}
        }

        if ( dtoOrder.getEditor() == null ) {

        	User editor = getNullUser();
    		order.setEditor( editor.getId() );
    		editorEmail = editor.getEmail();
        }
        else {
        	
            if ( dtoOrder.getEditor() == (long) 0 ) {
        		
            	User editor = getNullUser();
        		order.setEditor( editor.getId() );
        		editorEmail = editor.getEmail();
        	}
            else {
            	
        		User editor = userService.getUserByID( dtoOrder.getEditor() );
        		order.setEditor( editor.getId() );
        		editorEmail = editor.getEmail();
            }
        }

        if ( dtoOrder.getSupplier() == null ) {

        	User supplier = getNullUser();
    		order.setSupplier( supplier.getId() );
    		supplierEmail = supplier.getEmail();
        }
        else {
        	
            if ( dtoOrder.getSupplier() == (long) 0 ) {
            
            	User supplier = getNullUser();
        		order.setSupplier( supplier.getId() );
        		supplierEmail = supplier.getEmail();
            }
            else {
        		
        		User supplier = userService.getUserByID( dtoOrder.getSupplier() );
        		order.setSupplier( supplier.getId() );
        		supplierEmail = supplier.getEmail();
        	}
        }
        
        Order savedOrder = serviceorder.create(order);
        
        if (dataSettings.isEmailTrue()) {
        	
            mailSender.send( constructEmail("NEW " + savedOrder.getOrderType().getName() + " ORDER", savedOrder.formatNewEmail(customerEmail) + "\n\n" + getShowUrl(request, savedOrder.getOidAsString())) );
        }

        model.addAttribute("message", "Order " + savedOrder.getOid() + " ADDED successfully");

    	model.addAttribute("contextPath", request.getContextPath());

        return "selectionordersuccess";
    }
    
    
    /*
     * CRUD for Order EGGS - EDIT - Input
     */
    @RequestMapping(value = { "/edit-combined-egg-order-{oid}" }, 
    		method = RequestMethod.GET)
    public String editCombinedEggOrder(ModelMap model, 
    		@PathVariable String oid,
            final HttpServletRequest request) {
        
        LOGGER.debug("Rendering /edit-combined-egg-order - editCombinedEggOrder()");

        model.addAttribute("loggedin", whoIsLoggedIn());

        Order order = serviceorder.findByOid( ObjectConverter.convert(oid, Long.class) );

        int eggRequired = 0;
        int eggSupplied = 0;
        EggOrderLineFertilised eggOrderLineFertilised = null;
        Organism eggOrganism = null;

        Set<EggOrderLine> EggOrderLines = order.getEggOrderLines();
        
    	Iterator<EggOrderLine> iteratorEggOrderLines = EggOrderLines.iterator();
        
        while (iteratorEggOrderLines.hasNext()) {
    		
        	EggOrderLine eggorderline = iteratorEggOrderLines.next();

        	eggRequired = eggorderline.getRequired();
        	eggSupplied = eggorderline.getSupplied();
        	eggOrderLineFertilised = eggorderline.getEggOrderLineFertilised();
        	eggOrganism = eggorderline.getOrganism();
        }

        DTOOrder dtoorder = new DTOOrder();

        model.addAttribute("dtoorder", dtoorder);

		dtoorder.setRequiredDate( order.getRequiredDate() );
		dtoorder.setSuppliedDate( order.getSuppliedDate() );
		dtoorder.setStudy( order.getStudy() );
		dtoorder.setCode( order.getCode() );
		dtoorder.setCustomerComment( order.getCustomerComment() );
		dtoorder.setSupplierComment( order.getSupplierComment() );
		dtoorder.setCustomer( order.getCustomer() );
		dtoorder.setEditor( order.getEditor() );
		dtoorder.setSupplier( order.getSupplier() );
		dtoorder.setOrderStatus( order.getOrderStatus() );
		dtoorder.setOrderCollection( order.getOrderCollection() );
		dtoorder.setOrderType( order.getOrderType() );
		dtoorder.setEggRequired( eggRequired );
		dtoorder.setEggSupplied (eggSupplied );
		dtoorder.setEggOrderLineFertilised( eggOrderLineFertilised );
		dtoorder.setEggOrganism( eggOrganism );

    	List<Organism> eggorganisms = serviceorganism.findAll();
    	model.addAttribute("eggorganisms", eggorganisms);

    	List<EggOrderLineFertilised> eggorderlineFertiliseds = serviceeggorderlinefertilised.findAll();
    	Collections.sort(eggorderlineFertiliseds, new EggOrderLineFertilised.OrderByOidAsc());
    	model.addAttribute("fertiliseds", eggorderlineFertiliseds);

    	List<OrderStatus> statuss = serviceorderstatus.findAll();
    	Collections.sort(statuss, new OrderStatus.OrderByOidAsc());
    	model.addAttribute("statuss", statuss);

    	model.addAttribute("status", order.getOrderStatus().getOid().toString());

    	List<OrderCollection> collections = serviceordercollection.findAll();
    	Collections.sort(collections, new OrderCollection.OrderByOidAsc());
    	model.addAttribute("collections", collections);

    	model.addAttribute("collection", order.getOrderCollection().getOid().toString());

    	List<OrderType> types = serviceordertype.findAll();
    	Collections.sort(types, new OrderType.OrderByOidAsc());
    	model.addAttribute("types", types);

    	model.addAttribute("type", order.getOrderType().getOid().toString());

        model.addAttribute("editBool", true);
        
        List<User> customers = new ArrayList<User>();
        List<User> editors = new ArrayList<User>();
        List<User> suppliers = new ArrayList<User>();

    	if ( determinePrivilege() == 5 ) {

        	model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", true);

        	customers.add( getNullUser() );
        	customers.addAll( listByPrivilege(2) );
        	
        	editors.add( getNullUser() );
        	editors.addAll( listByPrivilege(4) );
        	editors.addAll( listByPrivilege(3) );
        	
        	suppliers.add( getNullUser() );
        	suppliers.addAll( listByPrivilege(4) );
        	suppliers.addAll( listByPrivilege(3) );
        }
        if (determinePrivilege() == 4 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("adminBool", true);
        	
        	customers.add( getNullUser() );
        	customers.addAll( listByPrivilege(2) );
        	
        	editors.add( getNullUser() );
        	editors.addAll( listByPrivilege(4) );
        	editors.addAll( listByPrivilege(3) );
        	
        	suppliers.add( getNullUser() );
        	suppliers.addAll( listByPrivilege(4) );
        	suppliers.addAll( listByPrivilege(3) );
        }
        if ( determinePrivilege() == 3 ) {
        	
            dtoorder.setEditor( whoIsLoggedInOid() );

            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("editorBool", true);
        	
        	editors.add( getNullUser() );
        	editors.addAll( listByPrivilege(3) );
        	
        	suppliers.add( getNullUser() );
        	suppliers.addAll( listByPrivilege(3) );
        }
        if ( determinePrivilege() == 2 ) {
        	
            dtoorder.setCustomer( whoIsLoggedInOid() );

            model.addAttribute("publicBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("customerBool", true);
        }
        if ( determinePrivilege() == 1 ) {
        	
            model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("publicBool", true);
        }

    	model.addAttribute("customers", customers);
    	model.addAttribute("editors", editors);
    	model.addAttribute("suppliers", suppliers);
        
    	model.addAttribute("eggBool", true);
    	model.addAttribute("birdBool", false);
    	model.addAttribute("embryoBool", false);

    	model.addAttribute("contextPath", request.getContextPath());

        return "combinedorder";
    }
    
    /*
     * CRUD for Order EGGS - EDIT - Process
     */
    @RequestMapping(value = { "/edit-combined-egg-order-{oid}" }, 
    		method = RequestMethod.POST)
    public String saveCombinedEggOrder(@Valid DTOOrder dtoOrder, 
    		BindingResult result,
            ModelMap model,
            @PathVariable String oid,
            final HttpServletRequest request) {
 
        LOGGER.debug("Processing /edit-combined-egg-order - saveCombinedEggOrder()");

        model.addAttribute("loggedin", whoIsLoggedIn());

        if (result.hasErrors()) {
            
        	return "combinedorder";
        }
        
        Order order = serviceorder.findByOid( ObjectConverter.convert(oid, Long.class) ); 

    	if ( determinePrivilege() == 5 ) {

        	model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", true);
        }
        if (determinePrivilege() == 4 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("adminBool", true);
        }
        if ( determinePrivilege() == 3 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("editorBool", true);
        }
        if ( determinePrivilege() == 2 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("customerBool", true);
        }
        if ( determinePrivilege() == 1 ) {
        	
            model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("publicBool", true);
        }

        EggOrderLine EggOrderLine = new EggOrderLine();

        Set<EggOrderLine> EggOrderLines = order.getEggOrderLines();
        
    	Iterator<EggOrderLine> iteratorEggOrderLines = EggOrderLines.iterator();
        
        while (iteratorEggOrderLines.hasNext()) {
    		
        	EggOrderLine = iteratorEggOrderLines.next();
        }

        EggOrderLine.setRequired(dtoOrder.getEggRequired());
        EggOrderLine.setSupplied(dtoOrder.getEggSupplied());
        EggOrderLine.setEggOrderLineFertilised(dtoOrder.getEggOrderLineFertilised());
        EggOrderLine.setOrganism(dtoOrder.getEggOrganism());
        
        serviceeggorderline.save(EggOrderLine);

        order.setRequiredDate(dtoOrder.getRequiredDate());
        order.setSuppliedDate( dtoOrder.getSuppliedDate() );
        order.setStudy(dtoOrder.getStudy());
        order.setCode(dtoOrder.getCode());
        order.setCustomerComment(dtoOrder.getCustomerComment());
        order.setSupplierComment(dtoOrder.getSupplierComment());
        order.setBirdOrderLines(null);
        order.setEggOrderLines(EggOrderLines);
        order.setEmbryoOrderLines(null);
        order.setOrderStatus(dtoOrder.getOrderStatus());
        order.setOrderCollection(dtoOrder.getOrderCollection());
        order.setOrderType(dtoOrder.getOrderType());

        String customerEmail = "";
        String editorEmail = "";
        String supplierEmail = "";
        
        if ( dtoOrder.getCustomer() == null ) {

        	User customer = getNullUser();
    		order.setCustomer( customer.getId() );
    		customerEmail = customer.getEmail();
        }
        else {
        	
            if ( dtoOrder.getCustomer() == (long) 0 ) {
            
            	User customer = getNullUser();
        		order.setCustomer( customer.getId() );
        		customerEmail = customer.getEmail();
            }
            else {
        		
        		User customer = userService.getUserByID( dtoOrder.getCustomer() );
        		order.setCustomer( customer.getId() );
        		customerEmail = customer.getEmail();
        	}
        }

        if ( dtoOrder.getEditor() == null ) {

        	User editor = getNullUser();
    		order.setEditor( editor.getId() );
    		editorEmail = editor.getEmail();
        }
        else {
        	
            if ( dtoOrder.getEditor() == (long) 0 ) {
        		
            	User editor = getNullUser();
        		order.setEditor( editor.getId() );
        		editorEmail = editor.getEmail();
        	}
            else {
            	
        		User editor = userService.getUserByID( dtoOrder.getEditor() );
        		order.setEditor( editor.getId() );
        		editorEmail = editor.getEmail();
            }
        }

        if ( dtoOrder.getSupplier() == null ) {

        	User supplier = getNullUser();
    		order.setSupplier( supplier.getId() );
    		supplierEmail = supplier.getEmail();
        }
        else {
        	
            if ( dtoOrder.getSupplier() == (long) 0 ) {
            
            	User supplier = getNullUser();
        		order.setSupplier( supplier.getId() );
        		supplierEmail = supplier.getEmail();
            }
            else {
        		
        		User supplier = userService.getUserByID( dtoOrder.getSupplier() );
        		order.setSupplier( supplier.getId() );
        		supplierEmail = supplier.getEmail();
        	}
        }

        serviceorder.save(order);

        if ( order.getOrderStatus().isClosed() ) {
        	
            if (dataSettings.isEmailTrue()) {
            	
                mailSender.send( constructEmail("EGG ORDER CLOSED", order.formatClosedEmail(customerEmail, editorEmail, supplierEmail) + "\n\n" +getShowUrl(request, order.getOidAsString())) );
            }
        }

        model.addAttribute("message", "Order " + order.getOid() + " EDITED successfully");

    	model.addAttribute("contextPath", request.getContextPath());

        return "combinedordersuccess";
    }
    
    /*
     * CRUD for Combined Order BIRDS - EDIT - Input
     */
    @RequestMapping(value = { "/edit-combined-bird-order-{oid}" }, 
    		method = RequestMethod.GET)
    public String editCombinedBirdOrder(ModelMap model, 
    		@PathVariable String oid,
            final HttpServletRequest request) {
        
        LOGGER.debug("Rendering /edit-combined-bird-order - editCombinedBirdOrder()");

        model.addAttribute("loggedin", whoIsLoggedIn());

        Order order = serviceorder.findByOid( ObjectConverter.convert(oid, Long.class) );

        int birdRequired = 0;
        int birdSupplied = 0;
        int birdAge = 0;
        BirdOrderLineSex birdOrderLineSex = null;
        BirdOrderLineDateFormat birdOrderLineDateFormat = null;
        Organism birdOrganism = null;

        Set<BirdOrderLine> BirdOrderLines = order.getBirdOrderLines();
        
    	Iterator<BirdOrderLine> iteratorBirdOrderLines = BirdOrderLines.iterator();
        
        while (iteratorBirdOrderLines.hasNext()) {
    		
        	BirdOrderLine birdorderline = iteratorBirdOrderLines.next();

        	birdRequired = birdorderline.getRequired();
        	birdSupplied = birdorderline.getSupplied();
        	birdAge = birdorderline.getAge();
        	birdOrderLineSex = birdorderline.getBirdOrderLineSex();
        	birdOrderLineDateFormat = birdorderline.getBirdOrderLineDateFormat();
        	birdOrganism = birdorderline.getOrganism();
        }

        DTOOrder dtoorder = new DTOOrder();

        model.addAttribute("dtoorder", dtoorder);

		dtoorder.setRequiredDate( order.getRequiredDate() );
		dtoorder.setSuppliedDate( order.getSuppliedDate() );
		dtoorder.setStudy( order.getStudy() );
		dtoorder.setCode( order.getCode() );
		dtoorder.setCustomerComment( order.getCustomerComment() );
		dtoorder.setSupplierComment( order.getSupplierComment() );
		dtoorder.setCustomer( order.getCustomer() );
		dtoorder.setEditor( order.getEditor() );
		dtoorder.setSupplier( order.getSupplier() );
		dtoorder.setOrderStatus( order.getOrderStatus() );
		dtoorder.setOrderCollection( order.getOrderCollection() );
		dtoorder.setOrderType( order.getOrderType() );
		dtoorder.setBirdRequired( birdRequired );
		dtoorder.setBirdSupplied( birdSupplied );
		dtoorder.setBirdAge( birdAge );
		dtoorder.setBirdOrderLineSex( birdOrderLineSex );
		dtoorder.setBirdOrderLineDateFormat( birdOrderLineDateFormat );
		dtoorder.setBirdOrganism( birdOrganism );

    	List<Organism> birdorganisms = serviceorganism.findAll();
    	model.addAttribute("birdorganisms", birdorganisms);

    	List<BirdOrderLineSex> birdorderlineSexs = servicebirdorderlinesex.findAll();
    	Collections.sort(birdorderlineSexs, new BirdOrderLineSex.OrderByOidAsc());
    	model.addAttribute("sexs", birdorderlineSexs);

    	List<BirdOrderLineDateFormat> birdorderlineDateFormats = servicebirdorderlinedateformat.findAll();
    	Collections.sort(birdorderlineDateFormats, new BirdOrderLineDateFormat.OrderByOidAsc());
    	model.addAttribute("dateformats", birdorderlineDateFormats);

    	List<OrderStatus> statuss = serviceorderstatus.findAll();
    	Collections.sort(statuss, new OrderStatus.OrderByOidAsc());
    	model.addAttribute("statuss", statuss);

    	model.addAttribute("status", order.getOrderStatus().getOid().toString());

    	List<OrderCollection> collections = serviceordercollection.findAll();
    	Collections.sort(collections, new OrderCollection.OrderByOidAsc());
    	model.addAttribute("collections", collections);

    	model.addAttribute("collection", order.getOrderCollection().getOid().toString());

    	List<OrderType> types = serviceordertype.findAll();
    	Collections.sort(types, new OrderType.OrderByOidAsc());
    	model.addAttribute("types", types);

    	model.addAttribute("type", order.getOrderType().getOid().toString());

        model.addAttribute("editBool", true);
        
        List<User> customers = new ArrayList<User>();
        List<User> editors = new ArrayList<User>();
        List<User> suppliers = new ArrayList<User>();

    	if ( determinePrivilege() == 5 ) {

        	model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", true);

        	customers.add( getNullUser() );
        	customers.addAll( listByPrivilege(2) );
        	
        	editors.add( getNullUser() );
        	editors.addAll( listByPrivilege(4) );
        	editors.addAll( listByPrivilege(3) );
        	
        	suppliers.add( getNullUser() );
        	suppliers.addAll( listByPrivilege(4) );
        	suppliers.addAll( listByPrivilege(3) );
        }
        if (determinePrivilege() == 4 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("adminBool", true);
        	
        	customers.add( getNullUser() );
        	customers.addAll( listByPrivilege(2) );
        	
        	editors.add( getNullUser() );
        	editors.addAll( listByPrivilege(4) );
        	editors.addAll( listByPrivilege(3) );
        	
        	suppliers.add( getNullUser() );
        	suppliers.addAll( listByPrivilege(4) );
        	suppliers.addAll( listByPrivilege(3) );
        }
        if ( determinePrivilege() == 3 ) {
        	
            dtoorder.setEditor( whoIsLoggedInOid() );

            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("editorBool", true);
        	
        	editors.add( getNullUser() );
        	editors.addAll( listByPrivilege(3) );
        	
        	suppliers.add( getNullUser() );
        	suppliers.addAll( listByPrivilege(3) );
        }
        if ( determinePrivilege() == 2 ) {
        	
            dtoorder.setCustomer( whoIsLoggedInOid() );

            model.addAttribute("publicBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("customerBool", true);
        }
        if ( determinePrivilege() == 1 ) {
        	
            model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("publicBool", true);
        }

    	model.addAttribute("customers", customers);
    	model.addAttribute("editors", editors);
    	model.addAttribute("suppliers", suppliers);
        
    	model.addAttribute("eggBool", false);
    	model.addAttribute("birdBool", true);
    	model.addAttribute("embryoBool", false);

    	model.addAttribute("contextPath", request.getContextPath());

        return "combinedorder";
    }
    
    /*
     * CRUD for Order BIRDS - EDIT - Process
     */
    @RequestMapping(value = { "/edit-combined-bird-order-{oid}" }, 
    		method = RequestMethod.POST)
    public String saveCombinedBirdOrder(@Valid DTOOrder dtoOrder, 
    		BindingResult result,
            ModelMap model,
            @PathVariable String oid,
            final HttpServletRequest request) {
 
        LOGGER.debug("Processing /edit-combined-bird-order - saveCombinedBirdOrder()");

        model.addAttribute("loggedin", whoIsLoggedIn());

        if (result.hasErrors()) {
            
        	return "order";
        }
 
    	if ( determinePrivilege() == 5 ) {

        	model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", true);
        }
        if (determinePrivilege() == 4 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("adminBool", true);
        }
        if ( determinePrivilege() == 3 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("editorBool", true);
        }
        if ( determinePrivilege() == 2 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("customerBool", true);
        }
        if ( determinePrivilege() == 1 ) {
        	
            model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("publicBool", true);
        }

        
        Order order = serviceorder.findByOid( ObjectConverter.convert(oid, Long.class) );

        String customerEmail = "";
        String editorEmail = "";
        String supplierEmail = "";
        
        if ( dtoOrder.getCustomer() == null ) {

        	User customer = getNullUser();
    		order.setCustomer( customer.getId() );
    		customerEmail = customer.getEmail();
        }
        else {
        	
            if ( dtoOrder.getCustomer() == (long) 0 ) {
            
            	User customer = getNullUser();
        		order.setCustomer( customer.getId() );
        		customerEmail = customer.getEmail();
            }
            else {
        		
        		User customer = userService.getUserByID( dtoOrder.getCustomer() );
        		order.setCustomer( customer.getId() );
        		customerEmail = customer.getEmail();
        	}
        }

        if ( dtoOrder.getEditor() == null ) {

        	User editor = getNullUser();
    		order.setEditor( editor.getId() );
    		editorEmail = editor.getEmail();
        }
        else {
        	
            if ( dtoOrder.getEditor() == (long) 0 ) {
        		
            	User editor = getNullUser();
        		order.setEditor( editor.getId() );
        		editorEmail = editor.getEmail();
        	}
            else {
            	
        		User editor = userService.getUserByID( dtoOrder.getEditor() );
        		order.setEditor( editor.getId() );
        		editorEmail = editor.getEmail();
            }
        }

        if ( dtoOrder.getSupplier() == null ) {

        	User supplier = getNullUser();
    		order.setSupplier( supplier.getId() );
    		supplierEmail = supplier.getEmail();
        }
        else {
        	
            if ( dtoOrder.getSupplier() == (long) 0 ) {
            
            	User supplier = getNullUser();
        		order.setSupplier( supplier.getId() );
        		supplierEmail = supplier.getEmail();
            }
            else {
        		
        		User supplier = userService.getUserByID( dtoOrder.getSupplier() );
        		order.setSupplier( supplier.getId() );
        		supplierEmail = supplier.getEmail();
        	}
        }

        BirdOrderLine BirdOrderLine = new BirdOrderLine();
        
        Set<BirdOrderLine> BirdOrderLines = order.getBirdOrderLines();

    	Iterator<BirdOrderLine> iteratorBirdOrderLines = BirdOrderLines.iterator();
        
        while (iteratorBirdOrderLines.hasNext()) {
    		
        	BirdOrderLine = iteratorBirdOrderLines.next();
        }

        BirdOrderLine.setRequired(dtoOrder.getBirdRequired());
        BirdOrderLine.setSupplied(dtoOrder.getBirdSupplied());
        BirdOrderLine.setBirdOrderLineSex(dtoOrder.getBirdOrderLineSex());
        BirdOrderLine.setAge(dtoOrder.getBirdAge());
        BirdOrderLine.setBirdOrderLineDateFormat(dtoOrder.getBirdOrderLineDateFormat());
        BirdOrderLine.setOrganism(dtoOrder.getBirdOrganism());

        servicebirdorderline.save(BirdOrderLine);
        
        order.setRequiredDate(dtoOrder.getRequiredDate());
        order.setSuppliedDate( dtoOrder.getSuppliedDate() );
        order.setStudy(dtoOrder.getStudy());
        order.setCode(dtoOrder.getCode());
        order.setCustomerComment(dtoOrder.getCustomerComment());
        order.setSupplierComment( dtoOrder.getSupplierComment() );
        order.setBirdOrderLines(BirdOrderLines);
        order.setEggOrderLines(null);
        order.setEmbryoOrderLines(null);
        order.setOrderStatus(dtoOrder.getOrderStatus());
        order.setOrderType(dtoOrder.getOrderType());
        order.setOrderCollection(dtoOrder.getOrderCollection());
        
        serviceorder.save(order);
        
        if ( order.getOrderStatus().isClosed() ) {
        	
            if (dataSettings.isEmailTrue()) {
            	
                mailSender.send( constructEmail("BIRD ORDER CLOSED", order.formatClosedEmail(customerEmail, editorEmail, supplierEmail) + "\n\n" +getShowUrl(request, order.getOidAsString())) );
            }
        }

        model.addAttribute("message", "Order " + order.getOid() + " EDITED successfully");
        
    	model.addAttribute("contextPath", request.getContextPath());

        return "combinedordersuccess";
    }
    
    /*
     * CRUD for Order EMBRYOS - EDIT - Input
     */
    @RequestMapping(value = { "/edit-combined-embryo-order-{oid}" }, 
    		method = RequestMethod.GET)
    public String editCombinedEmbryoOrder(ModelMap model, 
    		@PathVariable String oid,
            final HttpServletRequest request) {
        
        LOGGER.debug("Rendering /edit-combined-embryo-order - editCombinedEmbryoOrder()");

        model.addAttribute("loggedin", whoIsLoggedIn());

        Order order = serviceorder.findByOid( ObjectConverter.convert(oid, Long.class) );

        int embryoRequired = 0;
        int embryoSupplied = 0;
        EmbryoOrderLineIncubation embryoOrderLineIncubation = null;
        Organism embryoOrganism = null;

        Set<EmbryoOrderLine> EmbryoOrderLines = order.getEmbryoOrderLines();
        
    	Iterator<EmbryoOrderLine> iteratorEmbryoOrderLines = EmbryoOrderLines.iterator();
        
        while (iteratorEmbryoOrderLines.hasNext()) {
    		
        	EmbryoOrderLine embryoorderline = iteratorEmbryoOrderLines.next();

        	embryoRequired = embryoorderline.getRequired();
        	embryoSupplied = embryoorderline.getSupplied();
        	embryoOrderLineIncubation = embryoorderline.getEmbryoOrderLineIncubation();
        	embryoOrganism = embryoorderline.getOrganism();
        }

        DTOOrder dtoorder = new DTOOrder();

        model.addAttribute("dtoorder", dtoorder);

		dtoorder.setRequiredDate( order.getRequiredDate() );
		dtoorder.setSuppliedDate( order.getSuppliedDate() );
		dtoorder.setStudy( order.getStudy() );
		dtoorder.setCode( order.getCode() );
		dtoorder.setCustomerComment( order.getCustomerComment() );
		dtoorder.setSupplierComment( order.getSupplierComment() );
		dtoorder.setCustomer( order.getCustomer() );
		dtoorder.setEditor( order.getEditor() );
		dtoorder.setSupplier( order.getSupplier() );
		dtoorder.setOrderStatus( order.getOrderStatus() );
		dtoorder.setOrderCollection( order.getOrderCollection() );
		dtoorder.setOrderType( order.getOrderType() );
		dtoorder.setEmbryoRequired( embryoRequired );
		dtoorder.setEmbryoSupplied( embryoSupplied );
		dtoorder.setEmbryoOrderLineIncubation( embryoOrderLineIncubation );
		dtoorder.setEmbryoOrganism( embryoOrganism );

    	List<Organism> embryoorganisms = serviceorganism.findAll();
    	model.addAttribute("embryoorganisms", embryoorganisms);

    	List<EmbryoOrderLineIncubation> embryoorderlineIncubations = serviceembryoorderlineincubation.findAll();
    	Collections.sort(embryoorderlineIncubations, new EmbryoOrderLineIncubation.OrderByOidAsc());
    	model.addAttribute("incubateds", embryoorderlineIncubations);

    	List<OrderStatus> statuss = serviceorderstatus.findAll();
    	Collections.sort(statuss, new OrderStatus.OrderByOidAsc());
    	model.addAttribute("statuss", statuss);

    	model.addAttribute("status", order.getOrderStatus().getOid().toString());

    	List<OrderCollection> collections = serviceordercollection.findAll();
    	Collections.sort(collections, new OrderCollection.OrderByOidAsc());
    	model.addAttribute("collections", collections);

    	model.addAttribute("collection", order.getOrderCollection().getOid().toString());

    	List<OrderType> types = serviceordertype.findAll();
    	Collections.sort(types, new OrderType.OrderByOidAsc());
    	model.addAttribute("types", types);

    	model.addAttribute("type", order.getOrderType().getOid().toString());

        model.addAttribute("editBool", true);
        
        List<User> customers = new ArrayList<User>();
        List<User> editors = new ArrayList<User>();
        List<User> suppliers = new ArrayList<User>();

    	if ( determinePrivilege() == 5 ) {

        	model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", true);

        	customers.add( getNullUser() );
        	customers.addAll( listByPrivilege(2) );
        	
        	editors.add( getNullUser() );
        	editors.addAll( listByPrivilege(4) );
        	editors.addAll( listByPrivilege(3) );
        	
        	suppliers.add( getNullUser() );
        	suppliers.addAll( listByPrivilege(4) );
        	suppliers.addAll( listByPrivilege(3) );
        }
        if (determinePrivilege() == 4 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("adminBool", true);
        	
        	customers.add( getNullUser() );
        	customers.addAll( listByPrivilege(2) );
        	
        	editors.add( getNullUser() );
        	editors.addAll( listByPrivilege(4) );
        	editors.addAll( listByPrivilege(3) );
        	
        	suppliers.add( getNullUser() );
        	suppliers.addAll( listByPrivilege(4) );
        	suppliers.addAll( listByPrivilege(3) );
        }
        if ( determinePrivilege() == 3 ) {
        	
            dtoorder.setEditor( whoIsLoggedInOid() );

            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("editorBool", true);
        	
        	editors.add( getNullUser() );
        	editors.addAll( listByPrivilege(3) );
        	
        	suppliers.add( getNullUser() );
        	suppliers.addAll( listByPrivilege(3) );
        }
        if ( determinePrivilege() == 2 ) {
        	
            dtoorder.setCustomer( whoIsLoggedInOid() );

            model.addAttribute("publicBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("customerBool", true);
        }
        if ( determinePrivilege() == 1 ) {
        	
            model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("publicBool", true);
        }

    	model.addAttribute("customers", customers);
    	model.addAttribute("editors", editors);
    	model.addAttribute("suppliers", suppliers);
        
    	model.addAttribute("eggBool", false);
    	model.addAttribute("birdBool", false);
    	model.addAttribute("embryoBool", true);

    	model.addAttribute("contextPath", request.getContextPath());

        return "combinedorder";
    }
    
    /*
     * CRUD for Order EMBRYOS - EDIT - Process
     */
    @RequestMapping(value = { "/edit-combined-embryo-order-{oid}" }, 
    		method = RequestMethod.POST)
    public String saveCombinedEmbryoOrder(@Valid DTOOrder dtoOrder, 
    		BindingResult result,
            ModelMap model,
            @PathVariable String oid,
            final HttpServletRequest request) {
 
        LOGGER.debug("Processing /edit-combined-embryo-order - saveCombinedEmbryoOrder()");

        model.addAttribute("loggedin", whoIsLoggedIn());

        if (result.hasErrors()) {
            
        	return "order";
        }

        Order order = serviceorder.findByOid( ObjectConverter.convert(oid, Long.class) );

    	if ( determinePrivilege() == 5 ) {

        	model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", true);
        }
        if (determinePrivilege() == 4 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("adminBool", true);
        }
        if ( determinePrivilege() == 3 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("editorBool", true);
        }
        if ( determinePrivilege() == 2 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("customerBool", true);
        }
        if ( determinePrivilege() == 1 ) {
        	
            model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("publicBool", true);
        }

        Set<EmbryoOrderLine> EmbryoOrderLines = order.getEmbryoOrderLines();

        EmbryoOrderLine EmbryoOrderLine = new EmbryoOrderLine();

        Iterator<EmbryoOrderLine> iteratorEmbryoOrderLines = EmbryoOrderLines.iterator();
        
        while (iteratorEmbryoOrderLines.hasNext()) {
    		
        	EmbryoOrderLine = iteratorEmbryoOrderLines.next();
        }
        
        EmbryoOrderLine.setRequired(dtoOrder.getEmbryoRequired());
        EmbryoOrderLine.setSupplied(dtoOrder.getEmbryoSupplied());
        EmbryoOrderLine.setEmbryoOrderLineIncubation(dtoOrder.getEmbryoOrderLineIncubation());
        EmbryoOrderLine.setOrganism(dtoOrder.getEmbryoOrganism());

        serviceembryoorderline.save(EmbryoOrderLine);
        
        order.setRequiredDate(dtoOrder.getRequiredDate());
        order.setSuppliedDate( dtoOrder.getSuppliedDate() );
        order.setStudy(dtoOrder.getStudy());
        order.setCode(dtoOrder.getCode());
        order.setCustomerComment(dtoOrder.getCustomerComment());
        order.setSupplierComment( dtoOrder.getSupplierComment() );
        order.setBirdOrderLines(null);
        order.setEggOrderLines(null);
        order.setEmbryoOrderLines(EmbryoOrderLines);
        order.setOrderStatus(dtoOrder.getOrderStatus());
        order.setOrderCollection(dtoOrder.getOrderCollection());
        order.setOrderType(dtoOrder.getOrderType());

        String customerEmail = "";
        String editorEmail = "";
        String supplierEmail = "";
        
        if ( dtoOrder.getCustomer() == null ) {

        	User customer = getNullUser();
    		order.setCustomer( customer.getId() );
    		customerEmail = customer.getEmail();
        }
        else {
        	
            if ( dtoOrder.getCustomer() == (long) 0 ) {
            
            	User customer = getNullUser();
        		order.setCustomer( customer.getId() );
        		customerEmail = customer.getEmail();
            }
            else {
        		
        		User customer = userService.getUserByID( dtoOrder.getCustomer() );
        		order.setCustomer( customer.getId() );
        		customerEmail = customer.getEmail();
        	}
        }

        if ( dtoOrder.getEditor() == null ) {

        	User editor = getNullUser();
    		order.setEditor( editor.getId() );
    		editorEmail = editor.getEmail();
        }
        else {
        	
            if ( dtoOrder.getEditor() == (long) 0 ) {
        		
            	User editor = getNullUser();
        		order.setEditor( editor.getId() );
        		editorEmail = editor.getEmail();
        	}
            else {
            	
        		User editor = userService.getUserByID( dtoOrder.getEditor() );
        		order.setEditor( editor.getId() );
        		editorEmail = editor.getEmail();
            }
        }

        if ( dtoOrder.getSupplier() == null ) {

        	User supplier = getNullUser();
    		order.setSupplier( supplier.getId() );
    		supplierEmail = supplier.getEmail();
        }
        else {
        	
            if ( dtoOrder.getSupplier() == (long) 0 ) {
            
            	User supplier = getNullUser();
        		order.setSupplier( supplier.getId() );
        		supplierEmail = supplier.getEmail();
            }
            else {
        		
        		User supplier = userService.getUserByID( dtoOrder.getSupplier() );
        		order.setSupplier( supplier.getId() );
        		supplierEmail = supplier.getEmail();
        	}
        }

        serviceorder.save(order);
        
        if ( order.getOrderStatus().isClosed() ) {
        	
            if (dataSettings.isEmailTrue()) {
            	
                mailSender.send( constructEmail("EMBRYO ORDER CLOSED", order.formatClosedEmail(customerEmail, editorEmail, supplierEmail) + "\n\n" +getShowUrl(request, order.getOidAsString())) );
            }
        }

        model.addAttribute("message", "Order " + order.getOid() + " EDITED successfully");
        
    	model.addAttribute("contextPath", request.getContextPath());

        return "combinedordersuccess";
    }
    

     
    /*
     * CRUD for Order - LIST ALL
     */
    @RequestMapping(value = { "/orders-list" }, 
    		method = RequestMethod.GET)
    public String listOrders(ModelMap model) {
    	
        LOGGER.debug("Rendering /orders-list - listOrders()");

        model.addAttribute("loggedin", whoIsLoggedIn());

    	model.addAttribute("mineBool", false);

    	model.addAttribute("ordersall", true);
    	model.addAttribute("ordersnew", false);
    	model.addAttribute("orderspending", false);
    	model.addAttribute("ordersclosed", false);
    	model.addAttribute("orderscancelled", false);

    	if ( determinePrivilege() == 5 ) {

            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", true);
        }
        if (determinePrivilege() == 4 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("adminBool", true);
        }
        if ( determinePrivilege() == 3 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("editorBool", true);
        }
        if ( determinePrivilege() == 2 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("customerBool", true);
        }
        if ( determinePrivilege() == 1 ) {
        	
            model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("publicBool", true);
        }
        
        List<Order> orders = serviceorder.findAll();
        
    	Collections.sort(orders, new Order.OrderByCreatedDesc());

    	List<DTOOrder> dtoorders = new ArrayList<DTOOrder>();

        Iterator<Order> iteratorOrder = orders.iterator();
            
        while (iteratorOrder.hasNext()) {
        		
           	Order order = iteratorOrder.next();
           	
           	DTOOrder dtoorder = order.convertToDTOOrder();

        	if ( dtoorder.getCustomer() != (long) 0 ) {
        		
        		User customer = userService.getUserByID( dtoorder.getCustomer() );

        		dtoorder.setCustomerEmail( customer.getEmail() );
        		dtoorder.setCustomerOrganisation( customer.getOrganisation() );
        	}

        	if ( dtoorder.getEditor() != (long) 0 ) {
        		
        		User editor = userService.getUserByID( dtoorder.getEditor() );

        		dtoorder.setEditorEmail( editor.getEmail() );
        	}

        	if ( dtoorder.getSupplier() != (long) 0 ) {
        		
        		User supplier = userService.getUserByID( dtoorder.getSupplier() );

        		dtoorder.setSupplierEmail( supplier.getEmail() );
        	}

        	dtoorders.add( dtoorder );
        }
    	
        model.addAttribute("orders", dtoorders);

        return "combinedorderslist";
    }
    
    /*
     * CRUD for Order - LIST ALL - NEW
     */
    @RequestMapping(value = { "/orders-list-new" }, 
    		method = RequestMethod.GET)
    public String listOrdersNew(ModelMap model) {
    	
        LOGGER.debug("Rendering /orders-list-new - listOrdersNew()");

        model.addAttribute("loggedin", whoIsLoggedIn());

    	model.addAttribute("mineBool", false);

    	model.addAttribute("ordersall", false);
    	model.addAttribute("ordersnew", true);
    	model.addAttribute("orderspending", false);
    	model.addAttribute("ordersclosed", false);
    	model.addAttribute("orderscancelled", false);

    	if ( determinePrivilege() == 5 ) {

            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", true);
        }
        if (determinePrivilege() == 4 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("adminBool", true);
        }
        if ( determinePrivilege() == 3 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("editorBool", true);
        }
        if ( determinePrivilege() == 2 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("customerBool", true);
        }
        if ( determinePrivilege() == 1 ) {
        	
            model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("publicBool", true);
        }
        
        OrderStatus orderstatusNew = serviceorderstatus.findByName(EnumOrderStatus.NEW.getEnumOrderStatus());

        List<Order> orders = serviceorder.findAllStatus( orderstatusNew );
        
    	Collections.sort(orders, new Order.OrderByCreatedDesc());

    	List<DTOOrder> dtoorders = new ArrayList<DTOOrder>();

        Iterator<Order> iteratorOrder = orders.iterator();
            
        while (iteratorOrder.hasNext()) {
        		
           	Order order = iteratorOrder.next();
           	
           	DTOOrder dtoorder = order.convertToDTOOrder();

        	if ( dtoorder.getCustomer() != (long) 0 ) {
        		
        		User customer = userService.getUserByID( dtoorder.getCustomer() );

        		dtoorder.setCustomerEmail( customer.getEmail() );
        		dtoorder.setCustomerOrganisation( customer.getOrganisation() );
        	}

        	if ( dtoorder.getEditor() != (long) 0 ) {
        		
        		User editor = userService.getUserByID( dtoorder.getEditor() );

        		dtoorder.setEditorEmail( editor.getEmail() );
        	}

        	if ( dtoorder.getSupplier() != (long) 0 ) {
        		
        		User supplier = userService.getUserByID( dtoorder.getSupplier() );

        		dtoorder.setSupplierEmail( supplier.getEmail() );
        	}

        	dtoorders.add( dtoorder );

        }
    	
        model.addAttribute("orders", dtoorders);

        return "combinedorderslist";
    }

    /*
     * CRUD for Order - LIST ALL - PENDING_GREENWOOD && PENDING_BUMSTEAD
     */
    @RequestMapping(value = { "/orders-list-pending" }, 
    		method = RequestMethod.GET)
    public String listOrdersPending(ModelMap model) {
    	
        LOGGER.debug("Rendering /orders-list-pending - listOrdersPending()");

        model.addAttribute("loggedin", whoIsLoggedIn());

    	model.addAttribute("mineBool", false);

    	model.addAttribute("ordersall", false);
    	model.addAttribute("ordersnew", false);
    	model.addAttribute("orderspending", true);
    	model.addAttribute("ordersclosed", false);
    	model.addAttribute("orderscancelled", false);

    	if ( determinePrivilege() == 5 ) {

            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", true);
        }
        if (determinePrivilege() == 4 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("adminBool", true);
        }
        if ( determinePrivilege() == 3 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("editorBool", true);
        }
        if ( determinePrivilege() == 2 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("customerBool", true);
        }
        if ( determinePrivilege() == 1 ) {
        	
            model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("publicBool", true);
        }
        
        OrderStatus orderstatusPendingGreenwood = serviceorderstatus.findByName(EnumOrderStatus.PENDING_GREENWOOD.getEnumOrderStatus());
        OrderStatus orderstatusPendingBumstead = serviceorderstatus.findByName(EnumOrderStatus.PENDING_BUMSTEAD.getEnumOrderStatus());
       
        List<Order> ordersGreenwood = serviceorder.findAllStatus( orderstatusPendingGreenwood );
        List<Order> ordersBumstead = serviceorder.findAllStatus( orderstatusPendingBumstead );

        List<Order> orders = new ArrayList<Order>();

        orders.addAll(ordersGreenwood);
        orders.addAll(ordersBumstead);
        
    	Collections.sort(orders, new Order.OrderByCreatedDesc());

    	List<DTOOrder> dtoorders = new ArrayList<DTOOrder>();

        Iterator<Order> iteratorOrder = orders.iterator();
            
        while (iteratorOrder.hasNext()) {
        		
           	Order order = iteratorOrder.next();
           	
           	DTOOrder dtoorder = order.convertToDTOOrder();

        	if ( dtoorder.getCustomer() != (long) 0 ) {
        		
        		User customer = userService.getUserByID( dtoorder.getCustomer() );

        		dtoorder.setCustomerEmail( customer.getEmail() );
        		dtoorder.setCustomerOrganisation( customer.getOrganisation() );
        	}

        	if ( dtoorder.getEditor() != (long) 0 ) {
        		
        		User editor = userService.getUserByID( dtoorder.getEditor() );

        		dtoorder.setEditorEmail( editor.getEmail() );
        	}

        	if ( dtoorder.getSupplier() != (long) 0 ) {
        		
        		User supplier = userService.getUserByID( dtoorder.getSupplier() );

        		dtoorder.setSupplierEmail( supplier.getEmail() );
        	}

        	dtoorders.add( dtoorder );

        }
    	
        model.addAttribute("orders", dtoorders);

        return "combinedorderslist";
    }

    
    /*
     * CRUD for Order - LIST ALL - CLOSED
     */
    @RequestMapping(value = { "/orders-list-closed" }, 
    		method = RequestMethod.GET)
    public String listOrdersClosed(ModelMap model) {
    	
        LOGGER.debug("Rendering /orders-list-closed - listOrdersClosed()");

        model.addAttribute("loggedin", whoIsLoggedIn());

    	model.addAttribute("mineBool", false);

    	model.addAttribute("ordersall", false);
    	model.addAttribute("ordersnew", false);
    	model.addAttribute("orderspending", false);
    	model.addAttribute("ordersclosed", true);
    	model.addAttribute("orderscancelled", false);

    	if ( determinePrivilege() == 5 ) {

            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", true);
        }
        if (determinePrivilege() == 4 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("adminBool", true);
        }
        if ( determinePrivilege() == 3 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("editorBool", true);
        }
        if ( determinePrivilege() == 2 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("customerBool", true);
        }
        if ( determinePrivilege() == 1 ) {
        	
            model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("publicBool", true);
        }
        
        OrderStatus orderstatusClosed = serviceorderstatus.findByName(EnumOrderStatus.CLOSED.getEnumOrderStatus());

        List<Order> orders = serviceorder.findAllStatus( orderstatusClosed );

    	Collections.sort(orders, new Order.OrderByCreatedDesc());

    	List<DTOOrder> dtoorders = new ArrayList<DTOOrder>();

        Iterator<Order> iteratorOrder = orders.iterator();
            
        while (iteratorOrder.hasNext()) {
        		
           	Order order = iteratorOrder.next();
           	
           	DTOOrder dtoorder = order.convertToDTOOrder();

        	if ( dtoorder.getCustomer() != (long) 0 ) {
        		
        		User customer = userService.getUserByID( dtoorder.getCustomer() );

        		dtoorder.setCustomerEmail( customer.getEmail() );
        		dtoorder.setCustomerOrganisation( customer.getOrganisation() );
        	}

        	if ( dtoorder.getEditor() != (long) 0 ) {
        		
        		User editor = userService.getUserByID( dtoorder.getEditor() );

        		dtoorder.setEditorEmail( editor.getEmail() );
        	}

        	if ( dtoorder.getSupplier() != (long) 0 ) {
        		
        		User supplier = userService.getUserByID( dtoorder.getSupplier() );

        		dtoorder.setSupplierEmail( supplier.getEmail() );
        	}

        	dtoorders.add( dtoorder );

        }
    	
        model.addAttribute("orders", dtoorders);

        return "combinedorderslist";
    }

    
    /*
     * CRUD for Order - LIST ALL - CANCELLED
     */
    @RequestMapping(value = { "/orders-list-cancelled" }, 
    		method = RequestMethod.GET)
    public String listOrdersCancelled(ModelMap model) {
    	
        LOGGER.debug("Rendering /orders-list-cancelled - listOrdersCancelled()");

        model.addAttribute("loggedin", whoIsLoggedIn());

    	model.addAttribute("mineBool", false);

    	model.addAttribute("ordersall", false);
    	model.addAttribute("ordersnew", false);
    	model.addAttribute("orderspending", false);
    	model.addAttribute("ordersclosed", false);
    	model.addAttribute("orderscancelled", true);

    	if ( determinePrivilege() == 5 ) {

            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", true);
        }
        if (determinePrivilege() == 4 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("adminBool", true);
        }
        if ( determinePrivilege() == 3 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("editorBool", true);
        }
        if ( determinePrivilege() == 2 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("customerBool", true);
        }
        if ( determinePrivilege() == 1 ) {
        	
            model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("publicBool", true);
        }
        
        OrderStatus orderstatusCancelled = serviceorderstatus.findByName(EnumOrderStatus.CANCELLED.getEnumOrderStatus());

        List<Order> orders = serviceorder.findAllStatus( orderstatusCancelled );

    	Collections.sort(orders, new Order.OrderByCreatedDesc());

    	List<DTOOrder> dtoorders = new ArrayList<DTOOrder>();

        Iterator<Order> iteratorOrder = orders.iterator();
            
        while (iteratorOrder.hasNext()) {
        		
           	Order order = iteratorOrder.next();
           	
           	DTOOrder dtoorder = order.convertToDTOOrder();

        	if ( dtoorder.getCustomer() != (long) 0 ) {
        		
        		User customer = userService.getUserByID( dtoorder.getCustomer() );

        		dtoorder.setCustomerEmail( customer.getEmail() );
        		dtoorder.setCustomerOrganisation( customer.getOrganisation() );
        	}

        	if ( dtoorder.getEditor() != (long) 0 ) {
        		
        		User editor = userService.getUserByID( dtoorder.getEditor() );

        		dtoorder.setEditorEmail( editor.getEmail() );
        	}

        	if ( dtoorder.getSupplier() != (long) 0 ) {
        		
        		User supplier = userService.getUserByID( dtoorder.getSupplier() );

        		dtoorder.setSupplierEmail( supplier.getEmail() );
        	}

        	dtoorders.add( dtoorder );

        }
    	
        model.addAttribute("orders", dtoorders);

        return "combinedorderslist";
    }

    /*
     * CRUD for Order - LIST MINE
     */
    @RequestMapping(value = { "/my-orders-list" }, 
    		method = RequestMethod.GET)
    public String listMyOrders(ModelMap model) {
    	
        LOGGER.debug("Rendering /my-orders-list - listMyOrders()");

        model.addAttribute("loggedin", whoIsLoggedIn());

    	model.addAttribute("mineBool", true);

    	model.addAttribute("ordersall", true);
    	model.addAttribute("ordersnew", false);
    	model.addAttribute("orderspending", false);
    	model.addAttribute("ordersclosed", false);
    	model.addAttribute("orderscancelled", false);

    	if ( determinePrivilege() == 5 ) {

            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", true);
        }
        if (determinePrivilege() == 4 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("adminBool", true);
        }
        if ( determinePrivilege() == 3 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("editorBool", true);
        }
        if ( determinePrivilege() == 2 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("customerBool", true);
        }
        if ( determinePrivilege() == 1 ) {
        	
            model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("publicBool", true);
        }

        List<Order> orders = serviceorder.findAllByCustomer( whoIsLoggedInOid() );

    	Collections.sort(orders, new Order.OrderByCreatedDesc());
    	
    	List<DTOOrder> dtoorders = new ArrayList<DTOOrder>();

        Iterator<Order> iteratorOrder = orders.iterator();
            
        while (iteratorOrder.hasNext()) {
        		
           	Order order = iteratorOrder.next();
           	
        	DTOOrder dtoorder = order.convertToDTOOrder();

        	if ( dtoorder.getCustomer() != (long) 0 ) {
        		
        		User customer = userService.getUserByID( dtoorder.getCustomer() );

        		dtoorder.setCustomerEmail( customer.getEmail() );
        		dtoorder.setCustomerOrganisation( customer.getOrganisation() );
        	}

        	if ( dtoorder.getEditor() != (long) 0 ) {
        		
        		User editor = userService.getUserByID( dtoorder.getEditor() );

        		dtoorder.setEditorEmail( editor.getEmail() );
        	}

        	if ( dtoorder.getSupplier() != (long) 0 ) {
        		
        		User supplier = userService.getUserByID( dtoorder.getSupplier() );

        		dtoorder.setSupplierEmail( supplier.getEmail() );
        	}

        	dtoorders.add( dtoorder );
        }
    	
        model.addAttribute("orders", dtoorders);

        return "combinedorderslist";
    }

    /*
     * CRUD for Order - LIST ALL - NEW
     */
    @RequestMapping(value = { "/my-orders-list-new" }, 
    		method = RequestMethod.GET)
    public String listMyOrdersNew(ModelMap model) {
    	
        LOGGER.debug("Rendering /my-orders-list-new - listMyOrdersNew()");

        model.addAttribute("loggedin", whoIsLoggedIn());

    	model.addAttribute("mineBool", true);

    	model.addAttribute("ordersall", false);
    	model.addAttribute("ordersnew", true);
    	model.addAttribute("orderspending", false);
    	model.addAttribute("ordersclosed", false);
    	model.addAttribute("orderscancelled", false);

    	if ( determinePrivilege() == 5 ) {

            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", true);
        }
        if (determinePrivilege() == 4 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("adminBool", true);
        }
        if ( determinePrivilege() == 3 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("editorBool", true);
        }
        if ( determinePrivilege() == 2 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("customerBool", true);
        }
        if ( determinePrivilege() == 1 ) {
        	
            model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("publicBool", true);
        }
        
        OrderStatus orderstatusNew = serviceorderstatus.findByName(EnumOrderStatus.NEW.getEnumOrderStatus());

        List<Order> orders = serviceorder.findAllByCustomerAndStatus( whoIsLoggedInOid(), orderstatusNew );

    	Collections.sort(orders, new Order.OrderByCreatedDesc());

    	List<DTOOrder> dtoorders = new ArrayList<DTOOrder>();

        Iterator<Order> iteratorOrder = orders.iterator();
            
        while (iteratorOrder.hasNext()) {
        		
           	Order order = iteratorOrder.next();
           	
           	DTOOrder dtoorder = order.convertToDTOOrder();

        	if ( dtoorder.getCustomer() != (long) 0 ) {
        		
        		User customer = userService.getUserByID( dtoorder.getCustomer() );

        		dtoorder.setCustomerEmail( customer.getEmail() );
        		dtoorder.setCustomerOrganisation( customer.getOrganisation() );
        	}

        	if ( dtoorder.getEditor() != (long) 0 ) {
        		
        		User editor = userService.getUserByID( dtoorder.getEditor() );

        		dtoorder.setEditorEmail( editor.getEmail() );
        	}

        	if ( dtoorder.getSupplier() != (long) 0 ) {
        		
        		User supplier = userService.getUserByID( dtoorder.getSupplier() );

        		dtoorder.setSupplierEmail( supplier.getEmail() );
        	}

        	dtoorders.add( dtoorder );

        }
    	
        model.addAttribute("orders", dtoorders);

        return "combinedorderslist";
    }

    /*
     * CRUD for Order - LIST ALL - PENDING_GREENWOOD && PENDING_BUMSTEAD
     */
    @RequestMapping(value = { "/my-orders-list-pending" }, 
    		method = RequestMethod.GET)
    public String listMyOrdersPending(ModelMap model) {
    	
        LOGGER.debug("Rendering /my-orders-list-pending - listMyOrdersPending()");

        model.addAttribute("loggedin", whoIsLoggedIn());

    	model.addAttribute("mineBool", true);

    	model.addAttribute("ordersall", false);
    	model.addAttribute("ordersnew", false);
    	model.addAttribute("orderspending", true);
    	model.addAttribute("ordersclosed", false);
    	model.addAttribute("orderscancelled", false);

    	if ( determinePrivilege() == 5 ) {

            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", true);
        }
        if (determinePrivilege() == 4 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("adminBool", true);
        }
        if ( determinePrivilege() == 3 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("editorBool", true);
        }
        if ( determinePrivilege() == 2 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("customerBool", true);
        }
        if ( determinePrivilege() == 1 ) {
        	
            model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("publicBool", true);
        }
        
        OrderStatus orderstatusPendingGreenwood = serviceorderstatus.findByName(EnumOrderStatus.PENDING_GREENWOOD.getEnumOrderStatus());
        OrderStatus orderstatusPendingBumstead = serviceorderstatus.findByName(EnumOrderStatus.PENDING_BUMSTEAD.getEnumOrderStatus());
       
        List<Order> ordersGreenwood = serviceorder.findAllByCustomerAndStatus( whoIsLoggedInOid(), orderstatusPendingGreenwood );
        List<Order> ordersBumstead = serviceorder.findAllByCustomerAndStatus( whoIsLoggedInOid(), orderstatusPendingBumstead );

        List<Order> orders = new ArrayList<Order>();

        orders.addAll(ordersGreenwood);
        orders.addAll(ordersBumstead);
        
    	Collections.sort(orders, new Order.OrderByCreatedDesc());

    	List<DTOOrder> dtoorders = new ArrayList<DTOOrder>();

        Iterator<Order> iteratorOrder = orders.iterator();
            
        while (iteratorOrder.hasNext()) {
        		
           	Order order = iteratorOrder.next();
           	
           	DTOOrder dtoorder = order.convertToDTOOrder();

        	if ( dtoorder.getCustomer() != (long) 0 ) {
        		
        		User customer = userService.getUserByID( dtoorder.getCustomer() );

        		dtoorder.setCustomerEmail( customer.getEmail() );
        		dtoorder.setCustomerOrganisation( customer.getOrganisation() );
        	}

        	if ( dtoorder.getEditor() != (long) 0 ) {
        		
        		User editor = userService.getUserByID( dtoorder.getEditor() );

        		dtoorder.setEditorEmail( editor.getEmail() );
        	}

        	if ( dtoorder.getSupplier() != (long) 0 ) {
        		
        		User supplier = userService.getUserByID( dtoorder.getSupplier() );

        		dtoorder.setSupplierEmail( supplier.getEmail() );
        	}

        	dtoorders.add( dtoorder );

        }
    	
        model.addAttribute("orders", dtoorders);

        return "combinedorderslist";
    }

    
    /*
     * CRUD for Order - LIST ALL - CLOSED
     */
    @RequestMapping(value = { "/my-orders-list-closed" }, 
    		method = RequestMethod.GET)
    public String listMyOrdersClosed(ModelMap model) {
    	
        LOGGER.debug("Rendering /my-orders-list-closed - listMyOrdersClosed()");

        model.addAttribute("loggedin", whoIsLoggedIn());

    	model.addAttribute("mineBool", true);

    	model.addAttribute("ordersall", false);
    	model.addAttribute("ordersnew", false);
    	model.addAttribute("orderspending", false);
    	model.addAttribute("ordersclosed", true);
    	model.addAttribute("orderscancelled", false);

    	if ( determinePrivilege() == 5 ) {

            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", true);
        }
        if (determinePrivilege() == 4 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("adminBool", true);
        }
        if ( determinePrivilege() == 3 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("editorBool", true);
        }
        if ( determinePrivilege() == 2 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("customerBool", true);
        }
        if ( determinePrivilege() == 1 ) {
        	
            model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("publicBool", true);
        }
        
        OrderStatus orderstatusClosed = serviceorderstatus.findByName(EnumOrderStatus.CLOSED.getEnumOrderStatus());

        List<Order> orders = serviceorder.findAllByCustomerAndStatus( whoIsLoggedInOid(), orderstatusClosed );

    	Collections.sort(orders, new Order.OrderByCreatedDesc());

    	List<DTOOrder> dtoorders = new ArrayList<DTOOrder>();

        Iterator<Order> iteratorOrder = orders.iterator();
            
        while (iteratorOrder.hasNext()) {
        		
           	Order order = iteratorOrder.next();
           	
           	DTOOrder dtoorder = order.convertToDTOOrder();

        	if ( dtoorder.getCustomer() != (long) 0 ) {
        		
        		User customer = userService.getUserByID( dtoorder.getCustomer() );

        		dtoorder.setCustomerEmail( customer.getEmail() );
        		dtoorder.setCustomerOrganisation( customer.getOrganisation() );
        	}

        	if ( dtoorder.getEditor() != (long) 0 ) {
        		
        		User editor = userService.getUserByID( dtoorder.getEditor() );

        		dtoorder.setEditorEmail( editor.getEmail() );
        	}

        	if ( dtoorder.getSupplier() != (long) 0 ) {
        		
        		User supplier = userService.getUserByID( dtoorder.getSupplier() );

        		dtoorder.setSupplierEmail( supplier.getEmail() );
        	}

        	dtoorders.add( dtoorder );

        }
    	
        model.addAttribute("orders", dtoorders);

        return "combinedorderslist";
    }

    /*
     * CRUD for Order - LIST ALL - CANCELLED
     */
    @RequestMapping(value = { "/my-orders-list-cancelled" }, 
    		method = RequestMethod.GET)
    public String listMyOrdersCancelled(ModelMap model) {
    	
        LOGGER.debug("Rendering /my-orders-list-cancelled - listMyOrdersCancelled()");

        model.addAttribute("loggedin", whoIsLoggedIn());

    	model.addAttribute("mineBool", true);

    	model.addAttribute("ordersall", false);
    	model.addAttribute("ordersnew", false);
    	model.addAttribute("orderspending", false);
    	model.addAttribute("ordersclosed", false);
    	model.addAttribute("orderscancelled", true);

    	if ( determinePrivilege() == 5 ) {

            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", true);
        }
        if (determinePrivilege() == 4 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("adminBool", true);
        }
        if ( determinePrivilege() == 3 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("editorBool", true);
        }
        if ( determinePrivilege() == 2 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("customerBool", true);
        }
        if ( determinePrivilege() == 1 ) {
        	
            model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("publicBool", true);
        }
        
        OrderStatus orderstatusCancelled = serviceorderstatus.findByName(EnumOrderStatus.CANCELLED.getEnumOrderStatus());

        List<Order> orders = serviceorder.findAllByCustomerAndStatus( whoIsLoggedInOid(), orderstatusCancelled );

    	Collections.sort(orders, new Order.OrderByCreatedDesc());

    	List<DTOOrder> dtoorders = new ArrayList<DTOOrder>();

        Iterator<Order> iteratorOrder = orders.iterator();
            
        while (iteratorOrder.hasNext()) {
        		
           	Order order = iteratorOrder.next();
           	
           	DTOOrder dtoorder = order.convertToDTOOrder();

        	if ( dtoorder.getCustomer() != (long) 0 ) {
        		
        		User customer = userService.getUserByID( dtoorder.getCustomer() );

        		dtoorder.setCustomerEmail( customer.getEmail() );
        		dtoorder.setCustomerOrganisation( customer.getOrganisation() );
        	}

        	if ( dtoorder.getEditor() != (long) 0 ) {
        		
        		User editor = userService.getUserByID( dtoorder.getEditor() );

        		dtoorder.setEditorEmail( editor.getEmail() );
        	}

        	if ( dtoorder.getSupplier() != (long) 0 ) {
        		
        		User supplier = userService.getUserByID( dtoorder.getSupplier() );

        		dtoorder.setSupplierEmail( supplier.getEmail() );
        	}

        	dtoorders.add( dtoorder );

        }
    	
        model.addAttribute("orders", dtoorders);

        return "combinedorderslist";
    }

    /*
     * CRUD for Order - DELETE
     */
    @RequestMapping(value = { "/delete-combined-order-{oid}" }, 
    		method = RequestMethod.GET)
    public String deleteOrder(@PathVariable String oid, 
    		ModelMap model) {
    	
        LOGGER.debug("Processing /delete-combined-order-oid - deleteOrder()");

        Order Order = serviceorder.findByOid( ObjectConverter.convert(oid, Long.class) );

    	try {
			
    		serviceorder.delete( Order );
		} 
    	catch (ExceptionOrderNotFound e) {
    		
			e.printStackTrace();
		}
        
        model.addAttribute("message", "Order " + Order.getOid() + " DELETED successfully");

        model.addAttribute("loggedin", whoIsLoggedIn());

    	if ( determinePrivilege() == 5 ) {

            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", true);
        }
        if (determinePrivilege() == 4 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("adminBool", true);
        }
        if ( determinePrivilege() == 3 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("customerBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("editorBool", true);
        }
        if ( determinePrivilege() == 2 ) {
        	
            model.addAttribute("publicBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("customerBool", true);
        }
        if ( determinePrivilege() == 1 ) {
        	
            model.addAttribute("customerBool", false);
        	model.addAttribute("editorBool", false);
        	model.addAttribute("adminBool", false);
        	model.addAttribute("superBool", false);
        	model.addAttribute("publicBool", true);
        }

        return "combinedordersuccess";
    }
     
    
    
    /*
     * HELPER Routines
     */
    private List<User> listByPrivilege( int privilege ) {
    
        List<User> users = userService.getUsers();
        List<User> usersPrivileged = new ArrayList<User>();

    	Iterator<User> iteratorUser = users.iterator();
        
        while (iteratorUser.hasNext()) {
    		
        	User user = iteratorUser.next();
        	
        	if ( user.determinePrivilege() == privilege ) {
        		
        		usersPrivileged.add( user );
        	}
        }

        Collections.sort(usersPrivileged, new User.OrderByIdAsc());
        
        return usersPrivileged;
    }
    
    
    private User getNullUser() {
        
    	return userService.findUserByEmail("");
    }
    
    
    private int determinePrivilege() {
    	
        LOGGER.debug("Processing - determinePrivilege()");

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        return user.determinePrivilege();
    }
     

    private String whoIsLoggedIn() {
    	
        LOGGER.debug("Processing - whoIsLoggedIn()");

        if ( SecurityContextHolder.getContext().getAuthentication().getPrincipal() != null) {
        	
        	User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        	return user.getFirstName() + " " + user.getLastName();
        }
        else {
        
            return "";
        }
    }
        
    private Long whoIsLoggedInOid() {
    	
        LOGGER.debug("Processing - whoIsLoggedInOid()");

        if ( SecurityContextHolder.getContext().getAuthentication().getPrincipal() != null) {
        	
        	User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        	return user.getId();
        }
        else {
        
            return (long) 0;
        }
    }
        

    /*
     * Binder for Dates
     */
    @InitBinder
    public void initBinderDate(WebDataBinder webDataBinder) {
    	
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
     
    	dateFormat.setLenient(false);
     
    	webDataBinder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
     }


    private SimpleMailMessage constructEmail(String subject, String body) {
        
    	final SimpleMailMessage email = new SimpleMailMessage();
        
    	email.setSubject(subject);
        email.setText(body);
        email.setTo(env.getProperty("support.emailTo"));
        email.setFrom(env.getProperty("support.emailFrom"));
        
        return email;
    }

    private String getShowUrl(HttpServletRequest request, String oid) {
    	
    	return "http://" + request.getServerName() + ":" + request.getServerPort() + "/show-combined-order-" + oid;
    }

    /**
     * This setter method should only be used by unit tests
     * @param serviceline
     */
    protected void setServiceLine(ServiceLine serviceline) {
        this.serviceline = serviceline;
    }
}
