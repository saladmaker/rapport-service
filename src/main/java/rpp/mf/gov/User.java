package rpp.mf.gov;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import io.quarkus.elytron.security.common.BcryptUtil;


@Entity
@Table(name = "user_table")
public class User {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "user_name", nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private Set<String> roles = new HashSet<>();

    
    public User() {
    }

    
    public User(String name, String email, String password, Set<String> roles) {
        this.name = name;
        this.email = email;
        this.password = BcryptUtil.bcryptHash(password);
        this.roles = roles;
    }


    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void addRole(String role){
        Objects.requireNonNull(role, "role can not be null");
        roles.add(role);
    }
    
    public void removeRole(String role){
        Objects.requireNonNull(role, "role can not be null");
        roles.remove(role);
    }

}
