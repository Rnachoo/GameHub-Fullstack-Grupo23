package com.order.services;

import com.order.clients.InventoryClient;
import com.order.clients.ProductClient;
import com.order.clients.PromotionClient;
import com.order.clients.UserClient;
import com.order.exceptions.OrderException;
import com.order.models.Order;
import com.order.models.OrderItem;
import com.order.models.dtos.*;
import com.order.repositories.OrderRepository;
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
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private InventoryClient inventoryClient;
    @Mock
    private ProductClient productClient;
    @Mock
    private PromotionClient promotionClient;
    @Mock
    private UserClient userClient;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order ordenPrueba;
    private OrderItem itemPrueba;
    private UserDTO userDtoPrueba;
    private ProductDTO productDtoPrueba;
    private PromotionDTO promotionDtoPrueba;
    private InventoryDTO inventoryDtoPrueba;

    @BeforeEach
    public void setUp() {
        Faker faker = new Faker(Locale.of("es", "CL"));

        // 1. Simulación de Respuestas de los Feign Clients
        this.userDtoPrueba = new UserDTO();
        this.userDtoPrueba.setId(1L);
        this.userDtoPrueba.setNombreUser("Usuario de Prueba");

        this.productDtoPrueba = new ProductDTO();
        this.productDtoPrueba.setId(10L);
        this.productDtoPrueba.setPrecio(15000L);
        this.productDtoPrueba.setEstado("Activo");

        this.inventoryDtoPrueba = new InventoryDTO();
        this.inventoryDtoPrueba.setProductId(10L);
        this.inventoryDtoPrueba.setStockDisponible(50L);

        this.promotionDtoPrueba = new PromotionDTO();
        this.promotionDtoPrueba.setValor(5000.0); // Descuento simulado

        // 2. Configuración de Entidades
        this.ordenPrueba = new Order();
        this.ordenPrueba.setId(100L);
        this.ordenPrueba.setUserId(1L);
        this.ordenPrueba.setFecha(LocalDateTime.now());
        this.ordenPrueba.setEstado("PENDIENTE_PAGO");
        this.ordenPrueba.setSubtotal(30000L);
        this.ordenPrueba.setDescuento(0L);
        this.ordenPrueba.setTotal(30000L);

        this.itemPrueba = new OrderItem();
        this.itemPrueba.setId(1L);
        this.itemPrueba.setProductId(10L);
        this.itemPrueba.setCantidad(2L);
        this.itemPrueba.setPrecioUnitario(15000L);
        this.itemPrueba.setOrder(this.ordenPrueba);

        this.ordenPrueba.getItems().add(this.itemPrueba);
    }

    @Test
    @DisplayName("Debe listar todas las órdenes de un cliente")
    public void shouldFindByClient() {
        Long userId = 1L;
        when(this.orderRepository.findByUserId(userId)).thenReturn(List.of(this.ordenPrueba));

        List<OrderDetalleDTO> result = this.orderService.findByClient(userId);

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getUserId()).isEqualTo(userId);
        assertThat(result.get(0).getItems().size()).isEqualTo(1);
        verify(orderRepository, times(1)).findByUserId(userId);
    }

    @Test
    @DisplayName("Debe listar todas las órdenes por estado")
    public void shouldFindByEstado() {
        String estado = "PENDIENTE_PAGO";
        when(this.orderRepository.findByEstado(estado)).thenReturn(List.of(this.ordenPrueba));

        List<OrderDetalleDTO> result = this.orderService.findByEstado(estado);

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getEstado()).isEqualTo(estado);
        verify(orderRepository, times(1)).findByEstado(estado);
    }

    @Test
    @DisplayName("Debe buscar una orden por ID exitosamente")
    public void shouldFindById() {
        Long orderId = 100L;
        when(this.orderRepository.findById(orderId)).thenReturn(Optional.of(this.ordenPrueba));

        OrderDetalleDTO result = this.orderService.findById(orderId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(orderId);
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar orden inexistente")
    public void shouldThrowExceptionWhenOrderNotFound() {
        Long orderId = 999L;
        when(this.orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.orderService.findById(orderId))
                .isInstanceOf(OrderException.class)
                .hasMessage("La orden con id " + orderId + " no existe");
    }

    @Test
    @DisplayName("Debe guardar una orden exitosamente (Con Promoción y Reservas)")
    public void shouldSaveOrderSuccessfully() {
        OrderSaveDTO saveDTO = new OrderSaveDTO();
        saveDTO.setUserId(1L);
        saveDTO.setCodigoPromocion("DESCUENTO_VERANO");

        OrderSaveItemDTO saveItem = new OrderSaveItemDTO();
        saveItem.setProductId(10L);
        saveItem.setCantidad(2L);
        saveDTO.setItems(List.of(saveItem));

        // Mocks de clientes Feign
        when(this.userClient.getUserById(saveDTO.getUserId())).thenReturn(this.userDtoPrueba);
        when(this.productClient.getProductById(saveItem.getProductId())).thenReturn(this.productDtoPrueba);
        when(this.inventoryClient.reservarStock(eq(saveItem.getProductId()), any(InventoryCantidadDTO.class))).thenReturn(this.inventoryDtoPrueba);
        when(this.promotionClient.aplicarPromocion(anyString(), any(PromotionSaveDTO.class), anyDouble())).thenReturn(this.promotionDtoPrueba);

        // Mock del repositorio
        when(this.orderRepository.save(any(Order.class))).thenAnswer(i -> {
            Order ordenGuardada = i.getArgument(0);
            ordenGuardada.setId(200L);
            return ordenGuardada;
        });

        OrderDetalleDTO result = this.orderService.save(saveDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(200L);
        assertThat(result.getSubtotal()).isEqualTo(30000L); // 15000 * 2
        assertThat(result.getDescuento()).isEqualTo(5000L); // Valor de la promoción
        assertThat(result.getTotal()).isEqualTo(25000L); // 30000 - 5000

        verify(userClient, times(1)).getUserById(saveDTO.getUserId());
        verify(productClient, times(1)).getProductById(saveItem.getProductId());
        verify(inventoryClient, times(1)).reservarStock(eq(saveItem.getProductId()), any(InventoryCantidadDTO.class));
        verify(promotionClient, times(1)).aplicarPromocion(anyString(), any(PromotionSaveDTO.class), anyDouble());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al guardar si el usuario no existe")
    public void shouldThrowExceptionWhenUserNotExistsOnSave() {
        OrderSaveDTO saveDTO = new OrderSaveDTO();
        saveDTO.setUserId(999L);

        when(this.userClient.getUserById(saveDTO.getUserId())).thenThrow(mock(FeignException.class));

        assertThatThrownBy(() -> this.orderService.save(saveDTO))
                .isInstanceOf(OrderException.class)
                .hasMessage("El user con id 999 no existe");

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al guardar si el producto está inactivo")
    public void shouldThrowExceptionWhenProductIsInactive() {
        OrderSaveDTO saveDTO = new OrderSaveDTO();
        saveDTO.setUserId(1L);
        OrderSaveItemDTO saveItem = new OrderSaveItemDTO();
        saveItem.setProductId(10L);
        saveDTO.setItems(List.of(saveItem));

        this.productDtoPrueba.setEstado("Inactivo");

        when(this.userClient.getUserById(saveDTO.getUserId())).thenReturn(this.userDtoPrueba);
        when(this.productClient.getProductById(saveItem.getProductId())).thenReturn(this.productDtoPrueba);

        assertThatThrownBy(() -> this.orderService.save(saveDTO))
                .isInstanceOf(OrderException.class)
                .hasMessageContaining("no existe o está inactivo");
    }

    @Test
    @DisplayName("Debe lanzar excepción al guardar si no hay stock suficiente en inventario")
    public void shouldThrowExceptionWhenInventoryFails() {
        OrderSaveDTO saveDTO = new OrderSaveDTO();
        saveDTO.setUserId(1L);
        OrderSaveItemDTO saveItem = new OrderSaveItemDTO();
        saveItem.setProductId(10L);
        saveItem.setCantidad(2L);
        saveDTO.setItems(List.of(saveItem));

        when(this.userClient.getUserById(saveDTO.getUserId())).thenReturn(this.userDtoPrueba);
        when(this.productClient.getProductById(saveItem.getProductId())).thenReturn(this.productDtoPrueba);
        when(this.inventoryClient.reservarStock(eq(saveItem.getProductId()), any(InventoryCantidadDTO.class))).thenThrow(mock(FeignException.class));

        assertThatThrownBy(() -> this.orderService.save(saveDTO))
                .isInstanceOf(OrderException.class)
                .hasMessageContaining("No hay stock suficiente para el producto");
    }

    @Test
    @DisplayName("Debe actualizar el estado de una orden exitosamente")
    public void shouldUpdateEstado() {
        Long orderId = 100L;
        OrderUpdateEstadoDTO updateDTO = new OrderUpdateEstadoDTO();
        updateDTO.setEstado("COMPLETADA");

        when(this.orderRepository.findById(orderId)).thenReturn(Optional.of(this.ordenPrueba));
        when(this.orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        OrderDetalleDTO result = this.orderService.updateEstado(orderId, updateDTO);

        assertThat(result).isNotNull();
        assertThat(result.getEstado()).isEqualTo("COMPLETADA");
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Debe cancelar una orden y liberar el stock en el inventario")
    public void shouldCancelarOrden() {
        Long orderId = 100L;

        when(this.orderRepository.findById(orderId)).thenReturn(Optional.of(this.ordenPrueba));
        when(this.inventoryClient.liberarStock(anyLong(), any(InventoryCantidadDTO.class))).thenReturn(this.inventoryDtoPrueba);

        this.orderService.cancelarOrden(orderId);

        assertThat(this.ordenPrueba.getEstado()).isEqualTo("CANCELADA");
        verify(inventoryClient, times(1)).liberarStock(eq(10L), any(InventoryCantidadDTO.class));
        verify(orderRepository, times(1)).save(this.ordenPrueba);
    }

    @Test
    @DisplayName("Debe lanzar excepción si intenta cancelar una orden ya cancelada")
    public void shouldThrowExceptionWhenOrderIsAlreadyCanceled() {
        Long orderId = 100L;
        this.ordenPrueba.setEstado("CANCELADA");

        when(this.orderRepository.findById(orderId)).thenReturn(Optional.of(this.ordenPrueba));

        assertThatThrownBy(() -> this.orderService.cancelarOrden(orderId))
                .isInstanceOf(OrderException.class)
                .hasMessage("La orden ya se encuentra cancelada.");

        verify(inventoryClient, never()).liberarStock(anyLong(), any(InventoryCantidadDTO.class));
        verify(orderRepository, never()).save(any(Order.class));
    }
}