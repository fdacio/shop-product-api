package br.com.daciosoftware.shop.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.daciosoftware.shop.product.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>{

}
