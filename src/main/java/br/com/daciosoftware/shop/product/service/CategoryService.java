package br.com.daciosoftware.shop.product.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import br.com.daciosoftware.shop.product.dto.CategoryDTO;
import br.com.daciosoftware.shop.product.entity.Category;
import br.com.daciosoftware.shop.product.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

	private final CategoryRepository categoryRepository;
	
	public List<CategoryDTO> findAll() {
		List<Category> categorias = categoryRepository.findAll();
		return categorias
				.stream()
				.map(CategoryDTO::convert)
				.collect(Collectors.toList());
	}
	
	public CategoryDTO findById(Long id) {
		Optional<Category> category = categoryRepository.findById(id);
		if (category.isPresent()) {
			return CategoryDTO.convert(category.get()); 
		} else {
			throw new RuntimeException("Categoria não encontrada");
		}
	}
	
	public Category getById(Long id) {
		Optional<Category> category = categoryRepository.findById(id);
		if (category.isPresent()) {
			return category.get(); 
		} else {
			throw new RuntimeException("Categoria não encontrada");
		}
	}
}
