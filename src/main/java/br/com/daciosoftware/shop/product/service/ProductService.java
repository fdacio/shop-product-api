package br.com.daciosoftware.shop.product.service;

import java.util.List;
import java.util.Optional;
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
		List<Product> produtos = productRepository.findAll();
		return produtos.stream().map(ProductDTO::convert).collect(Collectors.toList());
	}

	public ProductDTO findById(Long id) {
		Optional<Product> productOptional = productRepository.findById(id);
		productOptional.orElseThrow(ProductNotFoundException::new);
		return ProductDTO.convert(productOptional.get());
	}

	public List<ProductDTO> findProductsByCategory(Long categoryId) {
		List<Product> produtos = productRepository.findProductsByCategory(categoryId);
		return produtos.stream().map(ProductDTO::convert).collect(Collectors.toList());
	}

	public ProductDTO findProductByProductIdentifier(String productIdentifie) {
		Optional<Product> productOptional = productRepository.findByProductIdentifier(productIdentifie);
		productOptional.orElseThrow(ProductNotFoundException::new);
		return ProductDTO.convert(productOptional.get());
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
		if ((productDTO.getCategory() != null) && !(productDTO.getCategory().getId().equals(product.getCategory().getId()))) {
			CategoryDTO categoryDTO = categoryService.findById(productDTO.getCategory().getId());
			product.setCategory(Category.convert(categoryDTO));
		}
		product = productRepository.save(product);
		return ProductDTO.convert(product);

	}

	public Page<ProductDTO> findAllPageable(Pageable pageable) {
		Page<Product> produtos = productRepository.findAll(pageable);
		return produtos.map(ProductDTO::convert);
	}

	public List<ProductDTO> findByNome(String name) {
		List<Product> produtos = productRepository.findByNomeContainingIgnoreCase(name);
		return produtos.stream().map(ProductDTO::convert).collect(Collectors.toList());
	}

}
