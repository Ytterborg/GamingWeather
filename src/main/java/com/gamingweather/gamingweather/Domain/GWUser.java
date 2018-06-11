package com.gamingweather.gamingweather.Domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
public class GWUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gwUserId;

    @NotNull
    @Size(min = 2, max = 60)
    private String username;

    @NotNull
    @Size(min=2,max=60)
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)$")
    private String email;

    @NotNull
    private String password;

    @OneToOne
    private RegistrationDetails registrationDetails;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "gwuser_city", joinColumns =
    @JoinColumn(name = "gwuser_id", referencedColumnName = "gwUserId"),
            inverseJoinColumns = @JoinColumn(name = "city_id", referencedColumnName = "cityId"))
    private Set<City> cities = new HashSet<>();

    public GWUser(){
    }

    public GWUser(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public Long getGwUserId() {
        return gwUserId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public Set<City> getCities() {
        return cities;
    }

    public void setRegistrationDetails(RegistrationDetails registrationDetails) {
        this.registrationDetails = registrationDetails;
    }

    public RegistrationDetails getRegistrationDetails() {
        return registrationDetails;
    }

    public void addCity(City city){
        this.cities.add(city);
    }
}
