package main.java.org.baeldung.spring;

import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import main.java.org.baeldung.Application;

import javax.servlet.*;

public class DataJPAInitializer implements WebApplicationInitializer {
    
    private static final String DISPATCHER_SERVLET_NAME = "dispatcher";
    private static final String DISPATCHER_SERVLET_MAPPING = "/";
    private static final String SINGLE_SESSION = "singleSession";
    private static final String TRUE_STRING = "true";
    private static final String OPEN_ENTITY_MANAGER_IN_VIEW_FILTER = "openEntityManagerInViewFilter";
    
    
    public void onStartup(ServletContext servletContext) throws ServletException {
    	
        AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();

        rootContext.register(Application.class);
        rootContext.setServletContext(servletContext);
        
        ServletRegistration.Dynamic dispatcher = servletContext.addServlet(DISPATCHER_SERVLET_NAME, new DispatcherServlet(rootContext));
        
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping(DISPATCHER_SERVLET_MAPPING);

        FilterRegistration.Dynamic filter = servletContext.addFilter(OPEN_ENTITY_MANAGER_IN_VIEW_FILTER, OpenEntityManagerInViewFilter.class);
        
        filter.setInitParameter(SINGLE_SESSION, TRUE_STRING);
        filter.addMappingForServletNames(null, true, DISPATCHER_SERVLET_NAME);

        /**
         * Add Spring ContextLoaderListener
        servletContext.addListener(new ContextLoaderListener(rootContext));
         */
    }
}
