package main.java.org.baeldung.web.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import main.java.org.baeldung.validation.PasswordMatches;
import main.java.org.baeldung.validation.ValidEmail;
import main.java.org.baeldung.validation.ValidPassword;

@PasswordMatches
public class UserDto {
    @NotNull
    @Size(min = 1)
    private String firstName;

    @NotNull
    @Size(min = 1)
    private String lastName;

    @NotNull
    @Size(min = 1)
    private String organisation;

    @ValidPassword
    private String password;

    @NotNull
    @Size(min = 1)
    private String matchingPassword;

    @ValidEmail
    @NotNull
    @Size(min = 1)
    private String email;

    private boolean using2FA;

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    private Integer role;

    public Integer getRole() {
        return role;
    }

    public void setRole(final Integer role) {
        this.role = role;
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

    public String getMatchingPassword() {
        return matchingPassword;
    }

    public void setMatchingPassword(final String matchingPassword) {
        this.matchingPassword = matchingPassword;
    }

    public boolean isUsing2FA() {
        return using2FA;
    }

    public boolean getUsing2FA() {
        return using2FA;
    }

    public void setUsing2FA(boolean using2FA) {
        this.using2FA = using2FA;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("UserDto [firstName=").append(firstName).append(", lastName=").append(lastName).append(", organisation=").append(organisation)
        	.append(", password=").append(password).append(", matchingPassword=").append(matchingPassword).append(", email=").append(email)
        	.append(", isUsing2FA=").append(using2FA).append(", role=").append(role).append("]");
        return builder.toString();
    }

}
