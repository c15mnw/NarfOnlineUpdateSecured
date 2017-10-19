package main.java.org.baeldung.spring;

import java.util.Locale;

import main.java.org.baeldung.validation.EmailValidator;
import main.java.org.baeldung.validation.PasswordMatchesValidator;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
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

    public MvcConfig() {

    	super();
    }

    @Override
    public void addViewControllers(final ViewControllerRegistry registry) {
    	
        super.addViewControllers(registry);
        
        registry.addViewController("/").setViewName("forward:/login");
        registry.addViewController("/login");
        registry.addViewController("/registration.html");
        registry.addViewController("/registrationCaptcha.html");
        registry.addViewController("/logout.html");
        registry.addViewController("/home.html");
        registry.addViewController("/expiredAccount.html");
        registry.addViewController("/badUser.html");
        registry.addViewController("/emailError.html");
        registry.addViewController("/home.html");
        registry.addViewController("/invalidSession.html");
        registry.addViewController("/console.html");
        registry.addViewController("/admin.html");
        registry.addViewController("/successRegister.html");
        registry.addViewController("/forgetPassword.html");
        registry.addViewController("/updatePassword.html");
        registry.addViewController("/changePassword.html");
        registry.addViewController("/users.html");
        registry.addViewController("/qrcode.html");
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

    
    /**
     * Handles favicon.ico requests assuring no <code>404 Not Found</code> error is returned.
     */
    @Controller
    static class FaviconController {
        @RequestMapping("favicon.ico")
        String favicon() {
            return "forward:/resources/images/favicon.ico";
        }
    }

}