package com.micro.springboot.app.oauth.auth;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import com.micro.springboot.app.commons.usuarios.models.entity.Usuario;
import com.micro.springboot.app.oauth.services.IUsuarioService;

//TokenEnhancer: interfaz que permite agregar datos al token
@Component
public class AdditionalTokenInfo implements TokenEnhancer {
	
	@Autowired
	private IUsuarioService usuarioService;
	
	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		
		Map<String, Object> info = new HashMap<>();
		
		Usuario usuario = this.usuarioService.findByUsername(authentication.getName());
		
		if(usuario == null) {
			System.out.println("Usuario no encontrado!");
			return accessToken;
		}
		
		info.put("nombre", usuario.getNombre());
		info.put("apellido", usuario.getApellido());
		info.put("correo", usuario.getEmail());
		
		System.out.println("Agregando informacion al token");
		
		//Se realiza down casting hacia un tipo concreto que contenga metodo para agregar info adicional al token.
		((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(info);
		
		return accessToken;
	}

	
}
