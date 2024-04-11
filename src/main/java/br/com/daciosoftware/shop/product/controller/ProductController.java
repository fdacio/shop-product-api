package br.com.daciosoftware.shop.product.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.itextpdf.text.DocumentException;

import br.com.daciosoftware.shop.modelos.dto.ProductDTO;
import br.com.daciosoftware.shop.product.service.ProductService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/product")
public class ProductController {

	@Autowired
	private ProductService productService;
	
	@GetMapping
	public List<ProductDTO> getAll() {
		return productService.findAll();
	}
	
	@GetMapping("/{id}")
	public ProductDTO findById(@PathVariable Long id) {
		return productService.findById(id);
	}
	
	@GetMapping("/category/{categoryId}")
	public List<ProductDTO> findProductsByCategory(@PathVariable Long categoryId) {
		return productService.findProductsByCategory(categoryId);
	}
	
	@GetMapping("/{productIdentifier}/identifier")
	public ProductDTO findByIdentifier(@PathVariable String productIdentifier) {
		return productService.findProductByProductIdentifier(productIdentifier);
	}
	
	@GetMapping("/search")
	public List<ProductDTO> findByNone(@RequestParam(name = "nome", required = true) String nome) {
		return productService.findByNome(nome);
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ProductDTO save(@Valid @RequestBody ProductDTO productDTO) {
		return productService.save(productDTO);
	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete (@PathVariable Long id) {
		productService.delete(id);
	}
	
	@PatchMapping("/{id}")
	public ProductDTO update(@PathVariable Long id, @Valid @RequestBody ProductDTO productDTO) {
		return productService.update(id, productDTO);
	}
	
	@GetMapping("/pageable")
	public Page<ProductDTO> findAllPageable(Pageable pageable) {
		return productService.findAllPageable(pageable);
	}
	
	@PostMapping("/report-pdf")
	public ResponseEntity<?> reportPdf(@RequestBody ProductDTO productDTO) {
		
		List<ProductDTO> products = productService.findProductsReportPdf(productDTO);
		
		try {
			
	        ByteArrayOutputStream pdfStream = productService.geraReportPdf(products);
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_PDF);
	        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=products.pdf");
	        headers.setContentLength(pdfStream.size());
	        
	        return new ResponseEntity<>(pdfStream.toByteArray(), headers, HttpStatus.OK);
	        
		} catch (DocumentException | IOException | URISyntaxException e) {

			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(e.getMessage());
		}	
		
	}
}
