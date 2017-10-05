package main.java.org.baeldung.spring;

import java.util.Locale;

import main.java.org.baeldung.validation.EmailValidator;
import main.java.org.baeldung.validation.PasswordMatchesValidator;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import com.roslin.mwicks.spring.narf.converter.OidToAntibodyReferenceConverter;
import com.roslin.mwicks.spring.narf.converter.OidToBirdOrderLineConverter;
import com.roslin.mwicks.spring.narf.converter.OidToBirdOrderLineDateFormatConverter;
import com.roslin.mwicks.spring.narf.converter.OidToBirdOrderLineSexConverter;
import com.roslin.mwicks.spring.narf.converter.OidToEggOrderLineConverter;
import com.roslin.mwicks.spring.narf.converter.OidToEggOrderLineFertilisedConverter;
import com.roslin.mwicks.spring.narf.converter.OidToEmbryoOrderLineConverter;
import com.roslin.mwicks.spring.narf.converter.OidToEmbryoOrderLineIncubationConverter;
import com.roslin.mwicks.spring.narf.converter.OidToLineMhcConverter;
import com.roslin.mwicks.spring.narf.converter.OidToLineReferenceConverter;
import com.roslin.mwicks.spring.narf.converter.OidToLineResistantConverter;
import com.roslin.mwicks.spring.narf.converter.OidToLineSusceptibleConverter;
import com.roslin.mwicks.spring.narf.converter.OidToOrderCollectionConverter;
import com.roslin.mwicks.spring.narf.converter.OidToOrderStatusConverter;
import com.roslin.mwicks.spring.narf.converter.OidToOrderTypeConverter;
import com.roslin.mwicks.spring.narf.converter.OidToStrainReferenceConverter;
import com.roslin.mwicks.spring.narf.converter.OidToStrainUseConverter;
import com.roslin.mwicks.spring.narf.converter.OidToOrganismConverter;


@Configuration
@ComponentScan(basePackages = { "main.java.org.baeldung.web,com.roslin.mwicks.spring.narf.service" })
@EnableWebMvc
public class MvcConfig extends WebMvcConfigurerAdapter {

    private static final String VIEW_RESOLVER_PREFIX = "/resources/jsp/";
    private static final String VIEW_RESOLVER_SUFFIX = ".jsp";

    private static final String VIEW_CONTROLLER_LOGIN = "/login";
    private static final String VIEW_CONTROLLER_REGISTRATION = "/registration.html";
    private static final String VIEW_CONTROLLER_REGISTRATION_CAPTCHA = "/registrationCaptcha.html";
    private static final String VIEW_CONTROLLER_LOGOUT = "/logout.html";
    private static final String VIEW_CONTROLLER_HOMEPAGE = "/home.html";
    //private static final String VIEW_CONTROLLER_HOMEPAGE = "/homepage.html";
    private static final String VIEW_CONTROLLER_EXPIRED_ACCOUNT = "/expiredAccount.html";
    private static final String VIEW_CONTROLLER_BAD_USER = "/badUser.html";
    private static final String VIEW_CONTROLLER_EMAIL_ERROR = "/emailError.html";
    private static final String VIEW_CONTROLLER_HOME = "/home.html";
    private static final String VIEW_CONTROLLER_INVALID_SESSION = "/invalidSession.html";
    private static final String VIEW_CONTROLLER_CONSOLE = "/console.html";
    private static final String VIEW_CONTROLLER_ADMIN = "/admin.html";
    private static final String VIEW_CONTROLLER_SUCCESS_REGISTER = "/successRegister.html";
    private static final String VIEW_CONTROLLER_FORGET_PASSWORD = "/forgetPassword.html";
    private static final String VIEW_CONTROLLER_UPDATE_PASSWORD = "/updatePassword.html";
    private static final String VIEW_CONTROLLER_CHANGE_PASSWORD = "/changePassword.html";
    private static final String VIEW_CONTROLLER_USERS = "/users.html";
    private static final String VIEW_CONTROLLER_QRCODE = "/qrcode.html";
    
    
    public MvcConfig() {
        super();
    }

