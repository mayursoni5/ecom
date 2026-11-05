package in.knoxdev.ecom.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemResponse {
    private Long id;
    private Long productId;
    private String product;
    private String imageUrl;
    private BigDecimal Price;
    private Integer quantity;
    private BigDecimal totalPrice;
}
