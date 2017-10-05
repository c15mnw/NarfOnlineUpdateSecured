package main.java.org.baeldung.persistence.dao;

import main.java.org.baeldung.persistence.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
	
    User findByEmail(String email);

    @Override
    void delete(User user);

}