    //

    @Override
    public void addViewControllers(final ViewControllerRegistry registry) {
    	
        super.addViewControllers(registry);
        
        registry.addViewController("/").setViewName("forward:/login");
        registry.addViewController(VIEW_CONTROLLER_LOGIN);
        registry.addViewController(VIEW_CONTROLLER_REGISTRATION);
        registry.addViewController(VIEW_CONTROLLER_REGISTRATION_CAPTCHA);
        registry.addViewController(VIEW_CONTROLLER_LOGOUT);
        registry.addViewController(VIEW_CONTROLLER_HOMEPAGE);
        registry.addViewController(VIEW_CONTROLLER_EXPIRED_ACCOUNT);
        registry.addViewController(VIEW_CONTROLLER_BAD_USER);
        registry.addViewController(VIEW_CONTROLLER_EMAIL_ERROR);
        registry.addViewController(VIEW_CONTROLLER_HOME);
        registry.addViewController(VIEW_CONTROLLER_INVALID_SESSION);
        registry.addViewController(VIEW_CONTROLLER_CONSOLE);
        registry.addViewController(VIEW_CONTROLLER_ADMIN);
        registry.addViewController(VIEW_CONTROLLER_SUCCESS_REGISTER);
        registry.addViewController(VIEW_CONTROLLER_FORGET_PASSWORD);
        registry.addViewController(VIEW_CONTROLLER_UPDATE_PASSWORD);
        registry.addViewController(VIEW_CONTROLLER_CHANGE_PASSWORD);
        registry.addViewController(VIEW_CONTROLLER_USERS);
        registry.addViewController(VIEW_CONTROLLER_QRCODE);
    }

    @Override
    public void configureDefaultServletHandling(final DefaultServletHandlerConfigurer configurer) {
    	
        configurer.enable();
    }

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        
    	registry.addResourceHandler("/resources/**").addResourceLocations("/", "/resources/");
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        
    	final LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        
    	localeChangeInterceptor.setParamName("lang");
        
