package br.com.daciosoftware.shop.product.service;

import br.com.daciosoftware.shop.exceptions.exceptions.ProductIdentifieViolationException;
import br.com.daciosoftware.shop.exceptions.exceptions.ProductNotFoundException;
import br.com.daciosoftware.shop.exceptions.exceptions.ReportPdfException;
import br.com.daciosoftware.shop.modelos.dto.product.CategoryDTO;
import br.com.daciosoftware.shop.modelos.dto.product.ProductDTO;
import br.com.daciosoftware.shop.modelos.dto.product.ProductReportRequestDTO;
import br.com.daciosoftware.shop.modelos.entity.product.Category;
import br.com.daciosoftware.shop.modelos.entity.product.Product;
import br.com.daciosoftware.shop.product.repository.ProductRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Font.FontStyle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ProductService {

	private static final int SCALE_PERC_LOGO = 50;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private CategoryService categoryService;

	public List<ProductDTO> findAll() {
		return productRepository.findAll()
				.stream()
				.sorted(Comparator.comparing(Product::getId))
				.map(ProductDTO::convert)
				.collect(Collectors.toList());
	}

	public List<ProductDTO> findByNome(String name) {
		return productRepository.findByNomeContainingIgnoreCaseOrderById(name)
				.stream()
				.map(ProductDTO::convert)
				.collect(Collectors.toList());
	}

	public ProductDTO findById(Long id) {
		return productRepository.findById(id)
				.map(ProductDTO::convert)
				.orElseThrow(ProductNotFoundException::new);
	}

	public ProductDTO findByIdentifier(String productIdentifie) {
		return productRepository.findByIdentifier(productIdentifie)
				.map(ProductDTO::convert)
				.orElseThrow(ProductNotFoundException::new);
	}

	public void validIdentifierIsUnique(String productIdentifie, Long id) {

		Optional<ProductDTO> productDTO = productRepository.findByIdentifier(productIdentifie).map(ProductDTO::convert);
		if (productDTO.isPresent()) {
			if (id == null) {
				throw new ProductIdentifieViolationException();
			} else if (!id.equals(productDTO.get().getId())) {
				throw new ProductIdentifieViolationException();
			}
		}
	}

	public List<ProductDTO> findByCategory(Long categoryId) {
		return productRepository.findByCategory(categoryId)
				.stream().map(ProductDTO::convert)
				.collect(Collectors.toList());
	}

	public Page<ProductDTO> findAllPageable(Pageable pageable) {
		return productRepository.findAll(pageable).map(ProductDTO::convert);
	}

	public ProductDTO save(ProductDTO productDTO) {

		validIdentifierIsUnique(productDTO.getIdentifier(), null);
		CategoryDTO categoryDTO = categoryService.findById(productDTO.getCategory().getId());
		productDTO.setCategory(categoryDTO);

		Product product = Product.convert(productDTO);

		return ProductDTO.convert(productRepository.save(product));
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

		if (productDTO.getNome() != null) {
			boolean isNomeAlterado =  !(productDTO.getNome().equals(product.getNome()));
			if(isNomeAlterado) product.setNome(productDTO.getNome());
		}

		if (productDTO.getDescricao() != null) {
			boolean isDescricaoAlterado = !(productDTO.getDescricao().equals(product.getDescricao()));
			if (isDescricaoAlterado) product.setDescricao(productDTO.getDescricao());
		}

		if (productDTO.getPreco() != null) {
			boolean isPrecoAlterado = !(productDTO.getPreco().equals(product.getPreco()));
			if (isPrecoAlterado) product.setPreco(productDTO.getPreco());
		}

		if (productDTO.getIdentifier() != null) {
			boolean isProductIdentifierAlterado = !(productDTO.getIdentifier().equals(product.getIdentifier()));
			if (isProductIdentifierAlterado) {
				validIdentifierIsUnique(productDTO.getIdentifier(), productId);
				product.setIdentifier(productDTO.getIdentifier());
			}
		}

		if (productDTO.getCategory() != null) {
			boolean isCategoryAlterada = !(productDTO.getCategory().getId().equals(product.getCategory().getId()));
			if (isCategoryAlterada) {
				CategoryDTO categoryDTO = categoryService.findById(productDTO.getCategory().getId());
				product.setCategory(Category.convert(categoryDTO));
			}
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

	public ByteArrayOutputStream geraReportPdf(ProductReportRequestDTO productDTO) {

		Document document = new Document();
		document.setMargins(20, 20, 30, 30);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		List<ProductDTO> products = findProductsReportPdf(productDTO);

		try {
			PdfWriter.getInstance(document, outputStream);
			document.open();
			addHeaderReport(document);

			PdfPTable table = new PdfPTable(4);
			table.setWidthPercentage(100);
			float[] widths = {35, 35, 15, 15};
			table.setWidths(widths);

			addTableHeader(table);
			addRows(table, products);
			addFooterRows(table, products);
			document.add(table);

			document.close();

			return outputStream;

		} catch (DocumentException | IOException | URISyntaxException e) {

			throw new ReportPdfException();
		}
	}

	private void addHeaderReport(Document document) throws URISyntaxException, IOException, DocumentException {

		PdfPTable tableHeader = new PdfPTable(3);
		tableHeader.setWidthPercentage(100);
		float[] widths = {15, 70, 15};
		tableHeader.setWidths(widths);

		PdfPCell pdfPCellImg =  getLogo().isPresent() ? new PdfPCell(getLogo().get()) : new PdfPCell();
		pdfPCellImg.setBorderWidth(0);
		tableHeader.addCell(pdfPCellImg);

		Font fontTitle = new Font(FontFamily.HELVETICA, 18, FontStyle.BOLD.ordinal());
		PdfPCell pdfPCellTitulo = new PdfPCell(new Phrase("Relatório de Produtos", fontTitle));
		pdfPCellTitulo.setBorderWidth(0);
		pdfPCellTitulo.setHorizontalAlignment(Element.ALIGN_CENTER);
		pdfPCellTitulo.setVerticalAlignment(Element.ALIGN_MIDDLE);

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

	private Optional<Image> getLogo()  {
		URL logoResource = this.getClass().getClassLoader().getResource("static/images/logo.png");
		if (logoResource == null) {
			return Optional.empty();
		}
		try {
			Image img = Image.getInstance(logoResource.toURI().toString());
			img.scalePercent(SCALE_PERC_LOGO);
			return Optional.of(img);
		} catch (Exception e) {
			return Optional.empty();
		}
	}
}
