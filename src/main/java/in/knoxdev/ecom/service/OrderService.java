package in.knoxdev.ecom.service;

import in.knoxdev.ecom.dto.CartItemResponse;
import in.knoxdev.ecom.dto.OrderItemDTO;
import in.knoxdev.ecom.dto.OrderResponse;
import in.knoxdev.ecom.model.*;
import in.knoxdev.ecom.repositories.OrderRepository;
import in.knoxdev.ecom.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    private final CartService cartService;
    private final UserRepository userRepository;


    public Optional<OrderResponse> createOrder(String userId) {
        //Validate for cart item
        List<CartItem> cartItems = cartService.fetchCartProducts(userId);
        if (cartItems == null || cartItems.isEmpty()) {

            return Optional.empty();
        }
        //Validate for user

        Optional<User> userOptional = userRepository.findById(Long.valueOf(userId));
        if (userOptional.isEmpty()) {

            return Optional.empty();
        }
        User user = userOptional.get();

        //Calculate total price
        BigDecimal totalPrice = cartItems.stream()
                .map(CartItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        //Create order

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setTotalAmount(totalPrice);
        List<OrderItem> orderItems = cartItems.stream()
                .map(item-> new OrderItem(
                        null,
                        item.getProduct(),
                        item.getQuantity(),
                        item.getPrice(),
                        order
                ))
                .toList();

        order.setItems(orderItems);
        Order savedorder = orderRepository.save(order);

        //Clear the cart

        cartService.clearCart(userId);

        return Optional.of(mapToOrderResponse(savedorder));
    }

    private OrderResponse mapToOrderResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getItems().stream()
                        .map(orderItem -> new OrderItemDTO(
                               orderItem.getId(),
                               orderItem.getProduct().getId(),
                               orderItem.getQuantity(),
                               orderItem.getPrice(),
                               orderItem.getPrice().multiply(new BigDecimal(orderItem.getQuantity()))
                        ))
                        .toList(),
                order.getCreationAt()
        );
    }
}
