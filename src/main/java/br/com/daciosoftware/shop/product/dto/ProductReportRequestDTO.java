package br.com.daciosoftware.shop.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProductReportRequestDTO {

	private String descricao;
	private CategoryDTO category;
	private Float precoInicial;
	private Float precoFinal;
}
