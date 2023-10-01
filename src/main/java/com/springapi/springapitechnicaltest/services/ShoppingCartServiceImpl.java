package com.springapi.springapitechnicaltest.services;

import com.springapi.springapitechnicaltest.controllers.NotFoundException;
import com.springapi.springapitechnicaltest.models.Product;
import com.springapi.springapitechnicaltest.models.ProductShoppingCart;
import com.springapi.springapitechnicaltest.models.ShoppingCart;
import com.springapi.springapitechnicaltest.repositories.ShoppingCartRepository;
import com.springapi.springapitechnicaltest.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Set;

@Slf4j
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
                Product productFound = productService.getProductByProductId(product.getProductId());
                product.setPrice(productFound.getPrice());
            }
        }
        shoppingCart.setProducts( setTotalProduct(shoppingCart.getProducts()) );
        shoppingCart.setTotal( calculateTotal(shoppingCart.getProducts()) );
        log.info(new Date() + " New shopping cart saved by user "
                + shoppingCart.getUsername() );
        return shoppingCartRepository.save(shoppingCart);
    }

    @Override
    public void deleteShoppingCart(String username) {
        ShoppingCart shoppingCart = findShoppingCartByUsername(username);
        shoppingCartRepository.deleteById(shoppingCart.get_id());
        log.info(new Date() + " Shopping cart deleted for user "
                + shoppingCart.getUsername() );
    }

    @Override
    public void updateShoppingCart(ShoppingCart shoppingCart, String shoppingCartId) {
        shoppingCartRepository.findById(shoppingCartId).orElseThrow(()-> new NotFoundException("This shopping cart does not exists"));
        shoppingCart.set_id(shoppingCartId);
        if( shoppingCart.getProducts().size() > 0 ){
            for ( ProductShoppingCart product: shoppingCart.getProducts() ) {
                Product productFound = productService.getProductByProductId(product.getProductId());
                product.setPrice(productFound.getPrice());
            }
        }
        shoppingCartRepository.save(shoppingCart);
        log.info(new Date() + " Shopping cart updated for user "
                + shoppingCart.getUsername() );
    }

    @Override
    public void addProductToShoppingCart(ProductShoppingCart productToAdd, String username) {
        userRepository.findUserByUsername(username)
                .orElseThrow(()-> new NotFoundException("User '" + username + "' not found"));
        ShoppingCart shoppingCartSaved = findShoppingCartByUsername(username);
        Product productFound = productService.getProductByProductId(productToAdd.getProductId());
        Set<ProductShoppingCart> productsInShoppingCart = shoppingCartSaved.getProducts();
        ProductShoppingCart productFoundInCart = findProductInShoppingCart(productsInShoppingCart, productToAdd.getProductId());
        productToAdd.setPrice(productFound.getPrice());

        if( productFoundInCart != null){
            shoppingCartSaved.setProducts(setNewQuantityToProductInCart(productsInShoppingCart, productToAdd));
            shoppingCartSaved.setTotal(calculateTotal(shoppingCartSaved.getProducts()));
            shoppingCartRepository.save(shoppingCartSaved);
            log.info(new Date() + " New product added to the shopping cart for user "
                    + shoppingCartSaved.getUsername() );
            return;
        }

        productToAdd.setTotal(productToAdd.getPrice()* productToAdd.getQuantity());
        productsInShoppingCart.add(productToAdd);
        shoppingCartSaved.setProducts(productsInShoppingCart);
        shoppingCartSaved.setTotal(calculateTotal(shoppingCartSaved.getProducts()));
        log.info(new Date() + " New product added to the shopping cart for user "
                + shoppingCartSaved.getUsername() );
        shoppingCartRepository.save(shoppingCartSaved);
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
        if(productsInShoppingCart.size() < 1 ) return null;
        for ( ProductShoppingCart product : productsInShoppingCart ) {
            if( product.getProductId().equals(productId) ){
                return product;
            }
        }
        return null;
    }

    private Set<ProductShoppingCart> setNewQuantityToProductInCart(Set<ProductShoppingCart> productsInShoppingCart, ProductShoppingCart newProduct){
        for ( ProductShoppingCart product : productsInShoppingCart ) {
            if( product.getProductId().equals(newProduct.getProductId()) ){
                product.setQuantity(newProduct.getQuantity());
                product.setTotal(product.getPrice() * product.getQuantity());
            }
        }
        return productsInShoppingCart;
    }

    private Float calculateTotal(Set<ProductShoppingCart> productsInShoppingCart){
        Float totalInCart = (float) 0;
        for ( ProductShoppingCart product: productsInShoppingCart ) {
            totalInCart += product.getTotal();
        }
        return totalInCart;
    }

}
