package br.com.daciosoftware.shop.product.repository;

import java.util.List;
import java.util.Optional;

import br.com.daciosoftware.shop.modelos.entity.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {
	
	Optional<Product> findByIdentifier(String productIdentifier);
	List<Product> findByNomeContainingIgnoreCaseOrderById(String nome);
	@Query(value="select p from product p join category c on p.category.id = c.id where c.id = :categoryId")
	List<Product> findByCategory(@Param("categoryId") Long categoryId);
	List<Product> findByIdGreaterThan(Long id);
}
