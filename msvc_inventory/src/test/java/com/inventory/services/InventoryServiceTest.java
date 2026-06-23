package com.inventory.services;

import com.inventory.clients.ProductClient;
import com.inventory.exceptions.InventoryException;
import com.inventory.models.Inventory;
import com.inventory.models.MovimientoInventario;
import com.inventory.models.dtos.*;
import com.inventory.repositories.InventoryRepository;
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
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private ProductClient productClient;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    private Inventory inventarioPrueba;
    private MovimientoInventario movimientoPrueba;
    private ProductDTO productDTOPrueba;

    @BeforeEach
    public void setUp() {
        Faker faker = new Faker(Locale.of("es", "CL"));

        // Configuración de ProductDTO simulado de Feign
        this.productDTOPrueba = new ProductDTO();
        this.productDTOPrueba.setId(100L);
        this.productDTOPrueba.setCantidad(50L);

        // Configuración de MovimientoInventario
        this.movimientoPrueba = new MovimientoInventario();
        this.movimientoPrueba.setId(1L);
        this.movimientoPrueba.setProductId(100L);
        this.movimientoPrueba.setTipo("ENTRADA");
        this.movimientoPrueba.setCantidad(50L);
        this.movimientoPrueba.setFecha(LocalDateTime.now());

        // Configuración de Inventory (Entidad)
        this.inventarioPrueba = new Inventory();
        this.inventarioPrueba.setId(1L);
        this.inventarioPrueba.setProductId(100L);
        this.inventarioPrueba.setStockDisponible(50L);
        this.inventarioPrueba.setStockReservado(10L);
        this.inventarioPrueba.setStockMinimo(5L);
        this.inventarioPrueba.setUbicacion("Pasillo 3, Estante A");

        // Relación bidireccional
        this.movimientoPrueba.setInventario(this.inventarioPrueba);
        this.inventarioPrueba.getMovimientos().add(this.movimientoPrueba);
    }

    @Test
    @DisplayName("Debe listar todo el inventario de un producto específico")
    public void shouldFindAllByProduct() {
        Long productId = 100L;
        when(this.inventoryRepository.findByProductId(productId)).thenReturn(List.of(this.inventarioPrueba));

        List<InventoryDetalleDTO> result = this.inventoryService.findAllByProduct(productId);

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getProductId()).isEqualTo(productId);
        assertThat(result.get(0).getMovimientosDTO().size()).isEqualTo(1);
        verify(inventoryRepository, times(1)).findByProductId(productId);
    }

    @Test
    @DisplayName("Debe buscar un inventario por su ID exitosamente")
    public void shouldFindById() {
        Long id = 1L;
        when(this.inventoryRepository.findById(id)).thenReturn(Optional.of(this.inventarioPrueba));

        InventoryDetalleDTO result = this.inventoryService.findByID(id);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStockDisponible()).isEqualTo(50L);
        verify(inventoryRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Debe guardar un inventario validando con ProductClient")
    public void shouldSaveInventory() {
        InventorySaveDTO saveDTO = new InventorySaveDTO();
        saveDTO.setProductId(100L);
        saveDTO.setStockDisponible(50L);
        saveDTO.setStockReservado(0L);
        saveDTO.setStockMinimo(5L);
        saveDTO.setUbicacion("Almacén Central");

        MovimientoDetalleDTO movDTO = new MovimientoDetalleDTO();
        movDTO.setProductId(100L);
        movDTO.setTipo("ENTRADA");
        movDTO.setCantidad(50L);
        saveDTO.setMovimientosDTO(List.of(movDTO));

        when(this.productClient.getProductById(saveDTO.getProductId())).thenReturn(this.productDTOPrueba);
        when(this.inventoryRepository.save(any(Inventory.class))).thenAnswer(i -> {
            Inventory invGuardado = i.getArgument(0);
            invGuardado.setId(2L);
            return invGuardado;
        });

        InventoryDetalleDTO result = this.inventoryService.save(saveDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getMovimientosDTO().size()).isEqualTo(1);
        verify(productClient, times(1)).getProductById(saveDTO.getProductId());
        verify(inventoryRepository, times(1)).save(any(Inventory.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al guardar si ProductClient falla (FeignException)")
    public void shouldThrowExceptionWhenProductClientFails() {
        InventorySaveDTO saveDTO = new InventorySaveDTO();
        saveDTO.setProductId(999L);

        // Simulamos la caída del microservicio de productos
        when(this.productClient.getProductById(saveDTO.getProductId())).thenThrow(mock(FeignException.class));

        assertThatThrownBy(() -> this.inventoryService.save(saveDTO))
                .isInstanceOf(InventoryException.class)
                .hasMessageContaining("no existe o el servicio no está disponible");

        verify(productClient, times(1)).getProductById(saveDTO.getProductId());
        verify(inventoryRepository, never()).save(any(Inventory.class));
    }

    @Test
    @DisplayName("Debe actualizar el stock disponible exitosamente")
    public void shouldUpdateCantidadDisponible() {
        Long id = 1L;
        InventoryUpdateCantidadDisponibleDTO updateDTO = new InventoryUpdateCantidadDisponibleDTO();
        updateDTO.setStockDisponible(80L); // Aumentando a 80

        when(this.inventoryRepository.findById(id)).thenReturn(Optional.of(this.inventarioPrueba));
        when(this.inventoryRepository.save(any(Inventory.class))).thenAnswer(i -> i.getArgument(0));

        InventoryDetalleDTO result = this.inventoryService.updateCantidadDisponible(id, updateDTO);

        assertThat(result).isNotNull();
        assertThat(result.getStockDisponible()).isEqualTo(80L);
        verify(inventoryRepository, times(1)).findById(id);
        verify(inventoryRepository, times(1)).save(any(Inventory.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al intentar actualizar stock a un valor negativo")
    public void shouldThrowExceptionWhenUpdateNegativeStock() {
        Long id = 1L;
        InventoryUpdateCantidadDisponibleDTO updateDTO = new InventoryUpdateCantidadDisponibleDTO();
        updateDTO.setStockDisponible(-10L);

        when(this.inventoryRepository.findById(id)).thenReturn(Optional.of(this.inventarioPrueba));

        assertThatThrownBy(() -> this.inventoryService.updateCantidadDisponible(id, updateDTO))
                .isInstanceOf(InventoryException.class)
                .hasMessage("El stock disponible no puede quedar en negativo.");

        verify(inventoryRepository, times(1)).findById(id);
        verify(inventoryRepository, never()).save(any(Inventory.class));
    }

    @Test
    @DisplayName("Debe eliminar un inventario si no tiene stock reservado")
    public void shouldDeleteById() {
        Long id = 1L;
        this.inventarioPrueba.setStockReservado(0L); // Condición para permitir borrado
        when(this.inventoryRepository.findById(id)).thenReturn(Optional.of(this.inventarioPrueba));

        this.inventoryService.deleteById(id);

        verify(inventoryRepository, times(1)).findById(id);
        verify(inventoryRepository, times(1)).delete(this.inventarioPrueba);
    }

    @Test
    @DisplayName("Debe lanzar excepción al intentar eliminar inventario con stock reservado")
    public void shouldThrowExceptionWhenDeleteWithReservedStock() {
        Long id = 1L;
        this.inventarioPrueba.setStockReservado(5L); // Condición que bloquea el borrado
        when(this.inventoryRepository.findById(id)).thenReturn(Optional.of(this.inventarioPrueba));

        assertThatThrownBy(() -> this.inventoryService.deleteById(id))
                .isInstanceOf(InventoryException.class)
                .hasMessage("No se puede eliminar el inventario dado que hay productos reservados");

        verify(inventoryRepository, times(1)).findById(id);
        verify(inventoryRepository, never()).delete(any(Inventory.class));
    }

    @Test
    @DisplayName("Debe reservar stock restando del disponible y sumando al reservado")
    public void shouldReservarStock() {
        Long productId = 100L;
        InventoryCantidadDTO cantidadDTO = new InventoryCantidadDTO();
        cantidadDTO.setCantidad(10L); // Queremos reservar 10 unidades (Había 50 Disp. y 10 Res.)

        when(this.inventoryRepository.findByProductId(productId)).thenReturn(List.of(this.inventarioPrueba));
        when(this.inventoryRepository.save(any(Inventory.class))).thenAnswer(i -> i.getArgument(0));
        // El método reservarStock llama internamente a findByID al final, debemos mockearlo
        when(this.inventoryRepository.findById(this.inventarioPrueba.getId())).thenReturn(Optional.of(this.inventarioPrueba));

        InventoryDetalleDTO result = this.inventoryService.reservarStock(productId, cantidadDTO);

        assertThat(result).isNotNull();
        assertThat(this.inventarioPrueba.getStockDisponible()).isEqualTo(40L); // 50 - 10
        assertThat(this.inventarioPrueba.getStockReservado()).isEqualTo(20L); // 10 + 10
        verify(inventoryRepository, times(1)).save(any(Inventory.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si el stock disponible es insuficiente para reservar")
    public void shouldThrowExceptionWhenReservarStockInsufficient() {
        Long productId = 100L;
        InventoryCantidadDTO cantidadDTO = new InventoryCantidadDTO();
        cantidadDTO.setCantidad(100L); // Intento reservar 100, pero solo hay 50

        when(this.inventoryRepository.findByProductId(productId)).thenReturn(List.of(this.inventarioPrueba));

        assertThatThrownBy(() -> this.inventoryService.reservarStock(productId, cantidadDTO))
                .isInstanceOf(InventoryException.class)
                .hasMessageContaining("Stock insufciente");

        verify(inventoryRepository, never()).save(any(Inventory.class));
    }

    @Test
    @DisplayName("Debe confirmar venta restando el stock reservado")
    public void shouldConfirmarVenta() {
        Long productId = 100L;
        InventoryCantidadDTO cantidadDTO = new InventoryCantidadDTO();
        cantidadDTO.setCantidad(5L); // Había 10 en reserva, confirmamos venta de 5

        when(this.inventoryRepository.findByProductId(productId)).thenReturn(List.of(this.inventarioPrueba));
        when(this.inventoryRepository.save(any(Inventory.class))).thenAnswer(i -> i.getArgument(0));
        when(this.inventoryRepository.findById(this.inventarioPrueba.getId())).thenReturn(Optional.of(this.inventarioPrueba));

        InventoryDetalleDTO result = this.inventoryService.confirmarVenta(productId, cantidadDTO);

        assertThat(result).isNotNull();
        assertThat(this.inventarioPrueba.getStockReservado()).isEqualTo(5L); // 10 - 5
        verify(inventoryRepository, times(1)).save(any(Inventory.class));
    }

    @Test
    @DisplayName("Debe liberar stock restando del reservado y devolviéndolo al disponible")
    public void shouldLiberarStock() {
        Long productId = 100L;
        InventoryCantidadDTO cantidadDTO = new InventoryCantidadDTO();
        cantidadDTO.setCantidad(5L); // Cancelamos reserva de 5

        when(this.inventoryRepository.findByProductId(productId)).thenReturn(List.of(this.inventarioPrueba));
        when(this.inventoryRepository.save(any(Inventory.class))).thenAnswer(i -> i.getArgument(0));
        when(this.inventoryRepository.findById(this.inventarioPrueba.getId())).thenReturn(Optional.of(this.inventarioPrueba));

        InventoryDetalleDTO result = this.inventoryService.liberarStock(productId, cantidadDTO);

        assertThat(result).isNotNull();
        assertThat(this.inventarioPrueba.getStockReservado()).isEqualTo(5L); // 10 - 5
        assertThat(this.inventarioPrueba.getStockDisponible()).isEqualTo(55L); // 50 + 5
        verify(inventoryRepository, times(1)).save(any(Inventory.class));
    }
}