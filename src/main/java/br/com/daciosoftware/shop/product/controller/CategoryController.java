package br.com.daciosoftware.shop.product.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.daciosoftware.shop.modelos.dto.CategoryDTO;
import br.com.daciosoftware.shop.product.service.CategoryService;

@RestController
@RequestMapping("/category")
public class CategoryController {

	@Autowired
	private CategoryService categoryService;
	
	@GetMapping
	public List<CategoryDTO> getAll() {		
		return categoryService.findAll();		
	}
	
	@GetMapping("/{id}")
	public CategoryDTO findById(@PathVariable Long id) {
		return categoryService.findById(id);
	}
	
}
