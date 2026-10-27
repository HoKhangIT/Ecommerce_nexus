
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.nexus.ecommerce.common.exception.AppException;
import com.nexus.ecommerce.common.exception.ErrorCode;
import com.nexus.ecommerce.inventory.entity.Inventory;
import com.nexus.ecommerce.inventory.repository.InventoryRepository;
import com.nexus.ecommerce.inventory.service.InventoryService;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryService inventoryService;

    @Test
    void deductStock_ShouldSucceed_WhenStockIsSufficient() {
        // Arrange
        Inventory inventory = Inventory.builder()
                .availableQuantity(10)
                .reservedQuantity(0)
                .version(1L)
                .build();
        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.of(inventory));

        // Act
        inventoryService.deductStock(1L, 3);

        // Assert
        assertEquals(7, inventory.getAvailableQuantity());
        verify(inventoryRepository, times(1)).save(inventory);
    }

    @Test
    void deductStock_ShouldThrowOutOfStock_WhenStockIsInsufficient() {
        // Arrange
        Inventory inventory = Inventory.builder()
                .availableQuantity(2)
                .reservedQuantity(0)
                .build();
        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.of(inventory));

        // Act & Assert
        AppException ex = assertThrows(AppException.class, () -> inventoryService.deductStock(1L, 5));
        assertEquals(ErrorCode.OUT_OF_STOCK, ex.getErrorCode());
        verify(inventoryRepository, never()).save(any());
    }
}
