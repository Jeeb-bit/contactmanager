package com.Smart.manager.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.Smart.manager.Dao.UserRepository;
import com.Smart.manager.entities.User;

public class UserDetailsServiceimp implements UserDetailsService {
@Autowired	
private UserRepository repo;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	User user=repo.getUserbyUserName(username);
	if(user==null) {
		throw new UsernameNotFoundException("could not found username" );
	}
	CustomUserDetails userDetails=new  CustomUserDetails(user);
		return userDetails;
	}

}
