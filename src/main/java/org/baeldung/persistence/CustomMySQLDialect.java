package main.java.org.baeldung.persistence;

import org.hibernate.dialect.MySQLDialect;

public class CustomMySQLDialect extends MySQLDialect {
    @Override
    public boolean dropConstraints() {
        return false;
    }
}
