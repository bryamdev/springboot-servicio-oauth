package com.micro.springboot.app.oauth.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserDetailsService detailsService;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	//Configura los permisos que tendran los endpoints de AuthServer (generar token, validar token, etc)
	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		//tokenKeyAccess: endpoint para generar token "/oauth/token", "permitAll()" para definirlo publico
		//... osea que el cliente no tiene que estar autenticado
		//checkTokenAccess: endpoint para validar token, "isAuthenticated()" metodo que valida que el cliente
		//... debe estar autenticado.
		//Nota: ambos endpoint protegidos por autenticacion Basic (deben enviarse credenciales del cliente)
		security
			.tokenKeyAccess("permitAll()")
			.checkTokenAccess("isAuthenticated()");
	}
	
	//Configura los clientes/apps (angular, react, movil, etc) que se authenticaran
	//Oauth2 standar (doble autenticacion):
	// - Cliente proporciona sus credenciales de aplicacion (client, secret)
	// - El usuario se autentica con sus credenciales (username y password)
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {		
		//scopes: permisos de la aplicacion cliente
		//authorizedGrantTypes: tipo de concesion, 'password' la autenticacion de usuario va a se por pass.
		//refresh_token: token que permite obtener un nuevo token de acceso cuando el inicial esta por vencer.
		clients.inMemory()
			.withClient("frontendapp")
			.secret(passwordEncoder.encode("12345"))
			.scopes("read", "write")
			.authorizedGrantTypes("password", "refresh_token")
			.accessTokenValiditySeconds(3600)
			.refreshTokenValiditySeconds(3600);
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		//AccesTokenConverter: componente que se encarga de guardar los datos del usuario en el token(claims)
		//TokenStore: componente que se encarga de generar el token con datos generados con el AccesTokenConverter
		endpoints
			.authenticationManager(authenticationManager)
			.tokenStore(tokenStore())
			.accessTokenConverter(accesTokenConverter());
	}
	
	@Bean
	public JwtTokenStore tokenStore() {
		return new JwtTokenStore(accesTokenConverter());
	}

	@Bean
	public JwtAccessTokenConverter accesTokenConverter() {		
		JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter();
		accessTokenConverter.setSigningKey("key_provicional");
		return accessTokenConverter;
	}	
	
}
