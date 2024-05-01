package br.com.daciosoftware.shop.product;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.daciosoftware.shop.exceptions.exceptions.ProductNotFoundException;
import br.com.daciosoftware.shop.modelos.dto.ProductDTO;
import br.com.daciosoftware.shop.product.controller.ProductController;
import br.com.daciosoftware.shop.product.service.ProductService;

@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {
	
	@InjectMocks
	private ProductController productController;
	
	@Mock
	private ProductService productService;
	
	private MockMvc mockMvc;
	
	@BeforeEach
	public void setup() {
		mockMvc = MockMvcBuilders
				.standaloneSetup(productController)
				.defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
				.build();
	}
	
	@Test
	public void testFindAll() throws Exception {
		
		ObjectMapper mapper = new ObjectMapper();
		String responseJson = mapper.writeValueAsString(ProductReposytoryMock.getListProductsDTO());
		
		Mockito.when(productService.findAll()).thenReturn(ProductReposytoryMock.getListProductsDTO());
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/product");
		
		MvcResult result = mockMvc
				.perform(request)
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn();
		
		Assertions.assertEquals(responseJson, result.getResponse().getContentAsString());
		
	}
	
	@Test
	public void testFindById() throws Exception {
		
		Long productId = 1L;
		
		ProductDTO productDTO = ProductReposytoryMock.getProductDTOFilterById(productId);

		ObjectMapper mapper = new ObjectMapper();
		String responseJson = mapper.writeValueAsString(productDTO);
		
		Mockito.when(productService.findById(productId)).thenReturn(productDTO);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/product/{productId}", productId);
		
		MvcResult result = mockMvc
				.perform(request)
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn();
		
		Assertions.assertEquals(responseJson, result.getResponse().getContentAsString());
		
	}
	
	@Test
	public void testFindById_Throw_ProductNotFoudException() throws Exception {
		
		Long productId = 8000L;
		
		Mockito.when(productService.findById(productId))
		.thenThrow(ProductNotFoundException.class);
		
		MvcResult result = mockMvc.perform(get("/product/{productId}", productId)).andReturn();
		
		System.err.println(result.getResponse().getContentAsString());
		
		Assertions.assertEquals("Produto não encontrado", result.getResponse().getContentAsString());
	}
	

}
