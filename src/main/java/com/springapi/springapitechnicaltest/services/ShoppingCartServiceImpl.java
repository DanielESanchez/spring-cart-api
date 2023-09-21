package com.springapi.springapitechnicaltest.services;

import com.springapi.springapitechnicaltest.controllers.NotFoundException;
import com.springapi.springapitechnicaltest.models.Product;
import com.springapi.springapitechnicaltest.models.ProductShoppingCart;
import com.springapi.springapitechnicaltest.models.ShoppingCart;
import com.springapi.springapitechnicaltest.repositories.ShoppingCartRepository;
import com.springapi.springapitechnicaltest.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final UserRepository userRepository;
    private final ProductService productService;
    @Override
    public ShoppingCart newShoppingCart(ShoppingCart shoppingCart) {
        userRepository.findUserByUsername(shoppingCart.getUsername())
                .orElseThrow(()-> new NotFoundException("User '" + shoppingCart.getUsername() + "' not found"));
        if( shoppingCart.getProducts().size() > 0 ){
            for ( ProductShoppingCart product: shoppingCart.getProducts() ) {
                productService.getProductByProductId(product.getProductId());
            }
        }
        shoppingCart.setProducts( setTotalProduct(shoppingCart.getProducts()) );
        shoppingCart.setTotal( calculateTotal(shoppingCart.getProducts()) );
        return shoppingCartRepository.save(shoppingCart);
    }

    @Override
    public void deleteShoppingCart(String shoppingCartId) {
        shoppingCartRepository.deleteById(shoppingCartId);
    }

    @Override
    public ShoppingCart updateShoppingCart(ShoppingCart shoppingCart, String shoppingCartId) {
        shoppingCartRepository.findById(shoppingCartId).orElseThrow(()-> new NotFoundException("This shopping cart does not exists"));
        shoppingCart.set_id(shoppingCartId);
        return shoppingCartRepository.save(shoppingCart);
    }

    @Override
    public ShoppingCart addProductToShoppingCart(ProductShoppingCart productToAdd, String username) {
        userRepository.findUserByUsername(username)
                .orElseThrow(()-> new NotFoundException("User '" + username + "' not found"));
        ShoppingCart shoppingCartSaved = findShoppingCartByUsername(username);
        Product productFound = productService.getProductByProductId(productToAdd.getProductId());
        Set<ProductShoppingCart> productsInShoppingCart = shoppingCartSaved.getProducts();
        ProductShoppingCart productFoundInCart = findProductInShoppingCart(productsInShoppingCart, productToAdd.getProductId());
        productToAdd.setPrice(productFound.getPrice());
        if( productFoundInCart != null){
            Integer oldQuantity = productFoundInCart.getQuantity();
            Integer newQuantity = productToAdd.getQuantity();
            productsInShoppingCart.remove(productFoundInCart);
            productToAdd.setQuantity( oldQuantity + newQuantity );
            productToAdd.setTotal(productToAdd.getPrice()* productToAdd.getQuantity());
            productsInShoppingCart.add(productToAdd);
            shoppingCartSaved.setProducts(productsInShoppingCart);
            shoppingCartSaved.setTotal(calculateTotal(shoppingCartSaved.getProducts()));
            return shoppingCartRepository.save(shoppingCartSaved);
        }
        productToAdd.setTotal(productToAdd.getPrice()* productToAdd.getQuantity());
        productsInShoppingCart.add(productToAdd);
        shoppingCartSaved.setProducts(productsInShoppingCart);
        shoppingCartSaved.setTotal(calculateTotal(shoppingCartSaved.getProducts()));
        return shoppingCartRepository.save(shoppingCartSaved);
    }

    @Override
    public ShoppingCart findShoppingCartByUsername(String username) {
        userRepository.findUserByUsername(username)
                .orElseThrow(()-> new NotFoundException("User '" + username + "' not found"));
        return shoppingCartRepository.findShoppingCartByUsername(username)
                .orElseThrow( ()-> new NotFoundException("The user '" + username + "' does not have a sopping cart saved") );
    }

    private Set<ProductShoppingCart> setTotalProduct(Set<ProductShoppingCart> productsInShoppingCart){
        for ( ProductShoppingCart product: productsInShoppingCart ) {
            Float productPrice = product.getPrice();
            Float quantity = product.getQuantity().floatValue();
            product.setTotal( quantity * productPrice );
        }
        return productsInShoppingCart;
    }

    private ProductShoppingCart findProductInShoppingCart(Set<ProductShoppingCart> productsInShoppingCart, String productId){
        if(productsInShoppingCart.size() <1 ) return null;
        for ( ProductShoppingCart product : productsInShoppingCart ) {
            if( product.getProductId().equals(productId) ){
                return product;
            }
        }
        return null;
    }

    private Float calculateTotal(Set<ProductShoppingCart> productsInShoppingCart){
        Float totalInCart = (float) 0;
        for ( ProductShoppingCart product: productsInShoppingCart ) {
            totalInCart += product.getTotal();
        }
        return totalInCart;
    }

}
