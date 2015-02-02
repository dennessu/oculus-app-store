package com.junbo.payment.db.repo;

import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.spec.model.PaymentProviderIdMapping;
import com.junbo.sharding.dualwrite.annotations.ReadMethod;
import com.junbo.sharding.repo.BaseRepository;

import java.util.List;

/**
 * Created by wenzhumac on 1/31/15.
 */
public interface PaymentProviderIdMappingRepository extends BaseRepository<PaymentProviderIdMapping, String> {
}
