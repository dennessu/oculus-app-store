package com.junbo.emulator.casey.rest

import com.junbo.emulator.casey.spec.model.CaseyEmulatorData
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

/**
 * The CaseyRepository class.
 */
@CompileStatic
@Component('caseyEmulatorDataRepository')
class CaseyEmulatorDataRepository {

    private CaseyEmulatorData caseyEmulatorData = new CaseyEmulatorData(
        caseyAggregateRatings: [],
        caseyReviews: []
    )

    CaseyEmulatorData post(CaseyEmulatorData caseyEmulatorData) {
        this.caseyEmulatorData = caseyEmulatorData
        return caseyEmulatorData
    }

    CaseyEmulatorData get() {
        return caseyEmulatorData
    }
}
