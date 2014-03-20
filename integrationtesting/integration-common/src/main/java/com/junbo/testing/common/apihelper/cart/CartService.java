
package com.junbo.testing.common.apihelper.cart;

import com.junbo.cart.spec.model.Cart;
import com.junbo.cart.spec.model.Coupon;
import com.junbo.cart.spec.model.Offer;


/**
 * Created by jiefeng on 14-3-19.
 */
public interface CartService {
    //return cartId in blueprint;
    String  addCart(String userId, Cart cart) throws Exception;

    String addCart(String userId, Cart cart, int expectedResponseCode) throws Exception;

    String  getCart(String userId, String cartId) throws Exception;

    String  getCart(String userId, String cartId, int expectedResponseCode) throws Exception;

    String getCartPrimary(String userId) throws Exception;

    String getCartPrimary(String userId, int expectedResponseCode) throws Exception;

    String  getCartByName(String userId, String cartName) throws Exception;

    String  getCartByName(String userId, String cartName, int expectedResponseCode) throws Exception;

    String  updateCart(String userId, String cartId, Cart cart) throws Exception;

    String  mergeCart(String userId, String cartId, String fromCartId) throws Exception;

    String  addOffer(String userId, String cartId, String offerId) throws Exception;

    String  updateOffer(String userId, String cartId, String offerItemId) throws Exception;

    String  removeOffer(String userId, String cartId, String offerItemId) throws Exception;

    String  addCoupon(String userId, String cartId, String CoupleId) throws Exception;

    String  removeCouple(String userId, String cartId, String coupleItemID) throws Exception;
}
