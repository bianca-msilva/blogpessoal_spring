package com.generation.blogpessoal.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.generation.blogpessoal.model.Postagem;
import com.generation.blogpessoal.repository.PostagemRepository;
import com.generation.blogpessoal.repository.TemasRepository;

import jakarta.validation.Valid;

// Indica que é a Controller de nossa API Rest
@RestController
@RequestMapping("/postagens")
//Autorizar o backend (API) as requisições vindo de outros servidores
@CrossOrigin(origins = "*", allowedHeaders = "*")  // Liberar solicitações de qualquer origem e liberar o cabeçalho também (token de segurança)
public class PostagemController {
	// Trazer a Repository aqui e seus métodos (que interagem com o DB), IOC (inversão de controles) - transferir a responsabilidade de criar a instância do Objeto e gerenciá-lo para o Spring
	@Autowired
	private PostagemRepository postagemRepository;
	
	@Autowired
	private TemasRepository temaRepository;
	
	// Resposta HTTP
	//<Tipo que quero retornar<Tipo atual da lista>>
	@GetMapping
	public ResponseEntity<List<Postagem>> getAll(){
		return ResponseEntity.ok(postagemRepository.findAll());  // SELECT * FROM tb_postagens;
		
	}
	
	@GetMapping("/{id}") // Variável de caminho, ou seja, está no endereço
	public ResponseEntity<Postagem> getById(@PathVariable Long id){
		// Pegar o valor no endereço e passar para a variável id
		return postagemRepository.findById(id)
				.map(resposta -> ResponseEntity.ok(resposta))
				.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
				// .orElse(ResponseEntity.notFound().build());
		// SELECT *FROM tb_postangens WHERE id = ?
		
	}
	
	// Procurar postagem por título, implementando o método do repository
	@GetMapping("/titulo/{titulo}")
	public ResponseEntity<List<Postagem>> getAllByTitulo(@PathVariable String titulo){
		return ResponseEntity.ok(postagemRepository.findAllByTituloContainingIgnoreCase(titulo)); 
		
	}
	
	// Para ter resposta HTTP ResponseEntity (é uma classe natural?)
	// ResquestBody É para pegar algo do corpo da requisição
	@PostMapping
	public ResponseEntity<Postagem> post(@Valid @RequestBody Postagem postagem){ // Pegar e aplicar as validações de Postagem
		
		// Validação de tema antes de adicionar a postagem
		if(temaRepository.existsById(postagem.getTema().getId())) {
			// Ter acesso ao Objeto de Postagem
			postagem.setId(null);  // Pois será no DB a criação do ID
			return ResponseEntity.status(HttpStatus.CREATED).body(postagemRepository.save(postagem));  // Sempre será retornada, mesmo se a lista estiver vazia 
		}
		
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O Tema não existe", null);
	}
	
	
	// Precisa de duas validações
	@PutMapping
	public ResponseEntity<Postagem> put(@Valid @RequestBody Postagem postagem){ // Pegar e aplicar as validações de Postagem
		
		if(postagemRepository.existsById(postagem.getId())) {
			
			// Se a postagem existe, checa se o tema existe
			if(temaRepository.existsById(postagem.getTema().getId())) {
				
				return ResponseEntity.status(HttpStatus.OK).body(postagemRepository.save(postagem));  // Sempre será retornada, mesmo se a lista estiver vazia 
			}
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O Tema não existe", null);
		}
		
		return ResponseEntity.notFound().build();
		
		// Checar se o post existe ou não		
//		return postagemRepository.findById(postagem.getId())
//				.map(resposta -> ResponseEntity.status(HttpStatus.OK).body(postagemRepository.save(postagem)))
//				.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}
	
	
	// void pois não tem retorno, o retorno é apenas para o usuário
	@ResponseStatus(HttpStatus.NO_CONTENT)  // retorno caso der certo
	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id){
		
		Optional<Postagem> postagem = postagemRepository.findById(id);
		
		if(postagem.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		
		postagemRepository.deleteById(id);
	}
}







