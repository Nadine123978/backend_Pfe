package com.itbulls.nadine.spring.springbootdemo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.itbulls.nadine.spring.springbootdemo.model.Group;
import com.itbulls.nadine.spring.springbootdemo.model.User;
import com.itbulls.nadine.spring.springbootdemo.repository.UserRepository;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority; 

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    	 User user = userRepository.findByEmail(username);
    	    if (user == null) {
    	        throw new UsernameNotFoundException("User not found");
    	    }

        List<GrantedAuthority> authorities = new ArrayList<>();

        Group group = user.getGroup();
        if (group != null && group.getName() != null) {
            // نحول اسم المجموعة إلى ROLE_XXX لصلاحيات Spring Security
            String roleName = "ROLE_" + group.getName().toUpperCase();
            authorities.add(new SimpleGrantedAuthority(roleName));
        } else {
            // إذا ما في مجموعة، ممكن تعطي صلاحية افتراضية أو ترفض
            throw new UsernameNotFoundException("User has no group assigned");
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }
}
