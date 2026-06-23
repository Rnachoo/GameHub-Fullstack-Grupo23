package com.inventory.services;

import com.inventory.models.dtos.InventoryCantidadDTO;
import com.inventory.models.dtos.InventoryDetalleDTO;
import com.inventory.models.dtos.InventorySaveDTO;
import com.inventory.models.dtos.InventoryUpdateCantidadDisponibleDTO;

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
