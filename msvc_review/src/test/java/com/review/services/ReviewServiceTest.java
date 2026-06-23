package com.review.services;

import com.review.clients.OrderFeignClient;
import com.review.clients.ProductFeignClient;
import com.review.clients.UserFeignClient;
import com.review.clients.dtos.OrdenClientDTO;
import com.review.clients.dtos.ProductoClientDTO;
import com.review.clients.dtos.UsuarioClientDTO;
import com.review.exceptions.ReviewException;
import com.review.models.Review;
import com.review.models.dtos.ResenaRequestDTO;
import com.review.models.dtos.ResenaResponseDTO;
import com.review.models.dtos.ResenaUpdateDTO;
import com.review.repositories.ResenaRepository;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @Mock
    private ResenaRepository resenaRepository;
    @Mock
    private UserFeignClient userFeignClient;
    @Mock
    private ProductFeignClient productFeignClient;
    @Mock
    private OrderFeignClient orderFeignClient;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private Review resenaPrueba;
    private UsuarioClientDTO usuarioMock;
    private ProductoClientDTO productoMock;
    private OrdenClientDTO ordenMock;
    private List<Review> listaResenas = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        Faker faker = new Faker(Locale.of("es", "CL"));

        // 1. Simulación de Respuestas de Feign
        this.usuarioMock = new UsuarioClientDTO();
        this.usuarioMock.setEstado("Active");

        this.productoMock = new ProductoClientDTO();
        this.productoMock.setEstado(true); // El servicio valida producto.isEstado()

        this.ordenMock = new OrdenClientDTO();
        this.ordenMock.setUsuarioId(1L); // Debe coincidir con el usuario que hace la reseña

        // 2. Simulación de la Entidad Review
        this.resenaPrueba = new Review();
        this.resenaPrueba.setId(10L);
        this.resenaPrueba.setUsuarioId(1L);
        this.resenaPrueba.setProductoId(50L);
        this.resenaPrueba.setOrdenId(100L);
        this.resenaPrueba.setPuntuacion(5);
        this.resenaPrueba.setComentario("¡Excelente producto!");
        this.resenaPrueba.setEstado("Active");
        this.resenaPrueba.setFecha(LocalDateTime.now());

        this.listaResenas.add(this.resenaPrueba);

        for (int i = 0; i < 3; i++) {
            Review r = new Review();
            r.setId((long) (i + 11));
            r.setUsuarioId(1L);
            r.setProductoId(50L);
            r.setOrdenId((long) (i + 101));
            r.setPuntuacion(4);
            r.setComentario(faker.lorem().sentence());
            r.setEstado("Active");
            this.listaResenas.add(r);
        }
    }

    @Test
    @DisplayName("Debe crear una reseña exitosamente")
    public void shouldCreateResenaSuccessfully() {
        ResenaRequestDTO requestDTO = new ResenaRequestDTO();
        requestDTO.setUsuarioId(1L);
        requestDTO.setProductoId(50L);
        requestDTO.setOrdenId(100L);
        requestDTO.setPuntuacion(5);
        requestDTO.setComentario("Me encantó");

        when(this.userFeignClient.getUsuarioById(requestDTO.getUsuarioId())).thenReturn(this.usuarioMock);
        when(this.productFeignClient.getProductoById(requestDTO.getProductoId())).thenReturn(this.productoMock);
        when(this.orderFeignClient.getOrdenById(requestDTO.getOrdenId())).thenReturn(this.ordenMock);
        when(this.resenaRepository.existsByUsuarioIdAndProductoIdAndOrdenId(1L, 50L, 100L)).thenReturn(false);
        when(this.resenaRepository.save(any(Review.class))).thenAnswer(i -> {
            Review r = i.getArgument(0);
            r.setId(20L);
            return r;
        });

        ResenaResponseDTO result = this.reviewService.createResena(requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(20L);
        assertThat(result.getPuntuacion()).isEqualTo(5);
        assertThat(result.getEstado()).isEqualTo("Active");

        verify(userFeignClient, times(1)).getUsuarioById(requestDTO.getUsuarioId());
        verify(productFeignClient, times(1)).getProductoById(requestDTO.getProductoId());
        verify(orderFeignClient, times(1)).getOrdenById(requestDTO.getOrdenId());
        verify(resenaRepository, times(1)).existsByUsuarioIdAndProductoIdAndOrdenId(1L, 50L, 100L);
        verify(resenaRepository, times(1)).save(any(Review.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si el usuario está inactivo o no existe")
    public void shouldThrowExceptionWhenUserInactive() {
        ResenaRequestDTO requestDTO = new ResenaRequestDTO();
        requestDTO.setUsuarioId(1L);

        this.usuarioMock.setEstado("Inactive");
        when(this.userFeignClient.getUsuarioById(requestDTO.getUsuarioId())).thenReturn(this.usuarioMock);

        assertThatThrownBy(() -> this.reviewService.createResena(requestDTO))
                .isInstanceOf(ReviewException.class)
                .hasMessage("Usuario no encontrado o inactivo.");

        verify(resenaRepository, never()).save(any(Review.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si el producto está inactivo")
    public void shouldThrowExceptionWhenProductInactive() {
        ResenaRequestDTO requestDTO = new ResenaRequestDTO();
        requestDTO.setUsuarioId(1L);
        requestDTO.setProductoId(50L);

        this.productoMock.setEstado(false);
        when(this.userFeignClient.getUsuarioById(requestDTO.getUsuarioId())).thenReturn(this.usuarioMock);
        when(this.productFeignClient.getProductoById(requestDTO.getProductoId())).thenReturn(this.productoMock);

        assertThatThrownBy(() -> this.reviewService.createResena(requestDTO))
                .isInstanceOf(ReviewException.class)
                .hasMessage("Producto no encontrado o inactivo.");
    }

    @Test
    @DisplayName("Debe lanzar excepción si el usuario no es el dueño de la orden")
    public void shouldThrowExceptionWhenUserNotOwnerOfOrder() {
        ResenaRequestDTO requestDTO = new ResenaRequestDTO();
        requestDTO.setUsuarioId(1L);
        requestDTO.setProductoId(50L);
        requestDTO.setOrdenId(100L);

        this.ordenMock.setUsuarioId(999L); // ID distinto al requestDTO

        when(this.userFeignClient.getUsuarioById(requestDTO.getUsuarioId())).thenReturn(this.usuarioMock);
        when(this.productFeignClient.getProductoById(requestDTO.getProductoId())).thenReturn(this.productoMock);
        when(this.orderFeignClient.getOrdenById(requestDTO.getOrdenId())).thenReturn(this.ordenMock);

        assertThatThrownBy(() -> this.reviewService.createResena(requestDTO))
                .isInstanceOf(ReviewException.class)
                .hasMessage("El usuario no es dueño de la orden.");
    }

    @Test
    @DisplayName("Debe lanzar excepción si ya existe una reseña para esta compra")
    public void shouldThrowExceptionWhenReviewAlreadyExists() {
        ResenaRequestDTO requestDTO = new ResenaRequestDTO();
        requestDTO.setUsuarioId(1L);
        requestDTO.setProductoId(50L);
        requestDTO.setOrdenId(100L);

        when(this.userFeignClient.getUsuarioById(requestDTO.getUsuarioId())).thenReturn(this.usuarioMock);
        when(this.productFeignClient.getProductoById(requestDTO.getProductoId())).thenReturn(this.productoMock);
        when(this.orderFeignClient.getOrdenById(requestDTO.getOrdenId())).thenReturn(this.ordenMock);
        when(this.resenaRepository.existsByUsuarioIdAndProductoIdAndOrdenId(1L, 50L, 100L)).thenReturn(true);

        assertThatThrownBy(() -> this.reviewService.createResena(requestDTO))
                .isInstanceOf(ReviewException.class)
                .hasMessage("Ya existe una reseña para esta compra y producto.");
    }

    @Test
    @DisplayName("Debe buscar una reseña por su ID")
    public void shouldGetResenaById() {
        Long id = 10L;
        when(this.resenaRepository.findById(id)).thenReturn(Optional.of(this.resenaPrueba));

        ResenaResponseDTO result = this.reviewService.getResenaById(id);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10L);
        verify(resenaRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Debe listar reseñas por ID de producto")
    public void shouldGetResenasByProductoId() {
        Long productoId = 50L;
        when(this.resenaRepository.findByProductoId(productoId)).thenReturn(this.listaResenas);

        List<ResenaResponseDTO> result = this.reviewService.getResenasByProductoId(productoId);

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(4);
        verify(resenaRepository, times(1)).findByProductoId(productoId);
    }

    @Test
    @DisplayName("Debe listar reseñas por ID de usuario")
    public void shouldGetResenasByUsuarioId() {
        Long usuarioId = 1L;
        when(this.resenaRepository.findByUsuarioId(usuarioId)).thenReturn(this.listaResenas);

        List<ResenaResponseDTO> result = this.reviewService.getResenasByUsuarioId(usuarioId);

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(4);
        verify(resenaRepository, times(1)).findByUsuarioId(usuarioId);
    }

    @Test
    @DisplayName("Debe actualizar una reseña exitosamente")
    public void shouldUpdateResena() {
        Long id = 10L;
        ResenaUpdateDTO updateDTO = new ResenaUpdateDTO();
        updateDTO.setPuntuacion(3);
        updateDTO.setComentario("Comentario editado");

        when(this.resenaRepository.findById(id)).thenReturn(Optional.of(this.resenaPrueba));
        when(this.resenaRepository.save(any(Review.class))).thenAnswer(i -> i.getArgument(0));

        ResenaResponseDTO result = this.reviewService.updateResena(id, updateDTO);

        assertThat(result.getPuntuacion()).isEqualTo(3);
        assertThat(result.getComentario()).isEqualTo("Comentario editado");
        verify(resenaRepository, times(1)).save(any(Review.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al intentar actualizar una reseña moderada (Inactive)")
    public void shouldThrowExceptionWhenUpdatingInactiveResena() {
        Long id = 10L;
        ResenaUpdateDTO updateDTO = new ResenaUpdateDTO();
        this.resenaPrueba.setEstado("Inactive");

        when(this.resenaRepository.findById(id)).thenReturn(Optional.of(this.resenaPrueba));

        assertThatThrownBy(() -> this.reviewService.updateResena(id, updateDTO))
                .isInstanceOf(ReviewException.class)
                .hasMessage("No se puede actualizar una reseña moderada.");

        verify(resenaRepository, never()).save(any(Review.class));
    }

    @Test
    @DisplayName("Debe moderar (desactivar) una reseña exitosamente")
    public void shouldModerarResena() {
        Long id = 10L;

        when(this.resenaRepository.findById(id)).thenReturn(Optional.of(this.resenaPrueba));
        when(this.resenaRepository.save(any(Review.class))).thenAnswer(i -> i.getArgument(0));

        ResenaResponseDTO result = this.reviewService.moderarResena(id);

        assertThat(result.getEstado()).isEqualTo("Inactive");
        verify(resenaRepository, times(1)).save(any(Review.class));
    }
}