        registry.addInterceptor(localeChangeInterceptor);
    }

    // beans

    @Bean
    public LocaleResolver localeResolver() {
    	
        final CookieLocaleResolver cookieLocaleResolver = new CookieLocaleResolver();
        cookieLocaleResolver.setDefaultLocale(Locale.ENGLISH);
        return cookieLocaleResolver;
    }

    // @Bean
    // public MessageSource messageSource() {
    // final ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
    // messageSource.setBasename("classpath:messages");
    // messageSource.setUseCodeAsDefaultMessage(true);
    // messageSource.setDefaultEncoding("UTF-8");
    // messageSource.setCacheSeconds(0);
    // return messageSource;
    // }

    @Bean
    public EmailValidator usernameValidator() {
        
    	return new EmailValidator();
    }

    @Bean
    public PasswordMatchesValidator passwordMatchesValidator() {
        
    	return new PasswordMatchesValidator();
    }

    @Bean
    @ConditionalOnMissingBean(RequestContextListener.class)
    public RequestContextListener requestContextListener() {
        
    	return new RequestContextListener();
    }
    
    
    
    
    @Bean
    public ViewResolver viewResolver() {
    	
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();

        viewResolver.setViewClass(JstlView.class);
        viewResolver.setPrefix(VIEW_RESOLVER_PREFIX);
        viewResolver.setSuffix(VIEW_RESOLVER_SUFFIX);

        return viewResolver;
    }
    
    
    @Bean
    public MultipartResolver multipartResolver() {
    	
        return new StandardServletMultipartResolver();
    }
    

    @Override
    public void addFormatters(FormatterRegistry formatterRegistry) {

    	formatterRegistry.addConverter(getMyConverterAntibodyReference());
    	
    	formatterRegistry.addConverter(getMyConverterLineReference());
        formatterRegistry.addConverter(getMyConverterLineMhc());
        formatterRegistry.addConverter(getMyConverterLineResistant());
        formatterRegistry.addConverter(getMyConverterLineSusceptible());
    	
        formatterRegistry.addConverter(getMyConverterStrainReference());
    	formatterRegistry.addConverter(getMyConverterStrainUse());
        
    	formatterRegistry.addConverter(getMyOrderCollectionConverter());
    	formatterRegistry.addConverter(getMyOrderStatusConverter());
    	formatterRegistry.addConverter(getMyOrderTypeConverter());
        
    	formatterRegistry.addConverter(getMyBirdOrderLineSexConverter());
    	formatterRegistry.addConverter(getMyBirdOrderLineDateFormatConverter());

    	formatterRegistry.addConverter(getMyEggOrderLineFertilisedConverter());
        formatterRegistry.addConverter(getMyEmbryoOrderLineIncubationConverter());
        
    	formatterRegistry.addConverter(getMyBirdOrderLineConverter());
        formatterRegistry.addConverter(getMyEggOrderLineConverter());
        formatterRegistry.addConverter(getMyEmbryoOrderLineConverter());
    }


    @Bean
    public OidToOrganismConverter getMyConverterOrganism() { return new OidToOrganismConverter(); }
    @Bean
    public OidToAntibodyReferenceConverter getMyConverterAntibodyReference() { return new OidToAntibodyReferenceConverter(); }
    @Bean
    public OidToStrainReferenceConverter getMyConverterStrainReference() { return new OidToStrainReferenceConverter(); }
    @Bean
    public OidToStrainUseConverter getMyConverterStrainUse() { return new OidToStrainUseConverter(); }
    @Bean
    public OidToLineReferenceConverter getMyConverterLineReference() { return new OidToLineReferenceConverter(); }
    @Bean
    public OidToLineMhcConverter getMyConverterLineMhc() { return new OidToLineMhcConverter(); }
    @Bean
    public OidToLineResistantConverter getMyConverterLineResistant() { return new OidToLineResistantConverter(); }
    @Bean
    public OidToLineSusceptibleConverter getMyConverterLineSusceptible() { return new OidToLineSusceptibleConverter(); }
    @Bean
    public OidToBirdOrderLineSexConverter getMyBirdOrderLineSexConverter() { return new OidToBirdOrderLineSexConverter(); }
    @Bean
    public OidToEggOrderLineFertilisedConverter getMyEggOrderLineFertilisedConverter() { return new OidToEggOrderLineFertilisedConverter(); }
    @Bean
    public OidToEmbryoOrderLineIncubationConverter getMyEmbryoOrderLineIncubationConverter() { return new OidToEmbryoOrderLineIncubationConverter(); }
    @Bean
    public OidToBirdOrderLineConverter getMyBirdOrderLineConverter() { return new OidToBirdOrderLineConverter(); }
    @Bean
    public OidToEggOrderLineConverter getMyEggOrderLineConverter() { return new OidToEggOrderLineConverter(); }
    @Bean
    public OidToEmbryoOrderLineConverter getMyEmbryoOrderLineConverter() { return new OidToEmbryoOrderLineConverter(); }
    @Bean
    public OidToBirdOrderLineDateFormatConverter getMyBirdOrderLineDateFormatConverter() { return new OidToBirdOrderLineDateFormatConverter(); }
    @Bean
    public OidToOrderCollectionConverter getMyOrderCollectionConverter() { return new OidToOrderCollectionConverter(); }
    @Bean
    public OidToOrderStatusConverter getMyOrderStatusConverter() { return new OidToOrderStatusConverter(); }
    @Bean
    public OidToOrderTypeConverter getMyOrderTypeConverter() { return new OidToOrderTypeConverter(); }
    
    

}