package rpp.mf.gov;

import java.util.Optional;

import jakarta.data.repository.Find;
import jakarta.data.repository.Repository;
import jakarta.data.repository.Save;



@Repository
public interface UserRepo {

    @Save
    void save(User user);

    @Find
    Optional<User> findUserByName(String name);

}
