package com.junbo.ewallet.spec.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.junbo.common.model.ResourceMetaForDualWrite
import groovy.transform.CompileStatic

/**
 * Created by haomin on 14-6-20.
 */
@CompileStatic
@JsonInclude(JsonInclude.Include.NON_NULL)
class WalletLot extends ResourceMetaForDualWrite<Long> {
    Long id
    Long walletId
    String type
    BigDecimal totalAmount
    BigDecimal remainingAmount
    Date expirationDate
    @JsonIgnore
    Boolean isValid
}
