package com.generation.blogpessoal.util;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import com.generation.blogpessoal.model.UsuarioLogin;

//Gerar um token de Jwt
public class JwtHelper {
	
	// Não instanciar fora da classe, construtor vazio
	private JwtHelper() {}
	
	// Pegar token para validar requisições
	// TestRestTemplate - Classe do Java que cria requisições HTTP
	public static String obterToken(TestRestTemplate testRestTemplate, String usuario, String senha) {
		
		UsuarioLogin usuarioLogin = TestBuilder.criarUsuarioLogin(usuario, senha);
		
		// Criar o Corpo da Requisição HTTP para enviar ao RestTemplate
		HttpEntity<UsuarioLogin> requisicao = new HttpEntity<>(usuarioLogin);
		
		// Usar test rest para Enviar a requisição à API da aplicação, como o Insomnia faz
		// Guarda a resposta dentro de "response"
		// (enviar para onde, tipo da requisição, corpo da requisição, o que espero receber)
		ResponseEntity<UsuarioLogin> resposta = testRestTemplate.exchange("/usuarios/logar", HttpMethod.POST, requisicao, UsuarioLogin.class);
		
		// Abrir/pegar o corpo da resposta para Pegar op Token
		UsuarioLogin corpoResposta = resposta.getBody();
		
		if (corpoResposta != null && corpoResposta.getToken() != null){
			return corpoResposta.getToken();
		}
		
		// Se der problema no if
		throw new RuntimeException("Falha no login: " + usuario);
	}
	
	
	// Criar uma Requisição com Token anexo
	// T - Tipagem da requisição HTTP criada de acordo com o tipo que você deseja
	public static <T> HttpEntity<T> criarRequisicaoComToken(T corpo, String token){
		
		HttpHeaders cabecalho = new HttpHeaders();
		
		// Validação do token - somente parte criptografada (sem o "Bearer")
		// Começa a pegar a partir do 8° caracter do token, ignorando até o 7°
		String tokenLimpo = token.startsWith("Bearer ") ? token.substring(7): token;
		cabecalho.setBearerAuth(tokenLimpo);
		return new HttpEntity<>(corpo,cabecalho);
	}
	
	// Variação do método quando não há corpo na requisição (dois métodos com mesmo nome porém com assinaturas diferentes, Polimorfismo de Sobrecarga
	// O método retorna algo, requisição
	// Overload não precisa por esta na mesma Classe
	public static HttpEntity<Void> criarRequisicaoComToken(String token){
		
		// A Requisição que não retorna nada 
		return criarRequisicaoComToken(null, token);
		
	}
	
}
