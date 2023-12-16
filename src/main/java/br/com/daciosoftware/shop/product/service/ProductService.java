package br.com.daciosoftware.shop.product.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import br.com.daciosoftware.shop.product.dto.ProductDTO;
import br.com.daciosoftware.shop.product.entity.Category;
import br.com.daciosoftware.shop.product.entity.Product;
import br.com.daciosoftware.shop.product.repository.CategoryRepository;
import br.com.daciosoftware.shop.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;
	private final CategoryService categoryService;
	
	public List<ProductDTO> getAll() {		
		List<Product> produtos = productRepository.findAll();		
		return produtos
				.stream()
				.map(ProductDTO::convert)
				.collect(Collectors.toList());
	}
	
	public List<ProductDTO> getProductByCategoryId(Long categoryId) {		
		List<Product> produtos = productRepository.getProductByCategory(categoryId);		
		return produtos
				.stream()
				.map(ProductDTO::convert)
				.collect(Collectors.toList());
	}
	
	public ProductDTO getProductByProductIdentifier(String productIdentifie) {
		Product product = productRepository.findByProductIdentifier(productIdentifie);
		if (product != null) {
			return ProductDTO.convert(product);
		} else {
			throw new RuntimeException("Produto não encontrado");
		}
	}
	
	public ProductDTO save(@RequestBody ProductDTO productDTO) {
		Product product = Product.convert(productDTO);
		Long categoryId = productDTO.getCategory().getId();
		Category category = categoryService.getById(categoryId);
		product.setCategory(category);
		return ProductDTO.convert(productRepository.save(product));
	}
	
	
	public void delete (Long productId) {
		Optional<Product> product = productRepository.findById(productId);
		if (product.isPresent()) {
			productRepository.delete(product.get());
		} else {
			throw new RuntimeException("Produto não encontrado");
		}
	}
	
	public ProductDTO editProduct(Long productId, ProductDTO productDTO) {
		Optional<Product> productOptional = productRepository.findById(productId);
		if (productOptional.isPresent()) {
			Product product = productOptional.get(); 
			if ((productDTO.getNome() != null) && !(productDTO.getNome().equals(product.getNome()))) {
				product.setNome(productDTO.getNome());
			}
			if ((productDTO.getDescricao() != null) && !(productDTO.getDescricao().equals(product.getDescricao()))) {
				product.setDescricao(productDTO.getDescricao());
			}
			if ((productDTO.getPreco() != null) && !(productDTO.getPreco().equals(product.getPreco()))) {
				product.setPreco(productDTO.getPreco());
			}
			productRepository.save(Product.convert(productDTO));
			return ProductDTO.convert(product);
		} else {
			throw new RuntimeException("Produto não encontrado");
		}
	}
	
	public Page<ProductDTO> getAllPage(Pageable page) {
		Page<Product> usuarios = productRepository.findAll(page);
		return usuarios.map(ProductDTO::convert);
	}
}