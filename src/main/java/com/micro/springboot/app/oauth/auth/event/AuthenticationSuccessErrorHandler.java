package com.micro.springboot.app.oauth.auth.event;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import com.micro.springboot.app.commons.usuarios.models.entity.Usuario;
import com.micro.springboot.app.oauth.services.IUsuarioService;

import feign.FeignException.FeignClientException;

//Clase que ejecuta logica en un intento de autenticacion exitosa o fallida
@Component
public class AuthenticationSuccessErrorHandler implements AuthenticationEventPublisher{

	private Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private IUsuarioService usuarioService;
	
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
		
		Usuario usuario = usuarioService.findByUsername(authentication.getName());
		
		Optional.ofNullable(usuario.getIntentos())
			.filter(intentos -> intentos >= 0)
			.ifPresent(val -> {
				log.info("Reseteando los intentos de login del usuario");
				usuario.setIntentos(0);
				usuarioService.update(usuario, usuario.getId());
			});
		
	}

	@Override
	public void publishAuthenticationFailure(AuthenticationException exception, Authentication authentication) {
		String msg = "Error en login: " + exception.getMessage();
		log.error(msg);
		System.out.println(msg);
		
		try {
			Usuario usuario = usuarioService.findByUsername(authentication.getName());
			
			if(Optional.ofNullable(usuario.getIntentos()).isEmpty()) {
				usuario.setIntentos(0);
			}
			
			log.info("Numero de intentos actuales: " + usuario.getIntentos());
			usuario.setIntentos(usuario.getIntentos() + 1);
			log.info("Nuevo numero de intentos: " + usuario.getIntentos());
			
			if(usuario.getIntentos() >= 3) {
				log.error(String.format("El usuario %s deshabilitado por maximo de intentos.", usuario.getUsername()));
				usuario.setEnabled(false);
			}
			
			usuarioService.update(usuario, usuario.getId());
			
		}catch(FeignClientException e) {
			log.error("El usuario no existe en el sistema: " + authentication.getName());
		}
		
		
		
	}
	
	
}
