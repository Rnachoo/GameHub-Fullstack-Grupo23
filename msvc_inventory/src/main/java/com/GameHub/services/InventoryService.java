package com.GameHub.services;

import com.GameHub.models.dtos.InventoryCantidadDTO;
import com.GameHub.models.dtos.InventoryDetalleDTO;
import com.GameHub.models.dtos.InventorySaveDTO;
import com.GameHub.models.dtos.InventoryUpdateCantidadDisponibleDTO;

import java.util.List;

public interface InventoryService {
    List<InventoryDetalleDTO> findAllByProduct(Long productId);
    InventoryDetalleDTO findByID(Long id);
    InventoryDetalleDTO save (InventorySaveDTO inventorySaveDTO);
    InventoryDetalleDTO updateCantidadDisponible (Long id, InventoryUpdateCantidadDisponibleDTO cantidadDisponibleDTO);
    void deleteById(Long id  );
    InventoryDetalleDTO reservarStock(Long productId, InventoryCantidadDTO cantidadDTO);
    InventoryDetalleDTO liberarStock(Long productId, InventoryCantidadDTO cantidadDTO);
    InventoryDetalleDTO confirmarVenta(Long productId, InventoryCantidadDTO cantidadDTO);

}
