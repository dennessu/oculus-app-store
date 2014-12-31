package com.junbo.store.rest.challenge.impl

import com.junbo.common.enumid.CountryId
import com.junbo.common.enumid.LocaleId
import com.junbo.common.id.UserId
import com.junbo.common.model.Results
import com.junbo.identity.spec.v1.model.*
import com.junbo.identity.spec.v1.option.list.TosListOptions
import com.junbo.identity.spec.v1.option.list.UserTosAgreementListOptions
import com.junbo.identity.spec.v1.option.model.CountryGetOptions
import com.junbo.identity.spec.v1.option.model.UserGetOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.store.clientproxy.ResourceContainer
import com.junbo.store.clientproxy.error.AppErrorUtils
import com.junbo.store.clientproxy.error.ErrorCodes
import com.junbo.store.db.repo.TokenRepository
import com.junbo.store.rest.challenge.ChallengeHelper
import com.junbo.store.rest.utils.Constants
import com.junbo.store.rest.utils.DataConverter
import com.junbo.store.spec.error.AppErrors
import com.junbo.store.spec.model.Challenge
import com.junbo.store.spec.model.ChallengeAnswer
import com.junbo.store.spec.model.token.Token
import groovy.transform.CompileStatic
import org.apache.commons.collections.CollectionUtils
import org.apache.commons.lang3.time.DateUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import javax.annotation.Resource

/**
 * The ChallengeHelperImpl class.
 */
@CompileStatic
@Component('storeChallengeHelper')
class ChallengeHelperImpl implements ChallengeHelper {

    @Resource(name = 'storeResourceContainer')
    ResourceContainer resourceContainer

    @Resource(name = 'storeDataConverter')
    DataConverter dataConverter

    @Value('${store.tos.challenge.enabled}')
    private boolean tosChallengeEnabled

    @Resource(name = 'cloudantTokenPinRepository')
    private TokenRepository tokenRepository

    @Resource(name = 'storeAppErrorUtils')
    private AppErrorUtils appErrorUtils

    @Autowired
    @Value('${store.conf.pinValidDuration}')
    private Integer pinCodeValidateDuration

    @Autowired
    @Value('${store.conf.tosChallengeDefaultLocale}')
    private String defaultLocale

    @Override
    Promise<Challenge> checkTosChallenge(UserId userId, String tosTitle, CountryId countryId, ChallengeAnswer challengeAnswer, LocaleId localeId) {
        if (!tosChallengeEnabled) {
            return Promise.pure()
        }

        User user = resourceContainer.userResource.get(userId, new UserGetOptions()).get()
        CountryId selectedCountryId = user.countryOfResidence == null ? countryId : user.countryOfResidence
        return resourceContainer.tosResource.list(new TosListOptions(title: tosTitle, countryId: selectedCountryId)).then { Results<Tos> toses ->
            if (toses == null || CollectionUtils.isEmpty(toses.items)) {
                return Promise.pure(null)
            }

            List<Tos> tosList = toses.items.sort { Tos item ->
                try {
                    return Double.parseDouble(item.version)
                } catch (NumberFormatException ex) {
                    return Double.valueOf(0)
                }
            }

            Tos tos = tosList.reverse().find { Tos tos ->
                return tos.state == 'APPROVED'
            }
            if (tos == null) {
                return Promise.pure(null)
            }

            if (!isSupportLocale(tos, user, localeId, selectedCountryId)) {
                return Promise.pure(null)
            }

            return resourceContainer.userTosAgreementResource.list(new UserTosAgreementListOptions(
                    userId: userId,
                    tosId: tos.getId()
            )).then { Results<UserTosAgreement> tosAgreementResults ->
                if (!hasValidTosAgreement(tosAgreementResults, tos)) {
                    if (challengeAnswer?.type == Constants.ChallengeType.TOS_ACCEPTANCE && challengeAnswer?.acceptedTos == tos.getId()) {
                        return resourceContainer.userTosAgreementResource.create(new UserTosAgreement(
                                userId: userId,
                                tosId: tos.getId(),
                                agreementTime: new Date()
                        )).then {
                            return Promise.pure(null)
                        }
                    }

                    return Promise.pure(new Challenge(type: Constants.ChallengeType.TOS_ACCEPTANCE, tos: dataConverter.toStoreTos(tos, null)))
                }

                return Promise.pure(null)
            }
        }
    }

    private Boolean isSupportLocale(Tos tos, User user, LocaleId localeId, CountryId countryId) {
        if (CollectionUtils.isEmpty(tos.locales)) {
            return false
        }
        if (tos.locales.contains(user.countryOfResidence)) {
            return true
        }

        if (tos.locales.contains(localeId)) {
            return true
        }

        Country country = resourceContainer.countryResource.get(countryId, new CountryGetOptions()).get()
        if (tos.locales.contains(country.defaultLocale)) {
            return true
        }

        return tos.locales.any { LocaleId tosLocale ->
            return tosLocale.toString().equals(defaultLocale)
        }
    }

    private Boolean hasValidTosAgreement(Results<UserTosAgreement> tosAgreementResults, Tos tos) {
        if (tosAgreementResults == null || CollectionUtils.isEmpty(tosAgreementResults.items)) {
            return false
        }

        return true
    }

    @Override
    Promise<Challenge> checkPurchasePINChallenge(UserId userId, ChallengeAnswer challengeAnswer) {
        if (userId == null) {
            throw new IllegalArgumentException('userId can\'t be null')
        }

        if (challengeAnswer?.type != Constants.ChallengeType.PIN || challengeAnswer?.pin == null) {
            def challenge = new Challenge(type:  Constants.ChallengeType.PIN)
            return tokenRepository.searchByUserIdAndType(userId, Constants.ChallengeType.PIN, 1, 0).then { List<Token> tokenList ->
                if (CollectionUtils.isEmpty(tokenList)) {
                    return Promise.pure(challenge)
                }

                Token validToken = tokenList.find { Token token ->
                    return token.expireTime.after(new Date())
                }

                return Promise.pure(validToken == null ? challenge : null)
            }
        } else {
            return resourceContainer.userCredentialVerifyAttemptResource.create(
                    new UserCredentialVerifyAttempt(
                            userId: userId,
                            type: Constants.ChallengeType.PIN,
                            value: challengeAnswer.pin
                    )
            ).recover { Throwable t ->
                if (appErrorUtils.isAppError(t, ErrorCodes.Identity.InvalidPin)) {
                    throw AppErrors.INSTANCE.invalidChallengeAnswer().exception()
                }

                if (appErrorUtils.isAppError(t, ErrorCodes.Identity.MaximumLoginAttempt)) {
                    throw AppErrors.INSTANCE.invalidChallengeAnswer().exception()
                }

                appErrorUtils.throwUnknownError('purchase', t)
            }.then {
                Token token = new Token(
                        userId: userId,
                        type: Constants.ChallengeType.PIN,
                        expireTime: DateUtils.addSeconds(new Date(), pinCodeValidateDuration)
                )
                return tokenRepository.create(token).then {
                    return Promise.pure(null)
                }
            }
        }
    }
}
