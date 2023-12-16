package br.com.daciosoftware.shop.product.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.daciosoftware.shop.product.dto.CategoryDTO;
import br.com.daciosoftware.shop.product.entity.Category;
import br.com.daciosoftware.shop.product.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {

	private final CategoryRepository categoryRepository;
	
	@GetMapping
	public List<CategoryDTO> getAll() {		
		List<Category> produtos = categoryRepository.findAll();		
		return produtos
				.stream()
				.map(CategoryDTO::convert)
				.collect(Collectors.toList());
	}
	
	@GetMapping("/{id}")
	public CategoryDTO findById(@PathVariable Long id) {
		Optional<Category> category = categoryRepository.findById(id);
		if (category.isPresent()) {
			return CategoryDTO.convert(category.get()); 	
		} else {
			throw new RuntimeException("Categoria não encontrada");
		}
	}
	
}
