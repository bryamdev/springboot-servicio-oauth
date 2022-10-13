package com.micro.springboot.app.oauth.services;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.micro.springboot.app.commons.usuarios.models.entity.Usuario;
import com.micro.springboot.app.oauth.clients.UsuarioFeignClient;

@Service
public class UsuarioService implements UserDetailsService, IUsuarioService{

	private Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private UsuarioFeignClient usuarioFeignClient;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		Usuario usuario = usuarioFeignClient.findByUsername(username);
		
		if(usuario == null) {
			log.error(String.format("Error en el login, usuario '%s' no existe en el sistema", username));
			throw new UsernameNotFoundException(
					String.format("Error en el login, usuario '%s' no existe en el sistema", username));
		}
		
		log.info("Usuario autenticado: " + username);
		
		List<GrantedAuthority> authorities = usuario.getRoles()
				.stream()
				.map(role -> new SimpleGrantedAuthority(role.getNombre()))
				.peek(authority -> log.info("Role: " + authority.getAuthority()))
				.collect(Collectors.toList());
		
		return new User(usuario.getUsername(), 
						usuario.getPassword(), 
						usuario.getEnabled(), 
						true, 
						true, 
						true, 
						authorities);
	}

	@Override
	public Usuario findByUsername(String username) {
		return usuarioFeignClient.findByUsername(username);
	}
	
	
	
}
