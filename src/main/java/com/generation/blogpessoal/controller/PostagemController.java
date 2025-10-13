package com.generation.blogpessoal.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.generation.blogpessoal.Repository.PostagemRepository;
import com.generation.blogpessoal.model.Postagem;

@RestController
@RequestMapping("/postagens")

//Autorizar o backend (API) as requisições vindo de outros servidores
@CrossOrigin(origins = "*", allowedHeaders = "*")  // Liberar solicitações de qualquer origem e liberar o cabeçalho também (token de segurança)
public class PostagemController {
	// Trazer a Repository aqui e seus métodos (que interagem com o DB), IOC (inversão de controles) - transferir a responsabilidade de criar a instância do Objeto e gerenciá-lo para o Spring
	@Autowired
	private PostagemRepository postagemRepository; 
	
	// Resposta HTTP
	//<Tipo que quero retornar<Tipo atual da lista>>
	@GetMapping
	public ResponseEntity<List<Postagem>> getAll(){
		return ResponseEntity.ok(postagemRepository.findAll());  // SELECT * FROM tb_postagens;
		
	}
	
}
