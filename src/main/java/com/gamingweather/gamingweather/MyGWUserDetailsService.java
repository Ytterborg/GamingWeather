package com.gamingweather.gamingweather;

import com.gamingweather.gamingweather.Domain.GWUser;
import com.gamingweather.gamingweather.Repository.GWUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyGWUserDetailsService implements UserDetailsService{

    @Autowired
    private GWUserRepository gwUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        GWUser gwUser = gwUserRepository.findByUsername(username);
        if (gwUser == null) {
            throw new UsernameNotFoundException(username);
        }
        return new MyGWUserPrincipal(gwUser);
    }


}
