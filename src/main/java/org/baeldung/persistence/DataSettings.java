package main.java.org.baeldung.persistence;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "persistence")
public class DataSettings {

    private static final String TRUE = "true";
	private static final String FALSE = "false";

    private String load;
    private String email;

    public DataSettings() {
    }

    public String getLoad() {
    	
        return load;
    }

    public void setLoad(String load) {
    	
        this.load = load.toLowerCase();
    }
    
    public boolean isLoadTrue() {
    	
    	boolean outBool = false;

    	if ( this.load.equals(TRUE) ) {
    		
    		outBool = true;
    	}
    	
    	return outBool;
    }
    
    public boolean isLoadFalse() {
    	
    	boolean outBool = false;

    	if ( this.load.equals(FALSE) ) {
    		
    		outBool = true;
    	}
    	
    	return outBool;
    }
    
    public String getEmail() {
    	
        return email;
    }

    public void setEmail(String email) {
    	
        this.email = email.toLowerCase();
    }
    
    public boolean isEmailTrue() {
    	
    	boolean outBool = false;

    	if ( this.email.equals(TRUE) ) {
    		
    		outBool = true;
    	}
    	
    	return outBool;
    }
    
    public boolean isEmailFalse() {
    	
    	boolean outBool = false;

    	if ( this.email.equals(FALSE) ) {
    		
    		outBool = true;
    	}
    	
    	return outBool;
    }
}
