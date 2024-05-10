package br.com.daciosoftware.shop.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import br.com.daciosoftware.shop.modelos.entity.product.Category;

@Repository
@EnableJpaRepositories
public interface CategoryRepository extends JpaRepository<Category, Long>{

}
