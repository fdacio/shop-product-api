package br.com.daciosoftware.shop.product.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.daciosoftware.shop.modelos.entity.Category;
import br.com.daciosoftware.shop.modelos.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
	
	Optional<Product> findByProductIdentifier(String productIdentifier);
	
	List<Product> findByNomeContainingIgnoreCase(String nome);
	
	List<Product> findByIdGreaterThan(Long id);	

	List<Product> findByCategory(Category category);
	

}
