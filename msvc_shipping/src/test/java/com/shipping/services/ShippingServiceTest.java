package com.shipping.services;

import com.shipping.clients.OrderFeignClient;
import com.shipping.clients.UserFeignClient;
import com.shipping.clients.dtos.OrdenClientDTO;
import com.shipping.clients.dtos.UsuarioClientDTO;
import com.shipping.exceptions.ShippingException;
import com.shipping.models.Despacho;
import com.shipping.models.EstadoDespacho;
import com.shipping.models.dtos.DespachoRequestDTO;
import com.shipping.models.dtos.DespachoResponseDTO;
import com.shipping.repositories.DespachoRepository;
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
public class ShippingServiceTest {

    @Mock
    private DespachoRepository despachoRepository;
    @Mock
    private OrderFeignClient orderFeignClient;
    @Mock
    private UserFeignClient userFeignClient;

    @InjectMocks
    private ShippingServiceImpl shippingService;

    private Despacho despachoPrueba;
    private OrdenClientDTO ordenMock;
    private UsuarioClientDTO usuarioMock;
    private List<Despacho> listaDespachos = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        Faker faker = new Faker(Locale.of("es", "CL"));

        // Simulación de respuesta de Orden
        this.ordenMock = new OrdenClientDTO();
        this.ordenMock.setId(100L);
        this.ordenMock.setUsuarioId(1L);
        this.ordenMock.setEstado("PAGADA");

        // Simulación de respuesta de Usuario
        this.usuarioMock = new UsuarioClientDTO();
        this.usuarioMock.setId(1L);
        this.usuarioMock.setNombreUser("Juan Perez");
        this.usuarioMock.setEstado("Active");
        this.usuarioMock.setDirectionsDTO(List.of("Avenida Principal 123")); // Debe tener al menos una dirección

        // Simulación de Entidad Despacho
        this.despachoPrueba = new Despacho();
        this.despachoPrueba.setId(10L);
        this.despachoPrueba.setOrdenId(100L);
        this.despachoPrueba.setUsuarioId(1L);
        this.despachoPrueba.setDireccion("Avenida Principal 123");
        this.despachoPrueba.setTransportista("BlueExpress");
        this.despachoPrueba.setTracking("TRK-123456");
        this.despachoPrueba.setEstado(EstadoDespacho.PENDIENTE);
        this.despachoPrueba.setFechaEnvio(LocalDateTime.now());

