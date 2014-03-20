package com.junbo.cart.db.util

import com.junbo.cart.db.entity.CartEntity
import com.junbo.cart.db.entity.CouponItemEntity
import com.junbo.cart.db.entity.ItemStatus
import com.junbo.cart.db.entity.OfferItemEntity
import com.junbo.cart.spec.model.Cart
import com.junbo.cart.spec.model.item.OfferItem
import com.junbo.common.id.CartId
import com.junbo.common.id.UserId
import org.apache.commons.lang.RandomStringUtils

import java.security.SecureRandom

/**
 * Created by fzhang@wan-san.com on 14-2-15.
 */
class Generator {

    def rand = new SecureRandom()

    def mNextId = System.currentTimeMillis()

    static final int MS_A_SECOND = 1000L

    static final int SHORT_CHAR_LENGTH = 10

    static final int LONG_CHAR_LENGTH = 3000

    Cart cart() {
        Cart cart = new Cart()
        cart.with {
            cartName = RandomStringUtils.randomAlphabetic(SHORT_CHAR_LENGTH)
            clientId = RandomStringUtils.randomAlphabetic(SHORT_CHAR_LENGTH)
            resourceAge = rand.nextLong()
            createdTime = date()
            updatedTime = date()
            id = new CartId(nextId)
            user = new UserId(nextId)
            userLoggedIn = rand.nextBoolean()
        }
        return cart
    }

    CartEntity cartEntity() {
        CartEntity cartEntity = new CartEntity()
        cartEntity.with {
            id = nextId
            userId = nextId
            userLoggedIn = rand.nextBoolean()
            cartName = RandomStringUtils.randomAlphabetic(SHORT_CHAR_LENGTH)
            clientId = RandomStringUtils.randomAlphabetic(SHORT_CHAR_LENGTH)
            properties = RandomStringUtils.randomAlphabetic(LONG_CHAR_LENGTH)
            updatedTime = date()
            createdTime = date()
        }
        return cartEntity
    }

    OfferItem offerItem() {
        def o = new OfferItem()
        o.with {
            id = nextId
            offer = nextId
            updatedTime = date()
            createdTime = date()
            quantity = rand.nextLong()
            selected = rand.nextBoolean()
        }
        return o
    }

    OfferItemEntity offerItemEntity() {
        def o = new OfferItemEntity()
        o.with {
            cartId = nextId
            offerId = nextId
            selected = rand.nextBoolean()
            cartItemId = nextId
            updatedTime = date()
            createdTime = date()
            quantity = rand.nextInt(4000)
            status = ItemStatus.values()[rand.nextInt(ItemStatus.values().length)]
        }
        return o
    }

    CouponItemEntity couponItemEntity() {
        def o = new CouponItemEntity()
        o.with {
            cartId = nextId
            couponCode = nextId.toString()
            cartId = nextId
            cartItemId = nextId
            updatedTime = date()
            createdTime = date()
            status = ItemStatus.values()[rand.nextInt(ItemStatus.values().length)]
        }
        return o
    }


    Date date() {
        return new Date(System.currentTimeMillis() + rand.nextInt(2000) * MS_A_SECOND * 3600L)
    }

    Long getNextId() {
        return mNextId++
    }
}
