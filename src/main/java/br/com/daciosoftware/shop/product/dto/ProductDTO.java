package br.com.daciosoftware.shop.product.dto;

import br.com.daciosoftware.shop.product.entity.Category;
import br.com.daciosoftware.shop.product.entity.Product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {

	private Long id;
	@NotBlank(message = "Informe o nome")
	private String nome;
	@NotBlank(message = "Informe o descrição")
	private String descricao;
	@NotBlank(message = "Informe o indenticador de produto")
	private String productIdentifier;
	@NotNull(message = "Informe o preço")
	private Float preco;
	@NotNull(message = "Informe a categoria")
	private CategoryDTO category;

	public static ProductDTO convert(Product product) {
		ProductDTO productDTO = new ProductDTO();
		productDTO.setId(product.getId());
		productDTO.setNome(product.getNome());
		productDTO.setDescricao(product.getDescricao());
		productDTO.setPreco(product.getPreco());
		productDTO.setProductIdentifier(product.getProductIdentifier());
		Category category = product.getCategory();
		CategoryDTO categoryDTO = CategoryDTO.convert(category);
		productDTO.setCategory(categoryDTO);
		return productDTO;

	}

	@Override
	public String toString() {
		return "ProductDTO [id=" + id + ", nome=" + nome + ", descricao=" + descricao + ", preco=" + preco
				+ ", productIdentifier=" + productIdentifier + ", category=" + category + "]";
	}

}
