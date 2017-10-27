package main.java.org.baeldung.spring;

import main.java.org.baeldung.security.google2fa.CustomAuthenticationProvider;
import main.java.org.baeldung.security.google2fa.CustomWebAuthenticationDetailsSource;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@Configuration
@ComponentScan(basePackages = { "main.java.org.baeldung.security" })
// @ImportResource({ "classpath:webSecurityConfig.xml" })
@EnableWebSecurity
public class SecSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationSuccessHandler myAuthenticationSuccessHandler;

    @Autowired
    private LogoutSuccessHandler myLogoutSuccessHandler;

    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;

    @Autowired
    private CustomWebAuthenticationDetailsSource authenticationDetailsSource;

    public SecSecurityConfig() {
    	
        super();
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        
    	auth.authenticationProvider(authProvider());
    }

    @Override
    public void configure(final WebSecurity web) throws Exception {
        
    	web.ignoring()
            .antMatchers("/resources/**");
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        // @formatter:off
        http
            .csrf().disable()
            .authorizeRequests()
                .antMatchers(
                		"/login*",
                		"/login*", 
                		"/logout*", 
                		"/signin/**", 
                		"/signup/**",
                        "/user/registration*", 
                        "/registrationConfirm*", 
                        "/expiredAccount*", 
                        "/registration*",
                        "/badUser*", 
                        "/user/resendRegistrationToken*" ,
                        "/forgetPassword*", 
                        "/user/resetPassword*",
                        "/user/changePassword*", 
                        "/emailError*", 
                        "/resources/**",
                        "/old/user/registration*",
                        "/successRegister*",
                        "/qrcode*",
    					"/home"
                        ).permitAll()
                .antMatchers(
                		"/invalidSession*"
                		).anonymous()
                .antMatchers(
                		"/user/updatePassword*",
                		"/user/savePassword*",
                		"/updatePassword*"
                		).hasAuthority("CHANGE_PASSWORD_PRIVILEGE")
                .antMatchers(
    					"/userslist",
    					"/show-user*",
    					"/edit-user*",
    					"/delete-user*"
                		).access("hasAuthority('SUPER_PRIVILEGE')")
                .antMatchers(
    					"/show-line*",
    					"/lines-list",
    					"/show-strain*",
    					"/strains-list"
    					).access("hasAuthority('SUPER_PRIVILEGE') or hasAuthority('ADMIN_PRIVILEGE')")
                .antMatchers(
        				"/new-combined-order",
        				"/new-selected-order"
    					).access("hasAuthority('SUPER_PRIVILEGE') or hasAuthority('ADMIN_PRIVILEGE') or hasAuthority('CUSTOMER_PRIVILEGE')")
                .antMatchers(
    					"/delete-combined-order*"
    					).access("hasAuthority('SUPER_PRIVILEGE') or hasAuthority('ADMIN_PRIVILEGE')")
                .antMatchers(
    					"/my-orders-list",
    					"/my-orders-list-new",
    					"/my-orders-list-pending",
    					"/my-orders-list-closed",
    					"/my-orders-list-cancelled"
    					).access("hasAuthority('SUPER_PRIVILEGE') or hasAuthority('ADMIN_PRIVILEGE') or hasAuthority('CUSTOMER_PRIVILEGE')")
                .antMatchers(
        				"/show-combined-order*",
    					"/edit-combined-order*"
						).access("hasAuthority('SUPER_PRIVILEGE') or hasAuthority('ADMIN_PRIVILEGE') or hasAuthority('EDITOR_PRIVILEGE') or hasAuthority('CUSTOMER_PRIVILEGE')")
                .antMatchers(
    					"/orders-list",
    					"/orders-list-new",
    					"/orders-list-pending",
    					"/orders-list-closed",
    					"/orders-list-cancelled"
						).access("hasAuthority('SUPER_PRIVILEGE') or hasAuthority('ADMIN_PRIVILEGE') or hasAuthority('EDITOR_PRIVILEGE')")
                .anyRequest().hasAuthority("PUBLIC_PRIVILEGE")
                .and()
            .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/home.html")
                .failureUrl("/login?error=true")
                .successHandler(myAuthenticationSuccessHandler)
                .failureHandler(authenticationFailureHandler)
                .authenticationDetailsSource(authenticationDetailsSource)
            .permitAll()
                .and()
            .sessionManagement()
                .invalidSessionUrl("/invalidSession.html")
                .maximumSessions(1).sessionRegistry(sessionRegistry()).and()
                .sessionFixation().none()
            .and()
            .logout()
                .logoutSuccessHandler(myLogoutSuccessHandler)
                .invalidateHttpSession(false)
                .logoutSuccessUrl("/logout.html?logSucc=true")
                .deleteCookies("JSESSIONID")
                .permitAll();
    // @formatter:on
    }

    
    // beans

    @Bean
    public DaoAuthenticationProvider authProvider() {
    	
        final CustomAuthenticationProvider authProvider = new CustomAuthenticationProvider();
        
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(encoder());
        
        return authProvider;
    }

    @Bean
    public PasswordEncoder encoder() {
        
    	return new BCryptPasswordEncoder(11);
    }

    @Bean
    public SessionRegistry sessionRegistry() {
    
    	return new SessionRegistryImpl();
    }

}