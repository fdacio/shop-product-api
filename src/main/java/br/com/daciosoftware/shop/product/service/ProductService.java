package br.com.daciosoftware.shop.product.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

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
		Optional<Product> product = productRepository.findById(id);
		if (product.isPresent()) {
			return ProductDTO.convert(product.get());
		} else {
			throw new RuntimeException("Produto não encontrado");
		}
	}

	public List<ProductDTO> findProductsByCategory(Long categoryId) {
		List<Product> produtos = productRepository.findProductsByCategory(categoryId);
		return produtos.stream().map(ProductDTO::convert).collect(Collectors.toList());
	}

	public ProductDTO findProductByProductIdentifier(String productIdentifie) {
		Product product = productRepository.findByProductIdentifier(productIdentifie);
		if (product != null) {
			return ProductDTO.convert(product);
		} else {
			throw new RuntimeException("Produto não encontrado");
		}
	}

	public ProductDTO save(@RequestBody ProductDTO productDTO) {
		Product product = Product.convert(productDTO);
		product.setCategory(getCategory(productDTO));
		return ProductDTO.convert(productRepository.save(product));
	}

	public void delete(Long productId) {
		Optional<Product> product = productRepository.findById(productId);
		if (product.isPresent()) {
			productRepository.delete(product.get());
		} else {
			throw new RuntimeException("Produto não encontrado");
		}
	}

	public ProductDTO update(Long productId, ProductDTO productDTO) {
		Optional<Product> productOptional = productRepository.findById(productId);
		if (productOptional.isPresent()) {
			Product product = productOptional.get();
			if ((productDTO.getNome() != null) && !(productDTO.getNome().equals(product.getNome()))) {
				product.setNome(productDTO.getNome());
			}
			if ((productDTO.getDescricao() != null) && !(productDTO.getDescricao().equals(product.getDescricao()))) {
				product.setDescricao(productDTO.getDescricao());
			}
			if ((productDTO.getDescricao() != null) && !(productDTO.getDescricao().equals(product.getDescricao()))) {
				product.setDescricao(productDTO.getDescricao());
			}
			if ((productDTO.getPreco() != null) && !(productDTO.getPreco().equals(product.getPreco()))) {
				product.setPreco(productDTO.getPreco());
			}
			if ((productDTO.getProductIdentifier() != null) && !(productDTO.getProductIdentifier().equals(product.getProductIdentifier()))) {
				product.setProductIdentifier(productDTO.getProductIdentifier());
			}
			if ((productDTO.getCategory() != null)
					&& !(productDTO.getCategory().getId().equals(product.getCategory().getId()))) {
				Category category = getCategory(productDTO);
				product.setCategory(category);
			}
			product = productRepository.save(product);
			return ProductDTO.convert(product);

		} else {
			throw new RuntimeException("Produto não encontrado");
		}
	}

	public Page<ProductDTO> findAllPageable(Pageable pageable) {
		Page<Product> produtos = productRepository.findAll(pageable);
		return produtos.map(ProductDTO::convert);
	}

	public List<ProductDTO> findByNome(String name) {
		List<Product> produtos = productRepository.findByNomeContainingIgnoreCase(name);
		return produtos.stream().map(ProductDTO::convert).collect(Collectors.toList());
	}
	
	private Category getCategory(ProductDTO productDTO) {
		Long categoryId = productDTO.getCategory().getId();
		return categoryService.getById(categoryId);
	}

}
