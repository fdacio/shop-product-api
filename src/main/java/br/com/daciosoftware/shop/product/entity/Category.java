package br.com.daciosoftware.shop.product.entity;

import br.com.daciosoftware.shop.product.dto.CategoryDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name="category")
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String nome;
	
	public static Category convert(CategoryDTO categoryDTO) {
		Category category = new Category();
		category.setId(category.getId());
		category.setNome(category.getNome());
		return category;
	}
}
