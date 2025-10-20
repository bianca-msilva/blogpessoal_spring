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

import com.generation.blogpessoal.model.Temas;
import com.generation.blogpessoal.repository.TemasRepository;

import jakarta.validation.Valid;

@RequestMapping("/temas")
@CrossOrigin(origins = "*", allowedHeaders = "*")  // Liberar solicitações de qualquer origem e liberar o cabeçalho também (token de segurança)
@RestController
public class TemasController {
	
	@Autowired
	private TemasRepository temasRepository;
	
	@GetMapping
	public ResponseEntity<List<Temas>> getAll(){
		return ResponseEntity.ok(temasRepository.findAll()); // temasRepository.findAll() será o que o ResponseEntity irá retornar (msg)
	}
	
	// PathVariable é para o Spring pegar o ID que veio na URL de requisição
	
	@GetMapping("/{id}")
	public ResponseEntity<Temas> getById(@PathVariable Long id){  
		
		return temasRepository.findById(id)
				.map(resposta -> ResponseEntity.ok(resposta))  // Se o tema for encontrado, será colocado nesse ResponseEntity
				.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build()); // o build() é indicando que o status não terá corpo na resposta
	}
	
	@GetMapping("/tema/{tema}")
	public ResponseEntity<List<Temas>> getAllByDescricao(@PathVariable String tema){
		
		return ResponseEntity.ok(temasRepository.findAllByDescricaoContainingIgnoreCase(tema));
	}
	
	@PostMapping
	public ResponseEntity<Temas> post(@Valid @RequestBody Temas tema){
		
		tema.setId(null); // Quem irá criar o ID é o DB
		return ResponseEntity.status(HttpStatus.CREATED).body(temasRepository.save(tema));
	}
	
	@PutMapping
	public ResponseEntity<Temas> put(@Valid @RequestBody Temas tema){
		
		return temasRepository.findById(tema.getId())
			.map(resposta -> ResponseEntity.status(HttpStatus.OK).body(temasRepository.save(tema)))
			.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}
	
	@ResponseStatus(HttpStatus.NO_CONTENT)  // Resposta caso a exclusão der certo
	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id) {
		
		// Temos que criar um Optiona pois não vem já implementado (void), como nos casos do ResponseEntity
		Optional<Temas> conferencia = temasRepository.findById(id);
		
		if(conferencia.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		
		temasRepository.deleteById(id);
	}
	
}
