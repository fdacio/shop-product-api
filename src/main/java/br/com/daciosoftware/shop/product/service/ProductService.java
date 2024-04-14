package br.com.daciosoftware.shop.product.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Font.FontStyle;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import br.com.daciosoftware.shop.exceptions.ProductIdentifieViolationException;
import br.com.daciosoftware.shop.exceptions.ProductNotFoundException;
import br.com.daciosoftware.shop.product.dto.CategoryDTO;
import br.com.daciosoftware.shop.product.dto.ProductDTO;
import br.com.daciosoftware.shop.product.dto.ProductReportRequestDTO;
import br.com.daciosoftware.shop.product.entity.Category;
import br.com.daciosoftware.shop.product.entity.Product;
import br.com.daciosoftware.shop.product.repository.ProductRepository;

@Service
public class ProductService {

	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private CategoryService categoryService;

	public List<ProductDTO> findAll() {
		return productRepository.findAll().stream().map(ProductDTO::convert).collect(Collectors.toList());
	}

	public List<ProductDTO> findByNome(String name) {
		return productRepository.findByNomeContainingIgnoreCase(name).stream().map(ProductDTO::convert)
				.collect(Collectors.toList());
	}

	public ProductDTO findById(Long id) {
		return productRepository.findById(id).map(ProductDTO::convert).orElseThrow(ProductNotFoundException::new);
	}

	public ProductDTO findProductByProductIdentifier(String productIdentifie) {
		return productRepository.findByProductIdentifier(productIdentifie).map(ProductDTO::convert)
				.orElseThrow(ProductNotFoundException::new);
	}

	public boolean checkIdentifierExistInUpdate(String productIdentifie, Product product) {
		
		Optional<Product> productOther = productRepository.findByProductIdentifier(productIdentifie);
		
		return (productOther.isPresent() && product.getId() != productOther.get().getId());
	}
	
	public List<ProductDTO> findByCategory(Long categoryId) {
		CategoryDTO category = categoryService.findById(categoryId);		
		return productRepository.findByCategory(Category.convert(category)).stream().map(ProductDTO::convert)
				.collect(Collectors.toList());
	}

	public Page<ProductDTO> findAllPageable(Pageable pageable) {
		return productRepository.findAll(pageable).map(ProductDTO::convert);
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

	public void deleteMaiorQue(Long productId) {
		List<Product> productsDelete = productRepository.findByIdGreaterThan(productId);
		productRepository.deleteAllInBatch(productsDelete);
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
			
			if (checkIdentifierExistInUpdate(productDTO.getProductIdentifier(), product)) { 
				throw new ProductIdentifieViolationException();
			}
			
			product.setProductIdentifier(productDTO.getProductIdentifier());
			
		}
		if ((productDTO.getCategory() != null)
				&& !(productDTO.getCategory().getId().equals(product.getCategory().getId()))) {
			CategoryDTO categoryDTO = categoryService.findById(productDTO.getCategory().getId());
			product.setCategory(Category.convert(categoryDTO));
		}
		
		product = productRepository.save(product);
		
		return ProductDTO.convert(product);
	}

	public List<ProductDTO> findProductsReportPdf(ProductReportRequestDTO productDTO) {

		List<Product> products = productRepository.findAll(Sort.by("nome")).stream().toList();

		if (productDTO.getCategory() != null) {
			products = products
					.stream()
					.filter(p -> p.getCategory().getId().equals(productDTO.getCategory().getId()))
					.toList();
		}
		
		if (productDTO.getPrecoInicial() != null) {
			products = products
					.stream()
					.filter(p -> p.getPreco() >= productDTO.getPrecoInicial())
					.toList();
			
		}
		
		if (productDTO.getPrecoFinal() != null) {
			products = products
					.stream()
					.filter(p -> p.getPreco() <= productDTO.getPrecoFinal())
					.toList();
			
		}

		return products.stream().map(ProductDTO::convert).collect(Collectors.toList());
	}

