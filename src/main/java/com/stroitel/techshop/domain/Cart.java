package com.stroitel.techshop.domain;

import javax.persistence.*;
import java.util.*;


@Entity
@Table(name = "cart")
public class Cart {

    @Id
    @Column(name = "id")
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private UserAccount userAccount;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true,
            targetEntity = CartItem.class, mappedBy = "cart")
    private List<CartItem> cartItems = new ArrayList<>(0);

    @Column(name = "delivery_included", nullable = false)
    private boolean deliveryIncluded = true;

    @Transient
    private double itemsCost;

    public Cart() {
        this(null);
    }

    public Cart(UserAccount userAccount) {
        this.userAccount = userAccount;
        itemsCost = calculateItemsCost();
    }

    public boolean isEmpty() {
        return cartItems.isEmpty();
    }

    public CartItem update(Product product, int newQuantity) {
        if (product == null)
            return null;

        CartItem updatedItem = null;
        if (newQuantity > 0) {
            CartItem existingItem = findItem(product.getId());
            if (existingItem == null) {
                CartItem newItem = new CartItem(this, product, newQuantity);
                cartItems.add(newItem);
                updatedItem = newItem;
            } else {
                existingItem.setQuantity(newQuantity);
                updatedItem = existingItem;
            }
        } else {
            removeItem(product.getId());
        }
        itemsCost = calculateItemsCost();
        return updatedItem;
    }

    private void removeItem(long productId) {
        Iterator<CartItem> iterator = cartItems.iterator();
        while (iterator.hasNext()) {
            CartItem item = iterator.next();
            if (item.getProduct().getId() == productId)
                iterator.remove();
        }
    }

    private CartItem findItem(long productId) {
        for (CartItem existingItem : cartItems) {
            if (existingItem.getProduct().getId() == productId)
                return existingItem;
        }
        return null;
    }

    private double calculateItemsCost() {
        return cartItems.stream()
                .mapToDouble(CartItem::calculateCost)
                .sum();
    }

    public void clear() {
        cartItems.clear();
        itemsCost = 0;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public List<CartItem> getCartItems() {
        return Collections.unmodifiableList(cartItems);
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
        itemsCost = calculateItemsCost();
    }

    public boolean isDeliveryIncluded() {
        return deliveryIncluded;
    }

    public void setDeliveryIncluded(boolean deliveryIncluded) {
        this.deliveryIncluded = deliveryIncluded;
    }

    public int getItemsCount() {
        return cartItems.size();
    }

    public double getItemsCost() {
        return itemsCost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cart cart = (Cart) o;
        return deliveryIncluded == cart.deliveryIncluded &&
                Objects.equals(id, cart.id) &&
                Objects.equals(cartItems, cart.cartItems);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cartItems, deliveryIncluded);
    }

    public static class Builder {
        private long id;
        private UserAccount userAccount;
        private List<CartItem> cartItems = new ArrayList<>(0);
        private boolean deliveryIncluded = true;
        private double itemsCost;

        public Builder() {
        }

        public Builder(Cart cart) {
            id = cart.id;
            userAccount = cart.userAccount;
            cartItems = cart.cartItems;
            deliveryIncluded = cart.deliveryIncluded;
            itemsCost = cart.itemsCost;
        }

        public Cart build() {
            Cart cart = new Cart();
            cart.id = id;
            cart.userAccount = userAccount;
            cart.cartItems = cartItems;
            cart.deliveryIncluded = deliveryIncluded;
            cart.itemsCost = itemsCost;
            return cart;
        }

        public Builder setId(long id) {
            this.id = id;
            return this;
        }

        public Builder setUserAccount(UserAccount userAccount) {
            this.userAccount = userAccount;
            return this;
        }

        public Builder setCartItems(List<CartItem> cartItems) {
            this.cartItems = cartItems;
            return this;
        }

        public Builder setDeliveryIncluded(boolean deliveryIncluded) {
            this.deliveryIncluded = deliveryIncluded;
            return this;
        }

        public Builder setItemsCost(double itemsCost) {
            this.itemsCost = itemsCost;
            return this;
        }
    }
}

