package br.com.daciosoftware.shop.product.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.daciosoftware.shop.exceptions.exceptions.CategoryNotFoundException;
import br.com.daciosoftware.shop.modelos.dto.product.CategoryDTO;
import br.com.daciosoftware.shop.modelos.entity.product.Category;
import br.com.daciosoftware.shop.product.repository.CategoryRepository;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;

	public List<CategoryDTO> findAll() {
		return categoryRepository.findAll()
				.stream()
				.map(CategoryDTO::convert)
				.collect(Collectors.toList());
	}

	public CategoryDTO findById(Long id) {
		return categoryRepository.findById(id)
				.map(CategoryDTO::convert)
				.orElseThrow(CategoryNotFoundException::new);
	}

	public CategoryDTO save(CategoryDTO categoryDTO) {
		Category category = Category.convert(categoryDTO);
		return CategoryDTO.convert(categoryRepository.save(category));
	}

	public void delete(Long id) {
		categoryRepository.delete(Category.convert(findById(id)));
	}

}
