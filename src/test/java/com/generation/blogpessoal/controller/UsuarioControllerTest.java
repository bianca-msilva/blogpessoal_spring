package com.generation.blogpessoal.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.service.UsuarioService;
import com.generation.blogpessoal.util.JwtHelper;
import com.generation.blogpessoal.util.TestBuilder;

// Construção de testes
// Usar portas aleatórias para rodar o teste, para não dar conflito se a aplicação já estiver rodando na porta 80
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // Ciclo de vida do teste é por classe
@TestMethodOrder(MethodOrderer.DisplayName.class) // DisplayName = nome do teste através da anotação no Spring
public class UsuarioControllerTest {
	
	// Injeções de dependências
	
	@Autowired
	private TestRestTemplate testRestTemplate;
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private UsuarioRepository usuarioRepository; // Para limpar a tabela

	// Constantes
	
	private static final String BASE_URL = "/usuarios"; // Identificar o caminho da requisição
	private static final String USUARIO = "root@root.com"; // Autenticar requisições protegidas
	private static final String SENHA = "rootroot";
	
	// Método para iniciar os testes
	
	@BeforeAll // rodar isso antes dos testes
	void inicio() {
		usuarioRepository.deleteAll();
		usuarioService.cadastrarUsuario(TestBuilder.criarUsuario(null, "root", USUARIO, SENHA));
	}
	
	@Test
	@DisplayName("01 - Deve Cadastrar um novo usuário com sucesso")
	void deveCadastrarUsuario() {
		// Given - cenário do teste, o que queremos testar
		Usuario usuario = TestBuilder.criarUsuario(null, "Thuany", "thuany@email.com.br", "12345678");
		
		// When - ação principal do teste
		HttpEntity<Usuario> requisicao = new HttpEntity<Usuario>(usuario);
		ResponseEntity<Usuario> resposta = testRestTemplate.exchange(
				BASE_URL + "/cadastrar", HttpMethod.POST, requisicao, Usuario.class); // exchange = método de envio
		
		// Then - verifica resultado esperado, a resposta
		assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
		// Se veio algo no corpo da requisição
		assertNotNull(resposta.getBody());
	
	}
	
	@Test
	@DisplayName("02 - Não Deve Cadastrar um novo usuário duplicado")
	void naoDeveCadastrarUsuarioDuplicado() {
		// Given - cenário do teste, o que queremos testar
		Usuario usuario = TestBuilder.criarUsuario(null, "Rafaela L", "rafalemes@email.com", "789456123");
		usuarioService.cadastrarUsuario(usuario); // antes de criar a requisição ele já cria o usuário no DB
		
		// When - ação principal do teste
		HttpEntity<Usuario> requisicao = new HttpEntity<Usuario>(usuario);
		ResponseEntity<Usuario> resposta = testRestTemplate.exchange(
				BASE_URL + "/cadastrar", HttpMethod.POST, requisicao, Usuario.class); // exchange = método de envio
		
		// Then - verifica resultado esperado, a resposta
		assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode()); // Pois já foi criado na linha 80
		// Se veio algo no corpo da requisição
		assertNull(resposta.getBody()); // esperando corpo nulo, pois deu erro e não criou o usuário
	}

	// Teste de atualizar
	
	@Test
	@DisplayName("03 - Deve atualizar os dados de um usuário com sucesso")
	void deveAtualizarUmUsuario() {
		
		// Given - cenário do teste, o que queremos testar
		Usuario usuario = TestBuilder.criarUsuario(null, "Nadia", "nadiacaricatto@email.com.br","12345678");
		// Pois precisa pegar o ID do usuário, guarda os dados dentro da variável de Optional
		Optional<Usuario>usuarioCadastrado = usuarioService.cadastrarUsuario(usuario);
		
		Usuario usuarioUpdate = TestBuilder.criarUsuario(usuarioCadastrado.get().getId(), "Nadia Caricatto",
				"nadiacaricatto@email.com.br","abc12345");
		
		// When - ação principal do teste
		
			// Gerar token
		String token = JwtHelper.obterToken(testRestTemplate, USUARIO, SENHA);
		
		// Criar requisição com token
		HttpEntity<Usuario> requisicao = JwtHelper.criarRequisicaoComToken(usuarioUpdate, token);
		
		// Enviar requisição PUT
		ResponseEntity<Usuario> resposta = testRestTemplate.exchange(
				BASE_URL + "/atualizar", HttpMethod.PUT, requisicao, Usuario.class);
		
		// Then - verifica resultado esperado, a resposta
		assertEquals(HttpStatus.OK, resposta.getStatusCode()); // Pois já foi criado na linha 80
		// Se veio algo no corpo da requisição
		assertNotNull(resposta.getBody()); // esperando corpo nulo, pois deu erro e não criou o usuário
	}

	@Test
	@DisplayName("04 - Deve Listar todos os Usuarios com sucesso")
	void deveListarTodosUsuarios() {
		
		// Given
		// Não precisa salvar o usuário numa variável pois ela não será utilizada posteriormente
		usuarioService.cadastrarUsuario(TestBuilder.criarUsuario(null, "Maria", "maria@email.com.br","maria789"));
		usuarioService.cadastrarUsuario(TestBuilder.criarUsuario(null, "Joao", "jao@email.com.br","jao98745"));
		
		// When
		String token = JwtHelper.obterToken(testRestTemplate, USUARIO, SENHA);
		HttpEntity<Void> requisicao = JwtHelper.criarRequisicaoComToken(token);
		ResponseEntity<Usuario[]> resposta = testRestTemplate.exchange(
				BASE_URL + "/all", HttpMethod.GET, requisicao, Usuario[].class);
		
		// Then
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
		assertNotNull(resposta.getBody());

	}
	
	@Test
	@DisplayName("05 - Deve Listar Usuario por Id")
	void deveListarPorId() {
		
		// Given
		Usuario usuario = TestBuilder.criarUsuario(null, "Rosa", "rosa@email.com.br","rosa5192");
		
		Optional<Usuario>usuarioCadastrado = usuarioService.cadastrarUsuario(usuario);
		
		Long id = usuarioCadastrado.get().getId();
		
		// When
		String token = JwtHelper.obterToken(testRestTemplate, USUARIO, SENHA);
		HttpEntity<Void> requisicao = JwtHelper.criarRequisicaoComToken(token);
		ResponseEntity<Usuario> resposta = testRestTemplate.exchange(
				BASE_URL + "/" + id , HttpMethod.GET, requisicao, Usuario.class);
		
		
		// Then
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
		assertNotNull(resposta.getBody());
	}

}
