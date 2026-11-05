package in.knoxdev.ecom.service;


import in.knoxdev.ecom.dto.CartItemRequest;
import in.knoxdev.ecom.dto.CartItemResponse;
import in.knoxdev.ecom.model.CartItem;
import in.knoxdev.ecom.model.Product;
import in.knoxdev.ecom.model.User;
import in.knoxdev.ecom.repositories.CartItemRepository;
import in.knoxdev.ecom.repositories.ProductRepository;
import in.knoxdev.ecom.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;

    public boolean addToCart(String userId, CartItemRequest request) {

       Optional<Product> productOpt = productRepository.findById(request.getProductId());
       if(productOpt.isEmpty()){
           return false;
       }
       Product product = productOpt.get();
       if (product.getStockQuantity() < request.getQuantity()) {
           return false;
       }

       Optional<User> userOpt = userRepository.findById(Long.valueOf(userId));
       if(userOpt.isEmpty()){
           return false;
       }
       User user = userOpt.get();

        CartItem existingCartItem = cartItemRepository.findByUserAndProduct(user, product);
        int currentQty = (existingCartItem != null) ? existingCartItem.getQuantity() : 0;
        int newQty = currentQty + request.getQuantity();

        if (newQty > product.getStockQuantity()) {
            return false;
        }

        if(existingCartItem != null){
            existingCartItem.setQuantity(existingCartItem.getQuantity() + request.getQuantity());
            existingCartItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(existingCartItem.getQuantity())));
            cartItemRepository.save(existingCartItem);
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setQuantity(request.getQuantity());
            cartItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())));
            cartItemRepository.save(cartItem);
        }
        return true;
    }

    public boolean deleteItemFromCart(String userId, Long productId) {
        Optional<Product> productOpt = productRepository.findById(productId);
        Optional<User> userOpt = userRepository.findById(Long.valueOf(userId));
        if (productOpt.isPresent() && userOpt.isPresent()) {
            cartItemRepository.deleteByUserAndProduct(userOpt.get(), productOpt.get());
            return  true;
        }
        return false;
    }

    public List<CartItem> fetchCartProducts(String userId) {
        return userRepository.findById(Long.valueOf(userId))
                .map(cartItemRepository::findByUser)
                .orElseGet(List::of);
    }

    public void clearCart(String userId) {
        userRepository.findById(Long.valueOf(userId)).ifPresent(cartItemRepository::deleteByUser
        );
    }

//    private CartItemResponse mapToCartItemResponse(CartItem item) {
//        CartItemResponse response = new CartItemResponse();
//        response.setId(item.getId());
//        response.setProductId(item.getProduct().getId());
//        response.setProductName(item.getProduct().getName());
//        response.setImageUrl(item.getProduct().getImageUrl());
//        response.setUnitPrice(item.getProduct().getPrice());
//        response.setQuantity(item.getQuantity());
//        response.setTotalPrice(item.getPrice());
//        return response;
//    }
}
