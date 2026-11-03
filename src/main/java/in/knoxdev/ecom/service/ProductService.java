package in.knoxdev.ecom.service;

import in.knoxdev.ecom.dto.ProductRequest;
import in.knoxdev.ecom.dto.ProductResponse;
import in.knoxdev.ecom.model.Product;
import in.knoxdev.ecom.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public ProductResponse createProduct(ProductRequest productRequest) {
        Product product = new Product();
        updateProductFromRequest(product,productRequest);
        Product createdProduct = productRepository.save(product);
        return mapToProductResponse(createdProduct);
    }

    public List<ProductResponse> fetchAllProducts() {
        return productRepository.findByActiveTrue().stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    public Optional<ProductResponse> fetchProduct(Long id) {
        return  productRepository.findById(id).map(this::mapToProductResponse);
    }

    public boolean updateProduct(Long id, ProductRequest updatedProductRequest) {
        return  productRepository.findById(id)
                .map(existingProduct ->{
                    updateProductFromRequest(existingProduct,updatedProductRequest);
                    productRepository.save(existingProduct);
                    return true;
                }).orElse(false);
    }

    public boolean deleteProduct(Long id) {
        return productRepository.findById(id)
                .map(product ->{
                    product.setActive(false);
                    productRepository.save(product);
                    return true;
                }).orElse(false);
    }

    public List<ProductResponse> searchProducts(String keyword) {
        return productRepository.searchProducts(keyword).stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
     }

    private void updateProductFromRequest(Product product, ProductRequest productRequest) {
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setStockQuantity(productRequest.getStockQuantity());
        product.setCategory(productRequest.getCategory());
        product.setImageUrl(productRequest.getImageUrl());
    }

    private ProductResponse mapToProductResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(String.valueOf(product.getId()));
        response.setName(product.getName());
        response.setActive(product.getActive());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setStockQuantity(product.getStockQuantity());
        response.setCategory(product.getCategory());
        response.setImageUrl(product.getImageUrl());
        return response;
    }

}