        this.listaDespachos.add(this.despachoPrueba);
    }

    @Test
    @DisplayName("Debe crear un despacho exitosamente")
    public void shouldCreateDespachoSuccessfully() {
        DespachoRequestDTO requestDTO = new DespachoRequestDTO();
        requestDTO.setOrdenId(100L);
        requestDTO.setUsuarioId(1L);
        requestDTO.setDireccion("Avenida Principal 123");
        requestDTO.setTransportista("BlueExpress");
        requestDTO.setTracking("TRK-999999");

        when(this.orderFeignClient.getOrdenById(requestDTO.getOrdenId())).thenReturn(this.ordenMock);
        when(this.userFeignClient.getUsuarioById(requestDTO.getUsuarioId())).thenReturn(this.usuarioMock);
        when(this.despachoRepository.findByTracking(requestDTO.getTracking())).thenReturn(Optional.empty());
        when(this.despachoRepository.save(any(Despacho.class))).thenAnswer(i -> {
            Despacho d = i.getArgument(0);
            d.setId(20L);
            return d;
        });

        DespachoResponseDTO result = this.shippingService.createDespacho(requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(20L);
        assertThat(result.getEstado()).isEqualTo(EstadoDespacho.PENDIENTE);

        verify(orderFeignClient, times(1)).getOrdenById(requestDTO.getOrdenId());
        verify(userFeignClient, times(1)).getUsuarioById(requestDTO.getUsuarioId());
        verify(despachoRepository, times(1)).findByTracking(requestDTO.getTracking());
        verify(despachoRepository, times(1)).save(any(Despacho.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si la orden no está PAGADA")
    public void shouldThrowExceptionWhenOrderNotPagada() {
        DespachoRequestDTO requestDTO = new DespachoRequestDTO();
        requestDTO.setOrdenId(100L);

        this.ordenMock.setEstado("PENDIENTE_PAGO"); // Estado inválido para despachar
        when(this.orderFeignClient.getOrdenById(requestDTO.getOrdenId())).thenReturn(this.ordenMock);

        assertThatThrownBy(() -> this.shippingService.createDespacho(requestDTO))
                .isInstanceOf(ShippingException.class)
                .hasMessage("Solo se puede despachar una orden pagada.");
    }

    @Test
    @DisplayName("Debe lanzar excepción si el usuario no tiene direcciones registradas")
    public void shouldThrowExceptionWhenUserHasNoDirections() {
        DespachoRequestDTO requestDTO = new DespachoRequestDTO();
        requestDTO.setOrdenId(100L);
        requestDTO.setUsuarioId(1L);

        this.usuarioMock.setDirectionsDTO(new ArrayList<>()); // Lista vacía
        when(this.orderFeignClient.getOrdenById(requestDTO.getOrdenId())).thenReturn(this.ordenMock);
        when(this.userFeignClient.getUsuarioById(requestDTO.getUsuarioId())).thenReturn(this.usuarioMock);

        assertThatThrownBy(() -> this.shippingService.createDespacho(requestDTO))
                .isInstanceOf(ShippingException.class)
                .hasMessage("El usuario no tiene dirección registrada.");
    }

    @Test
    @DisplayName("Debe lanzar excepción si el usuario del Request no es el dueño de la Orden")
    public void shouldThrowExceptionWhenUserNotOwnerOfOrder() {
        DespachoRequestDTO requestDTO = new DespachoRequestDTO();
        requestDTO.setOrdenId(100L);
        requestDTO.setUsuarioId(999L); // ID distinto al de la orden (1L)

        // Usamos un mock de usuario distinto para que pase la validación de la dirección
        UsuarioClientDTO otroUsuarioMock = new UsuarioClientDTO();
        otroUsuarioMock.setDirectionsDTO(List.of("Direccion Falsa"));

        when(this.orderFeignClient.getOrdenById(requestDTO.getOrdenId())).thenReturn(this.ordenMock);
        when(this.userFeignClient.getUsuarioById(requestDTO.getUsuarioId())).thenReturn(otroUsuarioMock);

        assertThatThrownBy(() -> this.shippingService.createDespacho(requestDTO))
                .isInstanceOf(ShippingException.class)
                .hasMessage("El usuario que solicita el envío no es el dueño de la orden.");
    }

    @Test
    @DisplayName("Debe lanzar excepción si el tracking ya existe al crear")
    public void shouldThrowExceptionWhenTrackingExistsOnCreate() {
        DespachoRequestDTO requestDTO = new DespachoRequestDTO();
        requestDTO.setOrdenId(100L);
        requestDTO.setUsuarioId(1L);
        requestDTO.setTracking("TRK-DUPLICADO");

        when(this.orderFeignClient.getOrdenById(requestDTO.getOrdenId())).thenReturn(this.ordenMock);
        when(this.userFeignClient.getUsuarioById(requestDTO.getUsuarioId())).thenReturn(this.usuarioMock);
        when(this.despachoRepository.findByTracking(requestDTO.getTracking())).thenReturn(Optional.of(this.despachoPrueba));

        assertThatThrownBy(() -> this.shippingService.createDespacho(requestDTO))
                .isInstanceOf(ShippingException.class)
                .hasMessage("El tracking ya existe: TRK-DUPLICADO");
    }

    @Test
    @DisplayName("Debe listar todos los despachos")
    public void shouldGetAllDespachos() {
        when(this.despachoRepository.findAll()).thenReturn(this.listaDespachos);

        List<DespachoResponseDTO> result = this.shippingService.getAllDespachos();

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        verify(despachoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe listar despachos por ID de orden")
    public void shouldGetDespachosByOrdenId() {
        Long ordenId = 100L;
        when(this.despachoRepository.findByOrdenId(ordenId)).thenReturn(this.listaDespachos);

        List<DespachoResponseDTO> result = this.shippingService.getDespachosByOrdenId(ordenId);

        assertThat(result).isNotNull();
        assertThat(result.get(0).getOrdenId()).isEqualTo(ordenId);
        verify(despachoRepository, times(1)).findByOrdenId(ordenId);
    }

    @Test
    @DisplayName("Debe listar despachos por estado (String convertido a Enum)")
    public void shouldGetDespachosByEstado() {
        String estadoString = "PENDIENTE";
        when(this.despachoRepository.findByEstado(EstadoDespacho.PENDIENTE)).thenReturn(this.listaDespachos);

        List<DespachoResponseDTO> result = this.shippingService.getDespachosByEstado(estadoString);

        assertThat(result).isNotNull();
        assertThat(result.get(0).getEstado()).isEqualTo(EstadoDespacho.PENDIENTE);
        verify(despachoRepository, times(1)).findByEstado(EstadoDespacho.PENDIENTE);
    }

    @Test
    @DisplayName("Debe actualizar el estado del despacho exitosamente (A EN_CAMINO)")
    public void shouldUpdateEstadoDespacho() {
        Long id = 10L;
        String nuevoEstado = "EN_CAMINO";
        String nuevoTracking = "TRK-NUEVO";

        when(this.despachoRepository.findById(id)).thenReturn(Optional.of(this.despachoPrueba));
        when(this.despachoRepository.findByTracking(nuevoTracking)).thenReturn(Optional.empty());
        when(this.despachoRepository.save(any(Despacho.class))).thenAnswer(i -> i.getArgument(0));

        DespachoResponseDTO result = this.shippingService.updateEstadoDespacho(id, nuevoEstado, nuevoTracking, null);

        assertThat(result.getEstado()).isEqualTo(EstadoDespacho.EN_CAMINO);
        assertThat(result.getTracking()).isEqualTo(nuevoTracking);
        verify(despachoRepository, times(1)).save(any(Despacho.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si se actualiza a ENTREGADO sin fecha de entrega")
    public void shouldThrowExceptionWhenEntregadoSinFecha() {
        Long id = 10L;
        String nuevoEstado = "ENTREGADO";

        when(this.despachoRepository.findById(id)).thenReturn(Optional.of(this.despachoPrueba));

        assertThatThrownBy(() -> this.shippingService.updateEstadoDespacho(id, nuevoEstado, null, null))
                .isInstanceOf(ShippingException.class)
                .hasMessage("Debe proporcionar fecha de entrega para marcar como entregado.");

        verify(despachoRepository, never()).save(any(Despacho.class));
    }

    @Test
    @DisplayName("Debe cancelar un despacho exitosamente")
    public void shouldCancelarDespacho() {
        Long id = 10L;

        when(this.despachoRepository.findById(id)).thenReturn(Optional.of(this.despachoPrueba));
        when(this.despachoRepository.save(any(Despacho.class))).thenAnswer(i -> i.getArgument(0));

        DespachoResponseDTO result = this.shippingService.cancelarDespacho(id);

        assertThat(result.getEstado()).isEqualTo(EstadoDespacho.CANCELADO);
        verify(despachoRepository, times(1)).save(this.despachoPrueba);
    }

    @Test
    @DisplayName("Debe lanzar excepción al intentar cancelar un despacho ya entregado")
    public void shouldThrowExceptionWhenCancelandoEntregado() {
        Long id = 10L;
        this.despachoPrueba.setEstado(EstadoDespacho.ENTREGADO); // Estado bloqueante

        when(this.despachoRepository.findById(id)).thenReturn(Optional.of(this.despachoPrueba));

        assertThatThrownBy(() -> this.shippingService.cancelarDespacho(id))
                .isInstanceOf(ShippingException.class)
                .hasMessage("No se puede cancelar un despacho ya entregado.");

        verify(despachoRepository, never()).save(any(Despacho.class));
    }
}