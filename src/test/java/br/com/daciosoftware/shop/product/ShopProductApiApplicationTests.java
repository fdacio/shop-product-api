package br.com.daciosoftware.shop.product;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import br.com.daciosoftware.shop.product.controller.ProductController;

@SpringBootTest
class ShopProductApiApplicationTests {

	@Autowired
	private ProductController productController;
	
	@Test
	void contextLoads() {
		Assertions.assertThat(productController).isNotNull();
	}

}
