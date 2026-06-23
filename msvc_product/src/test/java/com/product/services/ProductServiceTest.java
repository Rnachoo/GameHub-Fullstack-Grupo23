package com.product.services;

import com.product.clients.CategoryFeignClient;
import com.product.clients.dtos.CategoryClientDTO;
import com.product.exceptions.ProductException;
import com.product.models.Product;
import com.product.models.dtos.ProductRequestDTO;
import com.product.models.dtos.ProductResponseDTO;
import com.product.repositories.ProductRepository;
import feign.FeignException;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryFeignClient categoryFeignClient;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product productoPrueba;
    private CategoryClientDTO categoriaActivaPrueba;
    private List<Product> listaProductos = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        Faker faker = new Faker(Locale.of("es", "CL"));

        // Simulación de Categoría recibida vía Feign
        this.categoriaActivaPrueba = new CategoryClientDTO();
        this.categoriaActivaPrueba.setId(1L);
        this.categoriaActivaPrueba.setNombreCategory("Consolas");
        this.categoriaActivaPrueba.setEstado("Active");

        // Simulación de la Entidad Producto
        this.productoPrueba = new Product();
        this.productoPrueba.setId(10L);
        this.productoPrueba.setNombre("PlayStation 5");
        this.productoPrueba.setMarca("Sony");
        this.productoPrueba.setModelo("Digital Edition");
        this.productoPrueba.setPrecio(500000L);
        this.productoPrueba.setCategoriaId(1L);
        this.productoPrueba.setDescripcion("Consola de nueva generación");
        this.productoPrueba.setEstado("Activo");

        this.listaProductos.add(this.productoPrueba);

        // Generar lista para pruebas de findAll
        for (int i = 0; i < 3; i++) {
            Product p = new Product();
            p.setId((long) (i + 11));
            p.setNombre(faker.commerce().productName());
            p.setMarca(faker.commerce().brand());
            p.setModelo("Modelo X");
            p.setPrecio((long) faker.number().numberBetween(10000, 100000));
            p.setCategoriaId(1L);
            p.setEstado("Activo");
            this.listaProductos.add(p);
        }
    }

    @Test
    @DisplayName("Debe listar todos los productos registrados")
    public void shouldGetAllProducts() {
        when(this.productRepository.findAll()).thenReturn(this.listaProductos);

        List<ProductResponseDTO> result = this.productService.getAllProducts();

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(4);
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe buscar un producto por su ID exitosamente")
    public void shouldGetProductById() {
        Long id = 10L;
        when(this.productRepository.findById(id)).thenReturn(Optional.of(this.productoPrueba));

        ProductResponseDTO result = this.productService.getProductById(id);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getNombre()).isEqualTo("PlayStation 5");
        verify(productRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar producto con ID inexistente")
    public void shouldThrowExceptionWhenGetByIdNotFound() {
        Long id = 999L;
        when(this.productRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.productService.getProductById(id))
                .isInstanceOf(ProductException.class)
                .hasMessage("Producto no encontrado con ID: " + id);
    }

    @Test
    @DisplayName("Debe crear un producto validando que la categoría exista y esté activa")
    public void shouldCreateProductSuccessfully() {
        ProductRequestDTO requestDTO = new ProductRequestDTO();
        requestDTO.setNombre("Xbox Series X");
        requestDTO.setMarca("Microsoft");
        requestDTO.setPrecio(450000L);
        requestDTO.setCategoriaId(1L);

        when(this.categoryFeignClient.getCategoryById(requestDTO.getCategoriaId())).thenReturn(this.categoriaActivaPrueba);
        when(this.productRepository.save(any(Product.class))).thenAnswer(i -> {
            Product p = i.getArgument(0);
            p.setId(20L);
            return p;
        });

        ProductResponseDTO result = this.productService.createProduct(requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(20L);
        assertThat(result.getNombre()).isEqualTo("Xbox Series X");
        assertThat(result.getEstado()).isEqualTo("Activo");

        verify(categoryFeignClient, times(1)).getCategoryById(requestDTO.getCategoriaId());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al crear si la categoría está inactiva")
    public void shouldThrowExceptionWhenCreateWithInactiveCategory() {
        ProductRequestDTO requestDTO = new ProductRequestDTO();
        requestDTO.setCategoriaId(1L);

        // Simulamos categoría inactiva
        this.categoriaActivaPrueba.setEstado("Inactive");
        when(this.categoryFeignClient.getCategoryById(requestDTO.getCategoriaId())).thenReturn(this.categoriaActivaPrueba);

        assertThatThrownBy(() -> this.productService.createProduct(requestDTO))
                .isInstanceOf(ProductException.class)
                .hasMessage("Categoría no encontrada o inactiva.");

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al crear si el FeignClient falla (Categoría no existe)")
    public void shouldThrowExceptionWhenCreateAndCategoryFails() {
        ProductRequestDTO requestDTO = new ProductRequestDTO();
        requestDTO.setCategoriaId(999L);

        when(this.categoryFeignClient.getCategoryById(requestDTO.getCategoriaId())).thenReturn(null);

        assertThatThrownBy(() -> this.productService.createProduct(requestDTO))
                .isInstanceOf(ProductException.class)
                .hasMessage("Categoría no encontrada o inactiva.");

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Debe actualizar un producto validando que la nueva categoría sea válida")
    public void shouldUpdateProductSuccessfully() {
        Long id = 10L;
        ProductRequestDTO requestDTO = new ProductRequestDTO();
        requestDTO.setNombre("PS5 Pro");
        requestDTO.setCategoriaId(1L);

        when(this.categoryFeignClient.getCategoryById(requestDTO.getCategoriaId())).thenReturn(this.categoriaActivaPrueba);
        when(this.productRepository.findById(id)).thenReturn(Optional.of(this.productoPrueba));
        when(this.productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        ProductResponseDTO result = this.productService.updateProduct(id, requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getNombre()).isEqualTo("PS5 Pro");
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar si la nueva categoría es inválida")
    public void shouldThrowExceptionWhenUpdateWithInvalidCategory() {
        Long id = 10L;
        ProductRequestDTO requestDTO = new ProductRequestDTO();
        requestDTO.setCategoriaId(999L);

        when(this.categoryFeignClient.getCategoryById(requestDTO.getCategoriaId())).thenReturn(null);

        assertThatThrownBy(() -> this.productService.updateProduct(id, requestDTO))
                .isInstanceOf(ProductException.class)
                .hasMessage("Categoría no encontrada o inactiva.");

        verify(productRepository, never()).findById(anyLong());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Debe desactivar un producto por su ID exitosamente")
    public void shouldDeactivateProduct() {
        Long id = 10L;
        when(this.productRepository.findById(id)).thenReturn(Optional.of(this.productoPrueba));
        when(this.productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        this.productService.deactivateProduct(id);

        assertThat(this.productoPrueba.getEstado()).isEqualTo("Inactivo");
        verify(productRepository, times(1)).findById(id);
        verify(productRepository, times(1)).save(this.productoPrueba);
    }
}