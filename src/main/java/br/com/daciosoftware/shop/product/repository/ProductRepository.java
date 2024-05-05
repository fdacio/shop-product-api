package br.com.daciosoftware.shop.product.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.daciosoftware.shop.modelos.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
	
	@Query(value="select p from product p where p.productIdentifier = :productIdentifier")
	Optional<Product> findByProductIdentifier(@Param("productIdentifier") String productIdentifier);
	
	@Query(value="select p from product p where p.category.id = :categoryId")
	List<Product> findByCategory(@Param("categoryId") Long categoryId);

	List<Product> findByNomeContainingIgnoreCaseOrderById(String nome);
	
	List<Product> findByIdGreaterThan(Long id);	

}
