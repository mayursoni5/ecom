package in.knoxdev.ecom.controller;

import in.knoxdev.ecom.dto.CartItemRequest;
import in.knoxdev.ecom.dto.CartItemResponse;
import in.knoxdev.ecom.model.CartItem;
import in.knoxdev.ecom.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping
    public ResponseEntity<String> addToCart(@RequestHeader("X-User-ID") String userId, @RequestBody CartItemRequest request){
       if (!cartService.addToCart(userId,request)){
           return  ResponseEntity.badRequest().body("Product Out of Stock or User not found or Product not found or Quantity Exceeding the Availability");
       }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Void> removeFromCart(@RequestHeader("X-User-ID") String userId, @PathVariable Long productId){
        boolean deleted = cartService.deleteItemFromCart(userId, productId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<CartItem>> fetchProductsFromCart(@RequestHeader("X-User-ID") String userId){
        return new ResponseEntity<>(cartService.fetchCartProducts(userId),HttpStatus.OK);
    }
}
