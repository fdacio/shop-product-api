package br.com.daciosoftware.shop.product.service;

import br.com.daciosoftware.shop.exceptions.exceptions.CategoryNotFoundException;
import br.com.daciosoftware.shop.modelos.dto.product.CategoryDTO;
import br.com.daciosoftware.shop.modelos.entity.product.Category;
import br.com.daciosoftware.shop.product.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;

	public List<CategoryDTO> findAll() {
		List<Category> categorias = categoryRepository.findAll();
		return categorias.stream().map(CategoryDTO::convert).collect(Collectors.toList());
	}

	public CategoryDTO findById(Long id) {
		Optional<Category> categoryOptional = categoryRepository.findById(id);
		categoryOptional.orElseThrow(CategoryNotFoundException::new);
		return CategoryDTO.convert(categoryOptional.get());
	}

	public CategoryDTO save(CategoryDTO categoryDTO) {
		Category category = Category.convert(categoryDTO);
		return CategoryDTO.convert(categoryRepository.save(category));
	}

	public void delete(Long id) {
		categoryRepository.delete(Category.convert(findById(id)));
	}

}
