package br.com.daciosoftware.shop.product;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.daciosoftware.shop.exceptions.exceptions.ProductNotFoundException;
import br.com.daciosoftware.shop.modelos.dto.ProductDTO;
import br.com.daciosoftware.shop.modelos.entity.Product;
import br.com.daciosoftware.shop.product.repository.ProductRepository;
import br.com.daciosoftware.shop.product.service.CategoryService;
import br.com.daciosoftware.shop.product.service.ProductService;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

	@InjectMocks
	private ProductService productService;

	@Mock
	private ProductRepository productRepository;

	@Mock
	private CategoryService categoryService;

	@Test
	void testFindAll() {
		Mockito.when(productRepository.findAll()).thenReturn(ProductReposytoryMock.getListProducts());
		Assertions.assertEquals(3, productService.findAll().size());
	}

	@Test
	void testFindById() {
		
		Long id = 1L;

		Optional<Product> product = ProductReposytoryMock.getProductFilterById(id);

		Mockito.when(productRepository.findById(id)).thenReturn(product);

		ProductDTO productDTOResult = productService.findById(id);

		Assertions.assertEquals("Produto 1", productDTOResult.getNome());
	}

	@Test
	void testFindById_Throw_ProductNotFoundExcpetion() {

		Long id = 8L;//id não existente

		Optional<Product> product = ProductReposytoryMock.getProductFilterById(id);

		Mockito.when(productRepository.findById(id)).thenReturn(product);

		Assertions.assertThrowsExactly(ProductNotFoundException.class, () -> productService.findById(id));
	}

	@Test
	public void testFindByCategory() {

		Long categoryId = 1L;
		List<Product> products = ProductReposytoryMock.getProductsFilterByCategory(categoryId);

		Mockito.when(productRepository.findByCategory(categoryId)).thenReturn(products);

		Assertions.assertEquals(2, productService.findByCategory(categoryId).size());
	}

	@Test
	public void testFindByProductIdentifier() {

		String productIdentifier = "321123";

		Optional<Product> product = ProductReposytoryMock.getProductFilterByIdentifie(productIdentifier);

		Mockito.when(productRepository.findByProductIdentifier(productIdentifier)).thenReturn(product);

		ProductDTO productDTOResult = productService.findProductByProductIdentifier(productIdentifier);

		Assertions.assertEquals("Produto 2", productDTOResult.getNome());
	}
	
	@Test
	public void testFindByName() {

		String name = "Produto";

		List<Product> products = ProductReposytoryMock.getProductsFilterByName(name);

		Mockito.when(productRepository.findByNomeContainingIgnoreCase(name)).thenReturn(products);

		List<ProductDTO> productsResult = productService.findByNome(name);

		Assertions.assertEquals(3, productsResult.size());
	}

}
