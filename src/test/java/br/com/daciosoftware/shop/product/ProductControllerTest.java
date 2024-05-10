package br.com.daciosoftware.shop.product;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.daciosoftware.shop.modelos.dto.product.CategoryDTO;
import br.com.daciosoftware.shop.modelos.dto.product.ProductDTO;
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
		
		
		MvcResult result = mockMvc
				.perform(get("/product/{productId}", productId))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn();
		
		Assertions.assertEquals(responseJson, result.getResponse().getContentAsString());
		
	}
	
	@Test
	public void testFindByIdentifier() throws Exception {
		String identifier = "312346";
		
		ProductDTO productDTO = ProductReposytoryMock.getProductDTOFilterByIdentifie(identifier);

		ObjectMapper mapper = new ObjectMapper();
		String responseJson = mapper.writeValueAsString(productDTO);
		
		Mockito.when(productService.findProductByProductIdentifier(identifier)).thenReturn(productDTO);
		
		
		MvcResult result = mockMvc
				.perform(get("/product/{identifier}/identifier", identifier))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn();
		
		Assertions.assertEquals(responseJson, result.getResponse().getContentAsString());
	}

	@Test
	public void testSave() throws Exception {
		
		ProductDTO productDTO = new ProductDTO();
		productDTO.setNome("Refrigerador");
		productDTO.setDescricao("Descricao do Refrigerador ");
		productDTO.setPreco((float)1000.00);
		productDTO.setProductIdentifier("123456789");
		productDTO.setCategory(new CategoryDTO(1L, "Eletrodoméstico"));
		
		ObjectMapper mapper = new ObjectMapper();
		String productJson = mapper.writeValueAsString(productDTO);

		
		Mockito.when(productService.save(Mockito.any())).thenReturn(productDTO);
		
		
		MvcResult result = mockMvc
				.perform(post("/product")
						.contentType(MediaType.APPLICATION_JSON)
						.content(productJson))
				.andExpect(MockMvcResultMatchers.status().isCreated())
				.andReturn();
		
		Assertions.assertEquals(productJson, result.getResponse().getContentAsString());

	}
	
	@Test
	public void testSave_ValidationFields() throws Exception {
		
		ProductDTO productDTO = new ProductDTO();
		productDTO.setNome(null);
		productDTO.setDescricao(null);
		productDTO.setPreco(null);
		productDTO.setProductIdentifier(null);
		productDTO.setCategory(null);
		
		ObjectMapper mapper = new ObjectMapper();
		String productJson = mapper.writeValueAsString(productDTO);			

		mockMvc.perform(post("/product")
					.contentType(MediaType.APPLICATION_JSON)
					.content(productJson))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());

	}
}
