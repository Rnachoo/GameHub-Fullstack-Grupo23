package com.GameHub.services;

import com.GameHub.clients.InventoryClient;
import com.GameHub.clients.ProductClient;
import com.GameHub.clients.PromotionClient;
import com.GameHub.clients.UserClient;
import com.GameHub.exceptions.OrderException;
import com.GameHub.models.Order;
import com.GameHub.models.OrderItem;
import com.GameHub.models.dtos.*;
import com.GameHub.repositories.OrderRepository;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService{
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private InventoryClient inventoryClient;
    @Autowired
    private ProductClient productClient;
    @Autowired
    private PromotionClient promotionClient;
    @Autowired
    private UserClient userClient;


    @Transactional(readOnly = true)
    @Override
    public List<OrderDetalleDTO> findByClient(Long userId) {
        log.info("Buscando ordenes para el cliente con ID: "+userId);
        List<Order> ordenes = orderRepository.findByUserId(userId);
        List<OrderDetalleDTO> ordenesDto = new ArrayList<>();

        for (Order orden : ordenes) {
            OrderDetalleDTO dto = new OrderDetalleDTO();
            dto.setId(orden.getId());
            dto.setUserId(orden.getUserId());
            dto.setFecha(orden.getFecha());
            dto.setEstado(orden.getEstado());
            dto.setSubtotal(orden.getSubtotal());
            dto.setDescuento(orden.getDescuento());
            dto.setTotal(orden.getTotal());

            List<OrderItemDTO> itemsDto = new ArrayList<>();
            for (OrderItem item : orden.getItems()) {
                OrderItemDTO itemDto = new OrderItemDTO();
                itemDto.setId(item.getId());
                itemDto.setProductId(item.getProductId());
                itemDto.setCantidad(item.getCantidad());
                itemDto.setPrecioUnitario(item.getPrecioUnitario());

                itemsDto.add(itemDto);
            }

            dto.setItems(itemsDto);
            ordenesDto.add(dto);
        }
        log.info("Se encontraron +"+ordenesDto.size()+" órdenes con el estado: {}"+ userId);
        return ordenesDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<OrderDetalleDTO> findByEstado(String estado) {
        log.info("Buscando órdenes con estado: "+estado);
        List<Order> ordenes = orderRepository.findByEstado(estado);
        List<OrderDetalleDTO> ordenesDto = new ArrayList<>();

        for (Order orden : ordenes) {
            OrderDetalleDTO dto = new OrderDetalleDTO();
            dto.setId(orden.getId());
            dto.setUserId(orden.getUserId());
            dto.setFecha(orden.getFecha());
            dto.setEstado(orden.getEstado());
            dto.setSubtotal(orden.getSubtotal());
            dto.setDescuento(orden.getDescuento());
            dto.setTotal(orden.getTotal());

            List<OrderItemDTO> itemsDto = new ArrayList<>();
            for (OrderItem item : orden.getItems()) {
                OrderItemDTO itemDto = new OrderItemDTO();
                itemDto.setId(item.getId());
                itemDto.setProductId(item.getProductId());
                itemDto.setCantidad(item.getCantidad());
                itemDto.setPrecioUnitario(item.getPrecioUnitario());

                itemsDto.add(itemDto);
            }
            dto.setItems(itemsDto);
            ordenesDto.add(dto);
        }
        log.info("Se encontraron +"+ordenesDto.size()+" órdenes con el estado: {}"+ estado);
        return ordenesDto;
    }

    @Transactional(readOnly = true)
    @Override
    public OrderDetalleDTO findById(Long id){
        log.info("Buscando orden con ID " +id);
        Order orden = orderRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Error en la búsqueda: La orden con ID "+id+"+ no existe");
                    return new OrderException("La orden con id " + id + " no existe");
                });

        log.info("Orden {} encontrada exitosamente", id);
        OrderDetalleDTO dto = new OrderDetalleDTO();
        dto.setId(orden.getId());
        dto.setUserId(orden.getUserId());
        dto.setFecha(orden.getFecha());
        dto.setEstado(orden.getEstado());
        dto.setSubtotal(orden.getSubtotal());
        dto.setDescuento(orden.getDescuento());
        dto.setTotal(orden.getTotal());

        List<OrderItemDTO> itemsDto = new ArrayList<>();
        for (OrderItem item : orden.getItems()) {
            OrderItemDTO itemDto = new OrderItemDTO();
            itemDto.setId(item.getId());
            itemDto.setProductId(item.getProductId());
            itemDto.setCantidad(item.getCantidad());
            itemDto.setPrecioUnitario(item.getPrecioUnitario());

            itemsDto.add(itemDto);
        }
        dto.setItems(itemsDto);
        return dto;
    }


    @Transactional
    @Override
    public OrderDetalleDTO save(OrderSaveDTO orderSaveDTO) {
        log.info("Creando orden con id "+orderSaveDTO.getUserId());
        try {
            userClient.getUserById(orderSaveDTO.getUserId());
        }catch (FeignException e){
            log.error("Error al consultar el mcvs-user para el user con ID "+ orderSaveDTO.getUserId());
            throw new OrderException("El user con id "+orderSaveDTO.getUserId()+" no existe");
        }

        Order newOrder = new Order();
        newOrder.setUserId(orderSaveDTO.getUserId());
        newOrder.setFecha(LocalDateTime.now());
        newOrder.setEstado("PENDIENTE_PAGO");

        Long subTotal = 0L;
        List<OrderItem> listaItems = new ArrayList<>();
        for(OrderSaveItemDTO saveItemDTO : orderSaveDTO.getItems()){
            ProductDTO productDTO = productClient.getProductById(saveItemDTO.getProductId());

            if(productDTO == null || productDTO.getEstado().equals("Inactive")){
                throw new OrderException("El producto con id "+ productDTO.getId()+" no existe o esta inactivo");
            }
            OrderItem newItem = new OrderItem();
            newItem.setProductId(saveItemDTO.getProductId());
            newItem.setCantidad(saveItemDTO.getCantidad());
            newItem.setPrecioUnitario(productDTO.getPrecio());

            newItem.setOrder(newOrder);
            listaItems.add(newItem);

            subTotal += (productDTO.getPrecio() * saveItemDTO.getCantidad());

            try{
                InventoryCantidadDTO cantidadDTO = new InventoryCantidadDTO();
                cantidadDTO.setCantidad(saveItemDTO.getCantidad());
                inventoryClient.reservarStock(saveItemDTO.getProductId(), cantidadDTO);
            }catch (FeignException e){
                log.error("Error al reservar stock en msvc-inventory para el producto " + saveItemDTO.getProductId());
                throw new OrderException("No hay stock suficiente para el producto " + saveItemDTO.getProductId());
            }
        }

        newOrder.setItems(listaItems);
        newOrder.setSubtotal(subTotal);

        Long descuento = 0L;
        if(orderSaveDTO.getCodigoPromocion() != null && !orderSaveDTO.getCodigoPromocion().isBlank()){
            try{
                PromotionSaveDTO promotionSaveDTO = new PromotionSaveDTO(orderSaveDTO.getCodigoPromocion(), orderSaveDTO.getItems());
                descuento = promotionClient.calcularDescuento(promotionSaveDTO);
            }catch (FeignException e) {
                log.error("Error al validar el cupón en msvc-promotion: " + orderSaveDTO.getCodigoPromocion());
                throw new OrderException("El cupón ingresado no es válido o está expirado");
            }
        }
        newOrder.setDescuento(descuento);
        Long totalFinal = subTotal - descuento;
        if (totalFinal < 0L) {
            newOrder.setTotal(0L);
        } else {
            newOrder.setTotal(totalFinal);
        }

        Order orderSave = orderRepository.save(newOrder);
        log.info("Orden "+orderSave.getId()+" Guardada con exito");

        OrderDetalleDTO dto = new OrderDetalleDTO();
        dto.setId(orderSave.getId());
        dto.setUserId(orderSave.getUserId());
        dto.setFecha(orderSave.getFecha());
        dto.setEstado(orderSave.getEstado());
        dto.setSubtotal(orderSave.getSubtotal());
        dto.setDescuento(orderSave.getDescuento());
        dto.setTotal(orderSave.getTotal());

        List<OrderItemDTO> itemsDto = new ArrayList<>();
        for (OrderItem item : orderSave.getItems()) {
            OrderItemDTO itemDto = new OrderItemDTO();
            itemDto.setId(item.getId());
            itemDto.setProductId(item.getProductId());
            itemDto.setCantidad(item.getCantidad());
            itemDto.setPrecioUnitario(item.getPrecioUnitario());

            itemsDto.add(itemDto);
        }
        dto.setItems(itemsDto);
        return dto;

    }

    @Transactional
    @Override
    public OrderDetalleDTO updateEstado(Long id, OrderUpdateEstadoDTO orderUpdateEstadoDTO) {
        log.info("Actualizando estado de la orden con ID " + id);
        Order orden = orderRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Error al actualizar: La orden con ID " + id + " no existe");
                    return new OrderException("La orden con id " + id + " no existe");
                });

        orden.setEstado(orderUpdateEstadoDTO.getEstado());
        Order ordenActualizada = orderRepository.save(orden);

        log.info("Estado de la orden "+ id+ " actualizado exitosamente a: " +orderUpdateEstadoDTO.getEstado());

        OrderDetalleDTO dto = new OrderDetalleDTO();
        dto.setId(ordenActualizada.getId());
        dto.setUserId(ordenActualizada.getUserId());
        dto.setFecha(ordenActualizada.getFecha());
        dto.setEstado(ordenActualizada.getEstado());
        dto.setSubtotal(ordenActualizada.getSubtotal());
        dto.setDescuento(ordenActualizada.getDescuento());
        dto.setTotal(ordenActualizada.getTotal());

        List<OrderItemDTO> itemsDto = new ArrayList<>();
        for (OrderItem item : ordenActualizada.getItems()) {
            OrderItemDTO itemDto = new OrderItemDTO();
            itemDto.setId(item.getId());
            itemDto.setProductId(item.getProductId());
            itemDto.setCantidad(item.getCantidad());
            itemDto.setPrecioUnitario(item.getPrecioUnitario());

            itemsDto.add(itemDto);
        }
        dto.setItems(itemsDto);
        return dto;
    }

    @Transactional
    @Override
    public void cancelarOrden(Long id) {
        log.info("Iniciando proceso de cancelación para la orden "+ id);
        Order orden = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

        if ("CANCELADA".equals(orden.getEstado())) {
            throw new OrderException("La orden ya se encuentra cancelada.");
        }
        orden.setEstado("CANCELADA");
        orderRepository.save(orden);
        log.info("Orden "+id+" cancelada exitosamente");
    }
}
