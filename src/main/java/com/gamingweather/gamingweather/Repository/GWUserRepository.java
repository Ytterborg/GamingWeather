package com.gamingweather.gamingweather.Repository;

import com.gamingweather.gamingweather.Domain.GWUser;
import org.springframework.data.repository.CrudRepository;

public interface GWUserRepository extends CrudRepository<GWUser,Long> {
    GWUser findByUsername(String username);
    GWUser findByEmail(String email);
}
