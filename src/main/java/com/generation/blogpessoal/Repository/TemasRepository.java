package com.generation.blogpessoal.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.generation.blogpessoal.model.Temas;

public interface TemasRepository extends JpaRepository<Temas, Long> {
	
	// Query Method, apenas assinatura do método
	public List<Temas> findAllByDescricaoContainingIgnoreCase(String tema);
	
}
