package com.payment.services;

import com.payment.clients.OrdenClient;
import com.payment.exceptions.PaymentException;
import com.payment.models.Payment;
import com.payment.models.dtos.*;
import com.payment.repositories.PaymentRepository;
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
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrdenClient ordenClient;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Payment pagoPrueba;
    private OrderDTO ordenDtoPrueba;

    @BeforeEach
    public void setUp() {
        Faker faker = new Faker(Locale.of("es", "CL"));

        // Simulación de la respuesta de Feign (msvc-orden)
        this.ordenDtoPrueba = new OrderDTO();
        this.ordenDtoPrueba.setId(100L);
        this.ordenDtoPrueba.setTotal(50000L);
        this.ordenDtoPrueba.setEstado("PENDIENTE_PAGO");

        // Simulación de la Entidad Payment
        this.pagoPrueba = new Payment();
        this.pagoPrueba.setId(1L);
        this.pagoPrueba.setOrdenId(100L);
        this.pagoPrueba.setMonto(50000L);
        this.pagoPrueba.setMetodo("TARJETA_CREDITO");
        this.pagoPrueba.setEstado("APROBADO");
        this.pagoPrueba.setCodigoTransaccion(UUID.randomUUID().toString());
        this.pagoPrueba.setFecha(LocalDateTime.now());
    }

    @Test
    @DisplayName("Debe listar todos los pagos de una orden específica")
    public void shouldFindAllByOrdenId() {
        Long ordenId = 100L;
        when(this.paymentRepository.findByOrdenId(ordenId)).thenReturn(List.of(this.pagoPrueba));

        List<PaymentDetalleDTO> result = this.paymentService.findAllByOrdenId(ordenId);

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getOrdenId()).isEqualTo(ordenId);
        verify(paymentRepository, times(1)).findByOrdenId(ordenId);
    }

    @Test
    @DisplayName("Debe listar todos los pagos filtrados por estado")
    public void shouldFindAllByEstado() {
        String estado = "APROBADO";
        when(this.paymentRepository.findByEstado(estado)).thenReturn(List.of(this.pagoPrueba));

        List<PaymentDetalleDTO> result = this.paymentService.findAllByEstado(estado);

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getEstado()).isEqualTo(estado);
        verify(paymentRepository, times(1)).findByEstado(estado);
    }

    @Test
    @DisplayName("Debe buscar un pago por su ID exitosamente")
    public void shouldFindById() {
        Long id = 1L;
        when(this.paymentRepository.findById(id)).thenReturn(Optional.of(this.pagoPrueba));

        PaymentDetalleDTO result = this.paymentService.findById(id);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        verify(paymentRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar un pago con ID inexistente")
    public void shouldThrowExceptionWhenFindByIdNotFound() {
        Long id = 999L;
        when(this.paymentRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.paymentService.findById(id))
                .isInstanceOf(PaymentException.class)
                .hasMessage("Pago con ID " + id + " no encontrado");
    }

    @Test
    @DisplayName("Debe procesar el pago exitosamente y notificar a la orden")
    public void shouldSavePaymentSuccessfully() {
        PaymentSaveDTO saveDTO = new PaymentSaveDTO();
        saveDTO.setOrdenId(100L);
        saveDTO.setMonto(50000L);
        saveDTO.setMetodo("TARJETA_DEBITO");

        when(this.ordenClient.getOrdenById(saveDTO.getOrdenId())).thenReturn(this.ordenDtoPrueba);
        when(this.paymentRepository.existsByOrdenIdAndEstado(saveDTO.getOrdenId(), "APROBADO")).thenReturn(false);
        when(this.paymentRepository.save(any(Payment.class))).thenAnswer(i -> {
            Payment p = i.getArgument(0);
            p.setId(2L);
            return p;
        });
        when(this.ordenClient.actualizarEstadoOrden(eq(100L), any(OrderUpdateEstadoDTO.class))).thenReturn(this.ordenDtoPrueba);

        PaymentDetalleDTO result = this.paymentService.save(saveDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getEstado()).isEqualTo("APROBADO");
        assertThat(result.getCodigoTransaccion()).isNotNull();

        verify(ordenClient, times(1)).getOrdenById(saveDTO.getOrdenId());
        verify(paymentRepository, times(1)).existsByOrdenIdAndEstado(saveDTO.getOrdenId(), "APROBADO");
        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(ordenClient, times(1)).actualizarEstadoOrden(eq(100L), any(OrderUpdateEstadoDTO.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al guardar si el ID de la orden es nulo")
    public void shouldThrowExceptionWhenOrdenIdIsNullOnSave() {
        PaymentSaveDTO saveDTO = new PaymentSaveDTO();
        saveDTO.setMonto(5000L);

        assertThatThrownBy(() -> this.paymentService.save(saveDTO))
                .isInstanceOf(PaymentException.class)
                .hasMessage("El ID de la orden es obligatorio");

        verify(ordenClient, never()).getOrdenById(anyLong());
    }

    @Test
    @DisplayName("Debe lanzar excepción si el servicio de órdenes no responde al consultar")
    public void shouldThrowExceptionWhenOrdenClientFailsOnGet() {
        PaymentSaveDTO saveDTO = new PaymentSaveDTO();
        saveDTO.setOrdenId(100L);

        when(this.ordenClient.getOrdenById(saveDTO.getOrdenId())).thenThrow(mock(FeignException.class));

        assertThatThrownBy(() -> this.paymentService.save(saveDTO))
                .isInstanceOf(PaymentException.class)
                .hasMessage("La orden a pagar no existe o el servicio no responde");

        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si el monto del pago no coincide con el total de la orden")
    public void shouldThrowExceptionWhenMontoDoesNotMatchTotal() {
        PaymentSaveDTO saveDTO = new PaymentSaveDTO();
        saveDTO.setOrdenId(100L);
        saveDTO.setMonto(10000L); // Monto incorrecto (el total de la prueba es 50000)

        when(this.ordenClient.getOrdenById(saveDTO.getOrdenId())).thenReturn(this.ordenDtoPrueba);

        assertThatThrownBy(() -> this.paymentService.save(saveDTO))
                .isInstanceOf(PaymentException.class)
                .hasMessage("El monto de la compra debe coincidir con el total de la orden");

        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si la orden ya tiene un pago aprobado")
    public void shouldThrowExceptionWhenOrdenIsAlreadyPaid() {
        PaymentSaveDTO saveDTO = new PaymentSaveDTO();
        saveDTO.setOrdenId(100L);
        saveDTO.setMonto(50000L); // Monto correcto

        when(this.ordenClient.getOrdenById(saveDTO.getOrdenId())).thenReturn(this.ordenDtoPrueba);
        when(this.paymentRepository.existsByOrdenIdAndEstado(saveDTO.getOrdenId(), "APROBADO")).thenReturn(true);

        assertThatThrownBy(() -> this.paymentService.save(saveDTO))
                .isInstanceOf(PaymentException.class)
                .hasMessage("Esta orden ya ha sido pagada");

        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si falla la actualización del estado de la orden")
    public void shouldThrowExceptionWhenOrdenClientFailsOnUpdate() {
        PaymentSaveDTO saveDTO = new PaymentSaveDTO();
        saveDTO.setOrdenId(100L);
        saveDTO.setMonto(50000L);

        when(this.ordenClient.getOrdenById(saveDTO.getOrdenId())).thenReturn(this.ordenDtoPrueba);
        when(this.paymentRepository.existsByOrdenIdAndEstado(saveDTO.getOrdenId(), "APROBADO")).thenReturn(false);
        when(this.paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArgument(0));

        // Simulamos que el microservicio de ordenes se cae justo al actualizar
        when(this.ordenClient.actualizarEstadoOrden(eq(100L), any(OrderUpdateEstadoDTO.class))).thenThrow(mock(FeignException.class));

        assertThatThrownBy(() -> this.paymentService.save(saveDTO))
                .isInstanceOf(PaymentException.class)
                .hasMessage("El pago fue procesado, pero no se pudo actualizar el estado de la orden. Operación cancelada.");
    }

    @Test
    @DisplayName("Debe actualizar el estado de un pago exitosamente")
    public void shouldUpdateEstado() {
        Long id = 1L;
        PaymentUpdateEstadoDTO updateDTO = new PaymentUpdateEstadoDTO();
        updateDTO.setEstado("RECHAZADO");

        when(this.paymentRepository.findById(id)).thenReturn(Optional.of(this.pagoPrueba));
        when(this.paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArgument(0));

        PaymentDetalleDTO result = this.paymentService.updateEstado(id, updateDTO);

        assertThat(result).isNotNull();
        assertThat(result.getEstado()).isEqualTo("RECHAZADO");
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    @DisplayName("Debe anular un pago por su ID exitosamente")
    public void shouldNullById() {
        Long id = 1L;

        when(this.paymentRepository.findById(id)).thenReturn(Optional.of(this.pagoPrueba));
        when(this.paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArgument(0));

        PaymentDetalleDTO result = this.paymentService.nullById(id);

        assertThat(result).isNotNull();
        assertThat(result.getEstado()).isEqualTo("ANULADO");
        verify(paymentRepository, times(1)).findById(id);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }
}