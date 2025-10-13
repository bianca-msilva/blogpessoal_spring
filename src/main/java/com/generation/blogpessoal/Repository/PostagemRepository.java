package com.generation.blogpessoal.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.generation.blogpessoal.model.Postagem;

//Com qual model (tabela) essa Interface irá trabalhar, Long como a chave primária, no nosso caso é o id
public interface PostagemRepository extends JpaRepository<Postagem, Long>{
	
	
}
