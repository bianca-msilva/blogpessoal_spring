package com.generation.blogpessoal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.generation.blogpessoal.model.Postagem;

//Com qual model (tabela) essa Interface irá trabalhar, Long como a chave primária, no nosso caso é o id
public interface PostagemRepository extends JpaRepository<Postagem, Long>{
	
	// Assinar método
	public List<Postagem> findAllByTituloContainingIgnoreCase(String titulo);
	
	// SELECT * FROM tb_postagens WHERE titulo LIKE "%?%;"
}
