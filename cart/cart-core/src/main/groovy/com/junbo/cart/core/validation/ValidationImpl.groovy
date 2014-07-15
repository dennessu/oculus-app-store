package com.junbo.cart.core.validation
import com.junbo.cart.common.validate.Group
import com.junbo.cart.core.service.CartPersistService
import com.junbo.cart.core.service.impl.CartServiceImpl
import com.junbo.cart.spec.error.AppErrors
import com.junbo.cart.spec.model.Cart
import com.junbo.cart.spec.model.item.OfferItem
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.error.ErrorDetail
import com.junbo.common.id.CouponId
import com.junbo.common.id.UserId
import com.junbo.identity.spec.v1.model.User
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.hibernate.validator.HibernateValidator

import javax.validation.ConstraintViolation
import javax.validation.ValidationProviderResolver
import javax.validation.Validator
import javax.validation.ValidatorFactory
import javax.validation.spi.ValidationProvider
/**
 * Created by fzhang@wan-san.com on 14-1-23.
 */
@CompileStatic
class ValidationImpl implements Validation {

    private CartPersistService cartPersistService

    private static Validator validator

    static {
        ValidatorFactory validatorFactory = javax.validation.Validation.byDefaultProvider()
                .providerResolver( new OsgiServiceDiscoverer() )
                .configure()
                .buildValidatorFactory()
        validator = validatorFactory.validator
    }

    static class OsgiServiceDiscoverer implements ValidationProviderResolver {

        @Override
        List<ValidationProvider<?>> getValidationProviders() {
            return Collections.singletonList(new HibernateValidator())
        }
    }

    void setCartPersistService(CartPersistService cartPersistService) {
        this.cartPersistService = cartPersistService
    }

    @Override
    Validation validateUser(UserId userId, User user) {
        if (user == null) {
            throw AppErrors.INSTANCE.userNotFound("user", userId).exception()
        }
        if ('ACTIVE' != user.status) {
            throw AppErrors.INSTANCE.userStatusInvalid("user.status", userId, user.status).exception()
        }
        return this
    }

    @Override
    Validation validateCartAdd(String clientId, UserId userId, Cart cart) {
        validateField(cart, Group.CartCreate, Group.CartItem)
        validateCouponCodes(cart)
        // validate if cart with the name already exist
        if (cart.cartName == CartServiceImpl.CART_NAME_PRIMARY ||
            Promise.get { return cartPersistService.get(clientId, cart.cartName, userId) } != null) {
            throw AppErrors.INSTANCE.cartAlreadyExists(userId, cart.cartName).exception()
        }
        return this
    }

    @Override
    Validation validateCartUpdate(Cart newCart, Cart oldCart) {
        validateField(newCart, Group.CartUpdate, Group.CartItem)
        validateCouponCodes(newCart)
    }

    @Override
    Validation validateCartOwner(Cart cart, UserId userId) {
        if (cart.user != userId) {
            throw AppCommonErrors.INSTANCE.resourceNotFound("Cart", cart.getId()).exception()
        }
    }

    @Override
    Validation validateMerge(Cart mergeCart) {
        validateField(mergeCart, Group.CartMerge)
    }

    @Override
    Validation validateOfferAdd(OfferItem offerItem) {
        validateField(offerItem, Group.CartItem)
    }

    @Override
    Validation validateOfferUpdate(OfferItem offerItem) {
        validateField(offerItem, Group.CartItem)
    }

    private static void validateField(Object obj, Class... group) {
        def result = validator.validate(obj, group)
        if (!result.isEmpty()) {
            def errorDetails = result.collect { ConstraintViolation violation ->
                new ErrorDetail(violation.propertyPath.toString(), violation.message)
            }.toArray(new ErrorDetail[0])
            throw AppCommonErrors.INSTANCE.fieldInvalid(errorDetails).exception()
        }
    }

    private static void validateCouponCodes(Cart cart) {
        cart.coupons?.eachWithIndex { CouponId coupon, int i ->
            if (coupon == null) {
                throw AppCommonErrors.INSTANCE.fieldInvalid("coupons[${i}]").exception()
            }
        }
    }
}
