package com.micro.springboot.app.oauth.auth.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

//Clase que ejecuta logica en un intento de autenticacion exitosa o fallida
@Component
public class AuthenticationSuccessErrorHandler implements AuthenticationEventPublisher{

	private Logger log = LoggerFactory.getLogger(getClass());
	
	@Override
	public void publishAuthenticationSuccess(Authentication authentication) {
		
		//Evalua si la autenticacion es del cliente y no del user, no ejectute nada del evento success
		if(authentication.getDetails() instanceof WebAuthenticationDetails) {
			return;
		}
		
		UserDetails userDetails = (UserDetails)authentication.getPrincipal();
		String msg = "Succes login: " + userDetails.getUsername();
		log.info(msg);
		System.out.println(msg);
	}

	@Override
	public void publishAuthenticationFailure(AuthenticationException exception, Authentication authentication) {
		String msg = "Error en login: " + exception.getMessage();
		log.error(msg);
		System.out.println(msg);
	}
	
	
}
