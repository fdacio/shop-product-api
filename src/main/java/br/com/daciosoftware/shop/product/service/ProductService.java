package br.com.daciosoftware.shop.product.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.daciosoftware.shop.exceptions.ProductNotFoundException;
import br.com.daciosoftware.shop.modelos.dto.CategoryDTO;
import br.com.daciosoftware.shop.modelos.dto.ProductDTO;
import br.com.daciosoftware.shop.modelos.entity.Category;
import br.com.daciosoftware.shop.modelos.entity.Product;
import br.com.daciosoftware.shop.product.repository.ProductRepository;

@Service
public class ProductService {

	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private CategoryService categoryService;

	public List<ProductDTO> findAll() {
		return productRepository.findAll()
				.stream()
				.map(ProductDTO::convert)
				.collect(Collectors.toList());
	}

	
	public List<ProductDTO> findByNome(String name) {
		return productRepository.findByNomeContainingIgnoreCase(name)
				.stream()
				.map(ProductDTO::convert)
				.collect(Collectors.toList());
	}

	public ProductDTO findById(Long id) {
		return productRepository.findById(id)
				.map(ProductDTO::convert)
				.orElseThrow(ProductNotFoundException::new);
	}

	public ProductDTO findProductByProductIdentifier(String productIdentifie) {
		return productRepository.findByProductIdentifier(productIdentifie)
				.map(ProductDTO::convert)
				.orElseThrow(ProductNotFoundException::new);
	}

	public List<ProductDTO> findProductsByCategory(Long categoryId) {
		return productRepository.findProductsByCategory(categoryId)
				.stream()
				.map(ProductDTO::convert)
				.collect(Collectors.toList());
	}

	public Page<ProductDTO> findAllPageable(Pageable pageable) {
		return productRepository.findAll(pageable).map(ProductDTO::convert);
	}

	public ProductDTO save(ProductDTO productDTO) {
		CategoryDTO category = categoryService.findById(productDTO.getCategory().getId());
		productDTO.setCategory(category);
		Product product = Product.convert(productDTO);
		product = productRepository.save(product);
		return ProductDTO.convert(product);
	}

	public void delete(Long productId) {
		productRepository.delete(Product.convert(findById(productId)));
	}

	public ProductDTO update(Long productId, ProductDTO productDTO) {
		Product product = Product.convert(findById(productId));
		if ((productDTO.getNome() != null) && !(productDTO.getNome().equals(product.getNome()))) {
			product.setNome(productDTO.getNome());
		}
		if ((productDTO.getDescricao() != null) && !(productDTO.getDescricao().equals(product.getDescricao()))) {
			product.setDescricao(productDTO.getDescricao());
		}
		if ((productDTO.getPreco() != null) && !(productDTO.getPreco().equals(product.getPreco()))) {
			product.setPreco(productDTO.getPreco());
		}
		if ((productDTO.getProductIdentifier() != null)
				&& !(productDTO.getProductIdentifier().equals(product.getProductIdentifier()))) {
			product.setProductIdentifier(productDTO.getProductIdentifier());
		}
		if ((productDTO.getCategory() != null)
				&& !(productDTO.getCategory().getId().equals(product.getCategory().getId()))) {
			CategoryDTO categoryDTO = categoryService.findById(productDTO.getCategory().getId());
			product.setCategory(Category.convert(categoryDTO));
		}
		product = productRepository.save(product);
		return ProductDTO.convert(product);
	}

}
