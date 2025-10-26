package com.generation.blogpessoal.util;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.model.UsuarioLogin;


public class TestBuilder {

	
	// Método:  Criar objetos da classe Usuario para testá-lo conforme necessário
	public static Usuario criarUsuario(Long id, String nome, String usuario, String senha) {
		
		Usuario novoUsuario = new Usuario(); // construtor vazio
		novoUsuario.setId(id);
		novoUsuario.setNome(nome);
		novoUsuario.setUsuario(usuario);
		novoUsuario.setSenha(senha);
		novoUsuario.setFoto("-");
		
		return novoUsuario;
	}
	
	// Objeto de retorno 
	public static UsuarioLogin criarUsuarioLogin(String usuario, String senha) {
		
		UsuarioLogin novoUsuarioLogin = new UsuarioLogin();
		novoUsuarioLogin.setUsuario(usuario);
		novoUsuarioLogin.setSenha(senha);
	
		return novoUsuarioLogin;
	}
	
}
