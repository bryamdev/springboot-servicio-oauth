package com.micro.springboot.app.oauth.services;

import com.micro.springboot.app.commons.usuarios.models.entity.Usuario;

public interface IUsuarioService {
	
	public Usuario findByUsername(String username);
	
}
