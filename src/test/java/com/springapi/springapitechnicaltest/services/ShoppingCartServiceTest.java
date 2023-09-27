package com.springapi.springapitechnicaltest.services;

import com.springapi.springapitechnicaltest.controllers.NotFoundException;
import com.springapi.springapitechnicaltest.models.*;
import com.springapi.springapitechnicaltest.repositories.ProductRepository;
import com.springapi.springapitechnicaltest.repositories.ShoppingCartRepository;
import com.springapi.springapitechnicaltest.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class ShoppingCartServiceTest {
    @Autowired
    ShoppingCartService shoppingCartService;
    @MockBean
    ShoppingCartRepository shoppingCartRepository;
    @MockBean
    UserRepository userRepository;
    @MockBean
    ProductRepository productRepository;
    private final UserRole userRole = UserRole.builder().role(Role.builder().name(RoleName.ROLE_USER).build()).build();
    private User testUser;
    private ShoppingCart shoppingCart;
    ProductShoppingCart productShoppingCart;
    private Product product;

    @BeforeEach
    void setup() {
        Set<UserRole> rolesUser = new HashSet<>();
        rolesUser.add(userRole);

        testUser = new User();
        testUser.setUsername("user");
        testUser.setUserRoles(rolesUser);

        shoppingCart = new ShoppingCart();
        shoppingCart.setUsername("user");
        shoppingCart.set_id("1");
        Set<String> categories = new HashSet<>();
        categories.add("cat");
        Set<ProductShoppingCart> products = new HashSet<>();
        product =  new Product("prod1","name","desc",categories,(float) 5, 52);
        product.setPrice((float)25);
        productShoppingCart = new ProductShoppingCart("prod1", 5);
        productShoppingCart.setPrice( (float)5 );
        productShoppingCart.setTotal( (float)25 );
        products.add(productShoppingCart);
        shoppingCart.setProducts(products);

    }

    @Test
    void shouldReturnShoppingCartSaved_WhenNewShoppingCartNotEmptyProducts() {
        when(userRepository.findUserByUsername(eq("user")))
                .thenReturn(Optional.of(testUser));
        when(productRepository.findProductByProductId(anyString())).thenReturn(Optional.of(product));
        when(shoppingCartRepository.save(any(ShoppingCart.class)))
                .thenReturn(shoppingCart);

        ShoppingCart result = shoppingCartService.newShoppingCart(shoppingCart);

        assertNotNull(result);
    }

    @Test
    void shouldReturnShoppingCartSaved_WhenNewShoppingCartEmptyProducts() {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUsername("user");

        when(userRepository.findUserByUsername(eq("user")))
                .thenReturn(Optional.of(testUser));
        when(shoppingCartRepository.save(any(ShoppingCart.class)))
                .thenReturn(shoppingCart);


        ShoppingCart result = shoppingCartService.newShoppingCart(shoppingCart);

        assertNotNull(result);
    }

    @Test
    void shouldReturnNothing_WhenDeleteShoppingCart() {
        doNothing().when(shoppingCartRepository).deleteById(eq("id"));

        shoppingCartService.deleteShoppingCart("id");

        verify(shoppingCartRepository, times(1)).deleteById(eq("id"));
    }

    @Test
    void shouldReturnNothing_WhenUpdateShoppingCart() {
        ProductShoppingCart productShoppingCartToAdd = new ProductShoppingCart("prod2", 5);
        ShoppingCart shoppingCartUpdated = shoppingCart;
        Set<ProductShoppingCart> newProducts = shoppingCartUpdated.getProducts();
        newProducts.add(productShoppingCartToAdd);
        shoppingCartUpdated.setProducts(newProducts);

        when(userRepository.findUserByUsername(eq("user")))
                .thenReturn(Optional.of(testUser));
        when(productRepository.findProductByProductId(anyString())).thenReturn(Optional.of(product));
        when(shoppingCartRepository.findById(anyString()))
                .thenReturn(Optional.of(shoppingCart));

        shoppingCartService.updateShoppingCart(shoppingCart, "id");

        assertEquals(shoppingCart.getProducts().size(), 2);
        verify(shoppingCartRepository, times(1)).findById("id");
        verify(productRepository, times(2)).findProductByProductId(anyString());
    }

    @Test
    void shouldReturnNotFound_WhenUpdateShoppingCartDoesNotExists() {
        ProductShoppingCart productShoppingCartAdded = new ProductShoppingCart("prod2", 5);
        ShoppingCart shoppingCartUpdated = shoppingCart;
        Set<ProductShoppingCart> newProducts = shoppingCartUpdated.getProducts();
        newProducts.add(productShoppingCartAdded);
        shoppingCartUpdated.setProducts(newProducts);

        when(userRepository.findUserByUsername(eq("user")))
                .thenReturn(Optional.of(testUser));
        when(productRepository.findProductByProductId(anyString())).thenReturn(Optional.of(product));
        when(shoppingCartRepository.findById("id"))
                .thenReturn(Optional.of(shoppingCart));

        assertThrows(NotFoundException.class, () -> shoppingCartService.updateShoppingCart(shoppingCart, "unrealId"));
        verify(shoppingCartRepository, times(1)).findById("unrealId");
        verify(productRepository, times(0)).findProductByProductId(anyString());
    }

    @Test
    void shouldReturnNothing_WhenAddProductToShoppingCart() {
        ProductShoppingCart productShoppingCartToAdd = new ProductShoppingCart("prod2", 5);
        productShoppingCartToAdd.setTotal((float) 15);
        Set<String> categories = new HashSet<>();
        categories.add("cat");
        Product productNew =  new Product("prod2","nam2","desc2",categories,(float) 6, 52);
        productNew.setPrice( (float) 15 );

        when(userRepository.findUserByUsername(eq("user")))
                .thenReturn(Optional.of(testUser));
        when(productRepository.findProductByProductId("prod1")).thenReturn(Optional.of(product));
        when(productRepository.findProductByProductId("prod2")).thenReturn(Optional.of(product));
        when(shoppingCartRepository.findShoppingCartByUsername("user"))
                .thenReturn(Optional.of(shoppingCart));

        shoppingCartService.addProductToShoppingCart(productShoppingCartToAdd, "user");

        assertEquals(shoppingCart.getProducts().size(), 2);
        verify(shoppingCartRepository, times(1)).save(any(ShoppingCart.class));
        verify(productRepository, times(1)).findProductByProductId(anyString());

        shoppingCart.setProducts(null);

    }

    @Test
    void shouldReturnNothing_WhenAddProductToShoppingCartWithProductAlreadyAddedBefore() {
        ProductShoppingCart productShoppingCartToAdd = productShoppingCart;
        productShoppingCartToAdd.setQuantity(5);

        when(userRepository.findUserByUsername(eq("user")))
                .thenReturn(Optional.of(testUser));
        when(productRepository.findProductByProductId(productShoppingCartToAdd.getProductId())).thenReturn(Optional.of(product));
        when(shoppingCartRepository.findShoppingCartByUsername("user"))
                .thenReturn(Optional.of(shoppingCart));

        shoppingCartService.addProductToShoppingCart(productShoppingCartToAdd, "user");

        assertEquals(shoppingCart.getProducts().size(), 1);
        verify(shoppingCartRepository, times(1)).save(any(ShoppingCart.class));
        verify(productRepository, times(1)).findProductByProductId(anyString());

    }

    @Test
    void shoouldReturnShoppingCart_WhenFindShoppingCartByUsername() {
        when(userRepository.findUserByUsername("user")).thenReturn(Optional.of(testUser));
        when(shoppingCartRepository.findShoppingCartByUsername("user")).thenReturn(Optional.of(shoppingCart));

        ShoppingCart result = shoppingCartService.findShoppingCartByUsername("user");

        assertEquals(result, shoppingCart);
        verify(shoppingCartRepository, times(1)).findShoppingCartByUsername("user");
        verify(userRepository, times(1)).findUserByUsername("user");
    }

    @Test
    void shouldReturnNotFound_WhenFindShoppingCartByUsernameWithShoppingCartNotSavedForTheUser() {
        when(userRepository.findUserByUsername("user")).thenReturn(Optional.of(testUser));
        when(shoppingCartRepository.findShoppingCartByUsername("user")).thenReturn(Optional.of(shoppingCart));


        assertThrows(NotFoundException.class, ()-> shoppingCartService.findShoppingCartByUsername("unrealUser"));
        verify(shoppingCartRepository, times(0)).findShoppingCartByUsername("user");
        verify(userRepository, times(1)).findUserByUsername("unrealUser");
    }

}