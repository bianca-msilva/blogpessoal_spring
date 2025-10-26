package com.generation.blogpessoal.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.model.UsuarioLogin;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.security.JwtService;


// Realiza todo o processamento, com base em regras especificadas
@Service
public class UsuarioService {
	
	// Injeções de dependência
	
	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	// Métodos CRUD
	public List<Usuario> getAll() {
		return usuarioRepository.findAll();
	}
	
	public Optional<Usuario> getById(Long id) {
		return usuarioRepository.findById(id);
	}
	
	// Verificar se o usuário já tem cadastro, senão a security não saberá qual usuário autenticará
	public Optional<Usuario> cadastrarUsuario(Usuario usuario){
		
		if (usuarioRepository.findByUsuario(usuario.getUsuario()).isPresent()) {
			return Optional.empty();
		}
		
		// Atualizar com a senha criptografada
		// Se não achou o suuário, então grava ele:
		usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
		usuario.setId(null);
		
		return Optional.of(usuarioRepository.save(usuario));
		
	}
	
	
	public Optional<Usuario> atualizarUsuario(Usuario usuario){
		
		// Atualizar checando o usuário e ID
		if (!usuarioRepository.findById(usuario.getId()).isPresent()) {
			return Optional.empty(); // Indica para a controladora que não conseguiu criar o usuário pois ele já existe
		}
		
		Optional<Usuario> usuarioExistente = usuarioRepository.findByUsuario(usuario.getUsuario());
		
		if (usuarioExistente.isPresent() && !usuarioExistente.get().getId().equals(usuario.getId())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário já existe!", null);
		}
		
		usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
		 
		return Optional.of(usuarioRepository.save(usuario));
		
	}
	
	public Optional<UsuarioLogin> autenticarUsuario(Optional<UsuarioLogin> usuarioLogin){
		
		
		if(!usuarioLogin.isPresent()) {
			return Optional.empty();
		}
		
		UsuarioLogin login = usuarioLogin.get();
		
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(login.getUsuario(), login.getSenha()));
			return usuarioRepository.findByUsuario(login.getUsuario())
					.map(usuario -> construirRespostaLogin(login, usuario));
		}catch(Exception e) {
			return Optional.empty();
		}
		
	}
	
	private UsuarioLogin construirRespostaLogin(UsuarioLogin usuarioLogin, Usuario usuario) {
		 usuarioLogin.setId(usuario.getId());
		 usuarioLogin.setNome(usuario.getNome());
		 usuarioLogin.setFoto(usuario.getFoto());
		 usuarioLogin.setSenha("");
		 usuarioLogin.setToken(gerarToken(usuario.getUsuario()));
		 return usuarioLogin;
	 }
	
	 private String gerarToken(String usuario) {
		 return "Bearer " + jwtService.generateToken(usuario);
	 }

}
