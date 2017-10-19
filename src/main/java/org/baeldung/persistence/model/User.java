package main.java.org.baeldung.persistence.model;

import java.util.Collection;
import java.util.Comparator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.jboss.aerogear.security.otp.api.Base32;
import org.springframework.transaction.annotation.Transactional;


@Entity
@Table(name = "nuser_user_account")
@Transactional
public class User implements Comparable<User>{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String organisation;

    @Column(length = 60)
    private String password;

    private boolean enabled;

    private boolean using2FA;

    private String secret;

    //

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "nuser_users_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Collection<Role> roles;

    public User() {
        super();
        this.secret = Base32.random();
        this.enabled = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String username) {
        this.email = username;
    }

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(final String organisation) {
        this.organisation = organisation;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public Collection<Role> getRoles() {
        return roles;
    }

    public void setRoles(final Collection<Role> roles) {
        this.roles = roles;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public boolean getUsing2FA() {
        return using2FA;
    }

    public boolean isUsing2FA() {
        return using2FA;
    }

    public void setUsing2FA(boolean using2FA) {
        this.using2FA = using2FA;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getFirstAndLastName() {
        return firstName + " " + lastName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((email == null) ? 0 : email.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final User user = (User) obj;
        if (!email.equals(user.email)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("User [id=").append(id).append(", firstName=").append(firstName).append(", lastName=").append(lastName).append(", organisation=").append(organisation).append(", email=").append(email).append(", password=").append(password).append(", enabled=").append(enabled).append(", isUsing2FA=")
                .append(using2FA).append(", secret=").append(secret).append(", roles=").append(roles).append("]");
        return builder.toString();
    }
    

    public int determinePrivilege() {
    	
        int level = 0;
        
        if ( this.getRoles() == null ) {
        	
        	return 0;
        }
        
        for ( Role role : this.getRoles()) {

        	if ( role.getName().equals("ROLE_SUPER")) {

        		level = 5;
            }
        	if ( role.getName().equals("ROLE_ADMIN")) {
                
        		level = 4;
            }
        	if ( role.getName().equals("ROLE_EDITOR")) {
                
        		level = 3;
            }
        	if ( role.getName().equals("ROLE_CUSTOMER")) {
                
        		level = 2;
            }
        	if ( role.getName().equals("ROLE_PUBLIC")) {
            
        		level = 1;
            } 
        }

        return level;
    }



    public int compareTo(User o) {
        return this.getId() > o.getId() ? 1 : (this.getId() < o.getId() ? -1 : 0);
    }


    
    public static class OrderByIdAsc implements Comparator<User> {

        public int compare(User user_o1, User user_o2) {

        	return user_o1.id.compareTo(user_o2.id);
        }
    }
    public static class OrderByIdDesc implements Comparator<User> {

        public int compare(User user_o1, User user_o2) {

        	return user_o2.id.compareTo(user_o1.id);
        }
    }

}