package com.gamingweather.gamingweather.Repository;

import com.gamingweather.gamingweather.Domain.City;
import org.springframework.data.repository.CrudRepository;

public interface CityRepository extends CrudRepository<City,Long> {
    City findByCityName(String cityName);
}
