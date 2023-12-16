package br.com.daciosoftware.shop.product.entity;

import br.com.daciosoftware.shop.product.dto.CategoryDTO;
import br.com.daciosoftware.shop.product.dto.ProductDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name="product")
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String nome;
	private String descricao;
	private Float preco;
	private String productIdentifier;
	
	@ManyToOne
	@JoinColumn(name="category_id")
	private Category category;
	
	public static Product convert(ProductDTO productDTO) {		
		Product product = new Product();
		product.setId(productDTO.getId());
		product.setNome(productDTO.getNome());
		product.setDescricao(productDTO.getDescricao());
		product.setPreco(productDTO.getPreco());
		product.setProductIdentifier(productDTO.getProductIdentifier());
		CategoryDTO categoryDTO = productDTO.getCategory(); 
		Category category = Category.convert(categoryDTO);
		product.setCategory(category);
		return product;
	}

	@Override
	public String toString() {
		return "Product [id=" + id + ", nome=" + nome + ", descricao=" + descricao + ", preco=" + preco
				+ ", productIdentifier=" + productIdentifier + ", category=" + category + "]";
	}
	
}
