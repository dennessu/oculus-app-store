package com.junbo.testing.buyerscenario;

import com.junbo.cart.spec.model.Cart;
import com.junbo.cart.spec.model.Offer;
import com.junbo.cart.spec.model.item.CartItem;
import com.junbo.cart.spec.model.item.CouponItem;
import com.junbo.cart.spec.model.item.OfferItem;
import com.junbo.testing.common.apihelper.cart.CartService;
import com.junbo.testing.common.apihelper.cart.impl.CartServiceImpl;
import com.junbo.testing.common.apihelper.identity.UserService;
import com.junbo.testing.common.blueprint.Master;
import com.junbo.testing.common.libs.LogHelper;
import com.junbo.testing.common.property.Component;
import com.junbo.testing.common.property.Priority;
import com.junbo.testing.common.property.Property;
import com.junbo.testing.common.property.Status;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by jiefeng on 14-3-19.
 */
public class CartManagement {
    private LogHelper logger = new LogHelper(CartManagement.class);

    @Property(
            priority = Priority.BVT,
            features = "BuyerScenarios",
            component = Component.Cart,
            owner = "JieFeng",
            status = Status.Incomplete,
            description = "Post a cart",
            steps = {
                    ""
            }
    )
    @Test
    public void testGetPrimaryCart() throws Exception {

        String userId = "user1";

        //post a user


        //prepare a few offers and couples for cart
        //List<OfferItem> offers = new ArrayList<OfferItem>();
        //OfferItem offer1 = new OfferItem();
        //offers.add(offer1);

        //List<CouponItem> coupons = new ArrayList<CouponItem>();
        //CouponItem couponItem1 = new CouponItem();
        //coupons.add(couponItem1);

        //cart api helper;
        CartService cs = CartServiceImpl.getInstance();

        //get primary cart
        String primaryCartId = cs.getCartPrimary(userId);

        Assert.assertNotNull(Master.getInstance().getCart(primaryCartId),"No Primary cart respond!");
        //validation and verify responseCode = 200
        //Assert.assertNotNull(Master.getInstance().getCart(cartId), "no cart respond!");
        //Assert.assertEquals(Master.getInstance().getCart(cartId).getOffers().size(),1);
        //Assert.assertEquals(Master.getInstance().getCart(cartId).getCoupons().size(),1);
        //Assert.assertEquals(Master.getInstance().getCart(cartId).getCartName(), cartName);
    }
}
