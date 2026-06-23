package com.promotion.services;

import com.promotion.clients.CategoryClient;
import com.promotion.exceptions.PromotionException;
import com.promotion.models.Promotion;
import com.promotion.models.dtos.CategoryDTO;
import com.promotion.models.dtos.PromotionAplicarDescuentoDTO;
import com.promotion.models.dtos.PromotionDetalleDTO;
import com.promotion.models.dtos.PromotionSaveDTO;
import com.promotion.models.dtos.PromotionUpdateDateDTO;
import com.promotion.repositories.PromotionRepository;
import feign.FeignException;
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
public class PromotionServiceTest {

    @Mock
    private PromotionRepository promotionRepository;

    @Mock
    private CategoryClient categoryClient;

    @InjectMocks
    private PromotionServiceImpl promotionService;

    private Promotion promocionPrueba;
    private CategoryDTO categoriaPrueba;
    private List<Promotion> listaPromociones = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        Faker faker = new Faker(Locale.of("es", "CL"));

        // Simulación de Categoría Feign
        this.categoriaPrueba = new CategoryDTO();
        this.categoriaPrueba.setId(1L);
        this.categoriaPrueba.setEstado("Active");

        // Simulación de la Entidad Promotion (Válida y activa)
        this.promocionPrueba = new Promotion();
        this.promocionPrueba.setId(1L);
        this.promocionPrueba.setCodigo("SUMMER2026");
        this.promocionPrueba.setValor(5000.0);
        this.promocionPrueba.setTipo("FIJO");
        this.promocionPrueba.setFechaInicio(LocalDateTime.now().minusDays(2)); // Empezó hace 2 días
        this.promocionPrueba.setFechaFin(LocalDateTime.now().plusDays(5));     // Termina en 5 días
        this.promocionPrueba.setMontoMinimo(20000.0);
        this.promocionPrueba.setUsosMaximos(100L);
        this.promocionPrueba.setEstado("Active");

        this.listaPromociones.add(this.promocionPrueba);

