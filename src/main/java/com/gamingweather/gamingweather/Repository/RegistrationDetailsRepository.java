package com.gamingweather.gamingweather.Repository;

import com.gamingweather.gamingweather.Domain.RegistrationDetails;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;

public interface RegistrationDetailsRepository extends CrudRepository<RegistrationDetails,Long> {
    RegistrationDetails findByRegistrationTime(Date date);
}
