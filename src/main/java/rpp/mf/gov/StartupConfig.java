package rpp.mf.gov;

import java.util.Set;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

public class StartupConfig {
    
    @Inject
    UserRepo repo;

    public void onStartUp(@Observes StartupEvent event) {
        repo.save(new User("khaled", "df", "1994", Set.of("admin", "user")));
    }
}
