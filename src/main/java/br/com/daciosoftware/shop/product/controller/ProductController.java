package br.com.daciosoftware.shop.product.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.daciosoftware.shop.product.dto.ProductDTO;
import br.com.daciosoftware.shop.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {

	private final ProductService productService;
	
	@GetMapping
	public List<ProductDTO> getAll() {
		return productService.getAll();
	}
	
	@GetMapping("/category/{category}")
	public List<ProductDTO> getProductByCategory(@PathVariable Long categoryId) {
		return productService.getProductByCategoryId(categoryId);
	}
	
	@GetMapping("/{productIdentifier}")
	public ProductDTO findByIdentifier(@PathVariable String productIdentifier) {
		return productService.getProductByProductIdentifier(productIdentifier);
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ProductDTO newProduct(@RequestBody @Valid ProductDTO productDTO) {
		return productService.save(productDTO);
	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete (@PathVariable Long id) {
		productService.delete(id);
	}
	
	@PatchMapping("/{id}")
	public ProductDTO editProduct(@PathVariable Long id, @RequestBody ProductDTO productDTO) {
		return productService.editProduct(id, productDTO);
	}
	
	@GetMapping("/pageable")
	public Page<ProductDTO> getAllPage(Pageable pageable) {
		return productService.getAllPage(pageable);
	}
	
}
