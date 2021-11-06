package com.zweaver.statistics.config;

import java.util.ArrayList;
import java.util.List;

import com.zweaver.statistics.entity.UserEntity;
import com.zweaver.statistics.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    UserRepository userRepository;

    private BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

	boolean shouldAuthenticateAgainstThirdPartySystem = true;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String name = authentication.getName();
		String password = authentication.getCredentials().toString();

        UserEntity userEntity = userRepository.findByUsername(name);

		if (userEntity != null && passwordEncoder().matches(password, userEntity.getPassword())) {
			final List<GrantedAuthority> grantedAuths = new ArrayList<>();
			grantedAuths.add(new SimpleGrantedAuthority(name)); // set role for specific user name
            grantedAuths.add(new SimpleGrantedAuthority("USER")); // set generic user role
			final UserDetails principal = new User(name, password, grantedAuths);
			final Authentication auth = new UsernamePasswordAuthenticationToken(principal, password, grantedAuths);
			return auth;
		} else {
			return null;
		}

	}

	@Override
	public boolean supports(Class<?> authentication) {

		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

}