	public ByteArrayOutputStream geraReportPdf(List<ProductDTO> products) throws DocumentException, URISyntaxException, IOException {

		Document document = new Document();
		document.setMargins(20, 20, 30, 30);
	
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		
		PdfWriter.getInstance(document, outputStream);
		
		document.open();
		
		addHeaderReport(document);

		PdfPTable table = new PdfPTable(4);
		table.setWidthPercentage(100);
		float widths[] = {35, 35, 15, 15};
		table.setWidths(widths);
		
		addTableHeader(table);
		addRows(table, products);
		addFooterRows(table, products);
		document.add(table);
		
		document.close();
		
		return outputStream;
	}
	
	private void addHeaderReport(Document document) throws URISyntaxException, MalformedURLException, IOException, DocumentException {

		Path path = Paths.get(ClassLoader.getSystemResource("static/logo.png").toURI());
		Image img = Image.getInstance(path.toAbsolutePath().toString());
		img.scalePercent(20);
		
		PdfPTable tableHeader = new PdfPTable(3);
		tableHeader.setWidthPercentage(100);
		float widths[] = {15, 70, 15};
		tableHeader.setWidths(widths);
		
		PdfPCell pdfPCellImg = new PdfPCell(img);
		pdfPCellImg.setBorderWidth(0);

		Font fontTitle = new Font(FontFamily.HELVETICA, 18, FontStyle.BOLD.ordinal());
		PdfPCell pdfPCellTitulo = new PdfPCell(new Phrase("Relatório de Produtos", fontTitle));
		pdfPCellTitulo.setBorderWidth(0);
		pdfPCellTitulo.setHorizontalAlignment(Element.ALIGN_CENTER);
		pdfPCellTitulo.setVerticalAlignment(Element.ALIGN_MIDDLE);
		
		tableHeader.addCell(pdfPCellImg);
		tableHeader.addCell(pdfPCellTitulo);
		PdfPCell pdfPCell = new PdfPCell();
		pdfPCell.setBorderWidth(0);
		tableHeader.addCell(pdfPCell);
		
		document.add(tableHeader);

	}

	private void addTableHeader(PdfPTable table) {
		Font font = new Font(FontFamily.HELVETICA, 12, FontStyle.BOLD.ordinal());
		Stream.of("Nome", "Descriçao", "Categoria", "Preço").forEach(columnTitle -> {
			PdfPCell header = new PdfPCell();
			header.setBackgroundColor(BaseColor.LIGHT_GRAY);
			header.setBorderWidth(1);
			header.setPhrase(new Phrase(columnTitle, font));
			table.addCell(header);
		});
	}

	private void addRows(PdfPTable table, List<ProductDTO> products) {
		products.forEach(p -> {
			table.addCell(p.getNome());
			table.addCell(p.getDescricao());
			table.addCell(p.getCategory().getNome());
			PdfPCell pdfPCellPreco = new PdfPCell(new Phrase(String.format("R$ %,.2f", p.getPreco())));
			pdfPCellPreco.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(pdfPCellPreco);
		});
	}

	private void addFooterRows(PdfPTable table, List<ProductDTO> products) {
	
		Font font = new Font(FontFamily.HELVETICA, 12, FontStyle.BOLD.ordinal());
		
		PdfPCell pdfPCellLabelTotal = new PdfPCell(new Phrase("Total: ", font));
		pdfPCellLabelTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
		PdfPCell cell1 = new PdfPCell();
		table.addCell(cell1);
		table.addCell(cell1);
		table.addCell(pdfPCellLabelTotal);
		
		Float valorTotal = products.stream().map(ProductDTO::getPreco).reduce((float) 0, Float::sum);

		PdfPCell pdfPCellValorTotal = new PdfPCell(new Phrase(String.format("R$ %,.2f", valorTotal), font));
		pdfPCellValorTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(pdfPCellValorTotal);
	}

}
