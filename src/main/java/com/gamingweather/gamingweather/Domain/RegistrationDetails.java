package com.gamingweather.gamingweather.Domain;

import javax.persistence.*;
import java.util.Date;

@Entity
public class RegistrationDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long registrationDetailsId;

    @Temporal(TemporalType.DATE)
    private Date registrationDate;

    @Temporal(TemporalType.TIME)
    private Date registrationTime;

    public RegistrationDetails(){
    }

    public RegistrationDetails(Date registrationDate, Date registrationTime) {
        this.registrationDate = registrationDate;
        this.registrationTime = registrationTime;
    }

    public Long getRegistrationDetailsId() {
        return registrationDetailsId;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public Date getRegistrationTime() {
        return registrationTime;
    }
}
