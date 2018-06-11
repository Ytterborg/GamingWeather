package com.gamingweather.gamingweather.Domain;

import javax.persistence.*;
import javax.persistence.GenerationType;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cityId;

    @NotNull
    @Size(min=2,max=60)
    private String cityName;

    @ManyToMany(mappedBy = "cities")
    private Set<GWUser> gwUsers = new HashSet<>();

    public City(){
    }

    public City(String cityName) {
        this.cityName = cityName;
    }

    public Long getCityId() {
        return cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Set<GWUser> getGwUsers() {
        return gwUsers;
    }

    public void setGwUsers(Set<GWUser> gwUsers) {
        this.gwUsers = gwUsers;
    }
}
