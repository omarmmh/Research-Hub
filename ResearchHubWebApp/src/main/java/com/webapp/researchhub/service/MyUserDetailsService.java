package com.webapp.researchhub.service;

import com.webapp.researchhub.domain.MyUser;
import com.webapp.researchhub.domain.Role;
import com.webapp.researchhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository repo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        MyUser user = repo.findByEmail(email);
        List<GrantedAuthority> authorities = new ArrayList<>();

        for(Role r : user.getRoles())
        {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + r.getName()));
        }
        return new User(user.getEmail(), user.getPassword(), true, true, true, true, authorities);
    }
}