        // Generar lista de promociones simuladas
        for (int i = 0; i < 3; i++) {
            Promotion p = new Promotion();
            p.setId((long) (i + 2));
            p.setCodigo(faker.commerce().promotionCode());
            p.setValor(10.0);
            p.setTipo("PORCENTAJE");
            p.setFechaInicio(LocalDateTime.now().minusDays(1));
            p.setFechaFin(LocalDateTime.now().plusDays(10));
            p.setMontoMinimo(10000.0);
            p.setUsosMaximos(50L);
            p.setEstado("Active");
            this.listaPromociones.add(p);
        }
    }

    @Test
    @DisplayName("Debe listar todas las promociones registradas")
    public void shouldFindAll() {
        when(this.promotionRepository.findAll()).thenReturn(this.listaPromociones);

        List<PromotionDetalleDTO> result = this.promotionService.findAll();

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(4);
        verify(promotionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe listar solo las promociones activas")
    public void shouldFindCurrent() {
        when(this.promotionRepository.findByEstado("Active")).thenReturn(this.listaPromociones);

        List<PromotionDetalleDTO> result = this.promotionService.findCurrent();

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(4);
        assertThat(result.get(0).getEstado()).isEqualTo("Active");
        verify(promotionRepository, times(1)).findByEstado("Active");
    }

    @Test
    @DisplayName("Debe buscar una promoción por su ID exitosamente")
    public void shouldFindById() {
        Long id = 1L;
        when(this.promotionRepository.findById(id)).thenReturn(Optional.of(this.promocionPrueba));

        PromotionDetalleDTO result = this.promotionService.findById(id);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getCodigo()).isEqualTo("SUMMER2026");
        verify(promotionRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Debe buscar una promoción por su código exitosamente")
    public void shouldFindByCodigo() {
        String codigo = "SUMMER2026";
        when(this.promotionRepository.findByCodigo(codigo)).thenReturn(Optional.of(this.promocionPrueba));

        PromotionDetalleDTO result = this.promotionService.findByCodigo(codigo);

        assertThat(result).isNotNull();
        assertThat(result.getCodigo()).isEqualTo(codigo);
        verify(promotionRepository, times(1)).findByCodigo(codigo);
    }

    @Test
    @DisplayName("Debe guardar una promoción exitosamente validando la categoría si existe")
    public void shouldSavePromotionSuccessfully() {
        PromotionSaveDTO saveDTO = new PromotionSaveDTO();
        saveDTO.setCodigo("WINTER2026");
        saveDTO.setValor(20.0);
        saveDTO.setTipo("PORCENTAJE");
        saveDTO.setFechaFin(LocalDateTime.now().plusMonths(1));
        saveDTO.setMontoMinimo(15000.0);
        saveDTO.setUsosMaximos(200L);
        saveDTO.setCategoryId(1L); // Enviamos categoryId

        when(this.promotionRepository.findByCodigo(saveDTO.getCodigo())).thenReturn(Optional.empty());
        when(this.categoryClient.getCategoryById(saveDTO.getCategoryId())).thenReturn(this.categoriaPrueba);
        when(this.promotionRepository.save(any(Promotion.class))).thenAnswer(i -> {
            Promotion p = i.getArgument(0);
            p.setId(10L);
            return p;
        });

        PromotionDetalleDTO result = this.promotionService.save(saveDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getCodigo()).isEqualTo("WINTER2026");
        assertThat(result.getEstado()).isEqualTo("Active");

        verify(promotionRepository, times(1)).findByCodigo(saveDTO.getCodigo());
        verify(categoryClient, times(1)).getCategoryById(saveDTO.getCategoryId());
        verify(promotionRepository, times(1)).save(any(Promotion.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al guardar si el código ya existe")
    public void shouldThrowExceptionWhenSaveDuplicateCode() {
        PromotionSaveDTO saveDTO = new PromotionSaveDTO();
        saveDTO.setCodigo("SUMMER2026");

        when(this.promotionRepository.findByCodigo(saveDTO.getCodigo())).thenReturn(Optional.of(this.promocionPrueba));

        assertThatThrownBy(() -> this.promotionService.save(saveDTO))
                .isInstanceOf(PromotionException.class)
                .hasMessageContaining("ya está registrada");

        verify(promotionRepository, never()).save(any(Promotion.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al guardar si el FeignClient falla")
    public void shouldThrowExceptionWhenCategoryClientFailsOnSave() {
        PromotionSaveDTO saveDTO = new PromotionSaveDTO();
        saveDTO.setCodigo("NEWPROMO");
        saveDTO.setCategoryId(999L);

        when(this.promotionRepository.findByCodigo(saveDTO.getCodigo())).thenReturn(Optional.empty());
        when(this.categoryClient.getCategoryById(saveDTO.getCategoryId())).thenThrow(mock(FeignException.class));

        assertThatThrownBy(() -> this.promotionService.save(saveDTO))
                .isInstanceOf(PromotionException.class)
                .hasMessage("La categoría ingresada no existe o el servicio no está disponible");

        verify(promotionRepository, never()).save(any(Promotion.class));
    }

    @Test
    @DisplayName("Debe actualizar las fechas de la promoción exitosamente")
    public void shouldUpdateDate() {
        Long id = 1L;
        PromotionUpdateDateDTO updateDTO = new PromotionUpdateDateDTO();
        updateDTO.setFechaInicio(LocalDateTime.now());
        updateDTO.setFechaFin(LocalDateTime.now().plusMonths(2));

        when(this.promotionRepository.findById(id)).thenReturn(Optional.of(this.promocionPrueba));
        when(this.promotionRepository.save(any(Promotion.class))).thenAnswer(i -> i.getArgument(0));

        PromotionDetalleDTO result = this.promotionService.updateDate(id, updateDTO);

        assertThat(result).isNotNull();
        verify(promotionRepository, times(1)).save(any(Promotion.class));
    }

    @Test
    @DisplayName("Debe desactivar una promoción exitosamente")
    public void shouldDesactiveById() {
        Long id = 1L;
        when(this.promotionRepository.findById(id)).thenReturn(Optional.of(this.promocionPrueba));
        when(this.promotionRepository.save(any(Promotion.class))).thenAnswer(i -> i.getArgument(0));

        PromotionDetalleDTO result = this.promotionService.desactiveById(id);

        assertThat(result.getEstado()).isEqualTo("Inactive");
        verify(promotionRepository, times(1)).save(this.promocionPrueba);
    }

    @Test
    @DisplayName("Debe aplicar promoción exitosamente reduciendo los usos máximos")
    public void shouldAplicarPromocionSuccessfully() {
        String codigo = "SUMMER2026";
        Double totalOrden = 30000.0; // Mayor al mínimo (20000)
        PromotionAplicarDescuentoDTO dtoDescuento = new PromotionAplicarDescuentoDTO();
        dtoDescuento.setCodigo(codigo);

        when(this.promotionRepository.findByCodigo(codigo)).thenReturn(Optional.of(this.promocionPrueba));
        when(this.promotionRepository.save(any(Promotion.class))).thenAnswer(i -> i.getArgument(0));

        PromotionDetalleDTO result = this.promotionService.aplicarPromocion(codigo, dtoDescuento, totalOrden);

        assertThat(result).isNotNull();
        assertThat(this.promocionPrueba.getUsosMaximos()).isEqualTo(99L); // Originalmente 100 - 1
        verify(promotionRepository, times(1)).save(any(Promotion.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al aplicar si la promoción está inactiva")
    public void shouldThrowExceptionWhenAplicarPromocionInactive() {
        String codigo = "SUMMER2026";
        this.promocionPrueba.setEstado("Inactive");

        when(this.promotionRepository.findByCodigo(codigo)).thenReturn(Optional.of(this.promocionPrueba));

        assertThatThrownBy(() -> this.promotionService.aplicarPromocion(codigo, new PromotionAplicarDescuentoDTO(), 50000.0))
                .isInstanceOf(PromotionException.class)
                .hasMessage("El cupon no está activo");
    }

    @Test
    @DisplayName("Debe lanzar excepción al aplicar si la promoción está expirada")
    public void shouldThrowExceptionWhenAplicarPromocionExpired() {
        String codigo = "SUMMER2026";
        // Simulamos que venció ayer
        this.promocionPrueba.setFechaInicio(LocalDateTime.now().minusDays(10));
        this.promocionPrueba.setFechaFin(LocalDateTime.now().minusDays(1));

        when(this.promotionRepository.findByCodigo(codigo)).thenReturn(Optional.of(this.promocionPrueba));

        assertThatThrownBy(() -> this.promotionService.aplicarPromocion(codigo, new PromotionAplicarDescuentoDTO(), 50000.0))
                .isInstanceOf(PromotionException.class)
                .hasMessage("El cupon está vencido o aún no es valido para esta fecha");
    }

    @Test
    @DisplayName("Debe lanzar excepción al aplicar si no alcanza el monto mínimo")
    public void shouldThrowExceptionWhenAplicarPromocionBelowMinimum() {
        String codigo = "SUMMER2026";
        Double totalOrden = 10000.0; // El mínimo es 20000

        when(this.promotionRepository.findByCodigo(codigo)).thenReturn(Optional.of(this.promocionPrueba));

        assertThatThrownBy(() -> this.promotionService.aplicarPromocion(codigo, new PromotionAplicarDescuentoDTO(), totalOrden))
                .isInstanceOf(PromotionException.class)
                .hasMessage("El total de la orden no alcanza el mínimo requerido para usar este cupón");
    }

    @Test
    @DisplayName("Debe lanzar excepción al aplicar si se agotaron los usos máximos")
    public void shouldThrowExceptionWhenAplicarPromocionZeroUses() {
        String codigo = "SUMMER2026";
        this.promocionPrueba.setUsosMaximos(0L); // Cero usos restantes

        when(this.promotionRepository.findByCodigo(codigo)).thenReturn(Optional.of(this.promocionPrueba));

        assertThatThrownBy(() -> this.promotionService.aplicarPromocion(codigo, new PromotionAplicarDescuentoDTO(), 50000.0))
                .isInstanceOf(PromotionException.class)
                .hasMessage("El cupon ha alcanzado su limite de usos maximos");
    }
}