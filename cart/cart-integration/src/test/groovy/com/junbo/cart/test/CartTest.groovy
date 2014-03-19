package com.junbo.cart.test

import com.junbo.cart.spec.model.Cart
import com.junbo.cart.spec.model.item.CouponItem
import com.junbo.cart.spec.model.item.OfferItem
import com.junbo.cart.test.client.CartClient
import com.junbo.cart.test.client.IdentityClient
import com.junbo.cart.test.util.Generator
import com.junbo.cart.test.validate.CartValidator
import com.junbo.cart.test.validate.CouponValidator
import com.junbo.cart.test.validate.OfferValidator
import com.junbo.common.id.CouponId
import com.junbo.common.id.OfferId
import org.springframework.beans.factory.annotation.Autowired
import org.testng.annotations.Test

/**
 * Created by fzhang@wan-san.com on 14-2-24.
 */
class CartTest extends TestBase {

    @Autowired
    private CartClient cartClient

    @Autowired
    private IdentityClient identityClient

    def generator = new Generator()

    @Test
    void testPrimaryCartOperation() {
        // create user user A
        def userA = identityClient.randomUser()

        // get primary cart
        def cartUserA = cartClient.getPrimaryCart(userA.id)
        assert cartUserA != null
        CartValidator.fromResult(cartUserA).resourceAge(0).updatedTime(cartUserA.createdTime).user(userA.id)

        // add item oA by update
        def offerA = generator.offerItem()
        cartUserA.offers << offerA
        cartUserA = cartClient.updateCart(userA.id, cartUserA.id, cartUserA)
        CartValidator.fromResult(cartUserA).offerNumber(1).resourceAge(1)
        OfferValidator.fromResult(cartUserA.offers[0]).offer(offerA.offer).
                selected(true).quantity(offerA.quantity)

        // add item oB
        def offerB = generator.offerItem()
        cartUserA.offers << offerB
        cartUserA = cartClient.updateCart(userA.id, cartUserA.id, cartUserA)
        def resultOffer = cartUserA.offers.find { return it.offer == offerB.offer }
        CartValidator.fromResult(cartUserA).offerNumber(2).resourceAge(2)
        OfferValidator.fromResult(resultOffer).
                quantity(offerB.quantity).selected(true).updatedTime(resultOffer.updatedTime)

        // change quantity
        offer(cartUserA, offerB).quantity = offer(cartUserA, offerB).quantity * 2
        cartUserA = cartClient.updateCart(userA.id, cartUserA.id, cartUserA)
        CartValidator.fromResult(cartUserA).offerNumber(2).resourceAge(3)
        OfferValidator.fromResult(offer(cartUserA, offerB)).quantity(offerB.quantity * 2).selected(true)

        // unselect oB by update
        offer(cartUserA, offerB).selected = false
        cartUserA = cartClient.updateCart(userA.id, cartUserA.id, cartUserA)
        CartValidator.fromResult(cartUserA).resourceAge(4)
        OfferValidator.fromResult(offer(cartUserA, offerB)).selected(false)

        // update oB quantity by update Cart
        def quantity = offer(cartUserA, offerB).quantity
        offer(cartUserA, offerB).quantity = quantity + 1
        cartUserA = cartClient.updateCart(userA.id, cartUserA.id, cartUserA)
        CartValidator.fromResult(cartUserA).resourceAge(5)
        OfferValidator.fromResult(offer(cartUserA, offerB)).quantity(quantity + 1)

        // update oB quantity by update Cart
        offer(cartUserA, offerB).quantity = quantity
        cartUserA = cartClient.updateOffer(userA.id, cartUserA.id, offer(cartUserA, offerB).id,
                offer(cartUserA, offerB))
        CartValidator.fromResult(cartUserA).resourceAge(6)
        OfferValidator.fromResult(offer(cartUserA, offerB)).quantity(quantity)

        // add coupon cA by update
        def couponA = generator.couponItem()
        cartUserA.coupons << couponA
        cartUserA = cartClient.updateCart(userA.id, cartUserA.id, cartUserA)
        CartValidator.fromResult(cartUserA).resourceAge(7).couponNumber(1)
        CouponValidator.fromResult(cartUserA.coupons[0]).coupon(couponA.coupon)

        // add coupon cB by update
        def couponB = generator.couponItem()
        cartUserA.coupons << couponB
        cartUserA = cartClient.updateCart(userA.id, cartUserA.id, cartUserA)
        CartValidator.fromResult(cartUserA).resourceAge(8).couponNumber(2)
        CouponValidator.fromResult(coupon(cartUserA, couponB)).coupon(couponB.coupon)

        // remove by update
        cartUserA.coupons = [couponA]
        cartUserA = cartClient.updateCart(userA.id, cartUserA.id, cartUserA)
        CartValidator.fromResult(cartUserA).resourceAge(9).couponNumber(1)
        CouponValidator.fromResult(cartUserA.coupons[0]).coupon(couponA.coupon)

        // create cartUserB and update items
        def userB = identityClient.randomUser()
        def cartUserB = cartClient.getPrimaryCart(userB.id)
        def offerC = generator.offerItem()
        cartUserB.offers << offerC
        def couponC = generator.couponItem()
        cartUserB.coupons << couponC
        cartUserB = cartClient.updateCart(userB.id, cartUserB.id, cartUserB)

        // merge from cartUserB to cartUserA
        cartUserA = cartClient.mergeCart(userA.id, cartUserA.id, cartUserB)
        CartValidator.fromResult(cartUserA).couponNumber(2).offerNumber(3)
        OfferValidator.fromResult(offer(cartUserA, offerA)).selected(false)
        OfferValidator.fromResult(offer(cartUserA, offerB)).selected(false)
        OfferValidator.fromResult(offer(cartUserA, offerC)).selected(true)

        cartUserB = cartClient.getPrimaryCart(userB.id)
        CartValidator.fromResult(cartUserB).couponNumber(0).offerNumber(0).resourceAge(2)

    }

    static private  OfferItem offer(Cart cart, OfferId offerId) {
        return cart.offers.find {
            return it.offer == offerId
        }
    }

    static private OfferItem offer(Cart cart, OfferItem offerItem) {
        return offer(cart, offerItem.offer)
    }

    static private CouponItem coupon(Cart cart, CouponItem couponItem) {
        return cart.coupons.find {
            return it.coupon == couponItem.coupon
        }
    }

    static private CouponItem coupon(Cart cart, CouponId couponId) {
        return cart.coupons.find {
            return it.coupon == couponId
        }
    }
}
