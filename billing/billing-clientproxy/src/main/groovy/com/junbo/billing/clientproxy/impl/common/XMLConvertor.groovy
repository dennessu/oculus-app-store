/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.clientproxy.impl.common

import com.junbo.billing.clientproxy.impl.sabrix.AddressValidationResponse
import com.junbo.billing.clientproxy.impl.sabrix.Batch
import com.junbo.billing.clientproxy.impl.sabrix.RegistrationValidationRequest
import com.junbo.billing.clientproxy.impl.sabrix.RegistrationValidationResponse
import com.junbo.billing.clientproxy.impl.sabrix.SabrixAddress
import com.junbo.billing.clientproxy.impl.sabrix.TaxCalculationResponse
import com.thoughtworks.xstream.XStream
import com.thoughtworks.xstream.io.HierarchicalStreamDriver
import com.thoughtworks.xstream.io.xml.DomDriver
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder
import com.thoughtworks.xstream.mapper.MapperWrapper
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.springframework.stereotype.Component

/**
 * Util class to convert XML.
 */
@CompileStatic
@TypeChecked
@Component('xmlConvertor')
class XmlConvertor {
    private final XStream xstream

    private static class CustomizedXStream extends XStream {
        public CustomizedXStream(HierarchicalStreamDriver hierarchicalStreamDriver) {
            super(hierarchicalStreamDriver)
        }

        /* ignore the extra field */
        @Override
        protected MapperWrapper wrapMapper(MapperWrapper next) {
            return new MapperWrapper(next) {
                @Override
                public boolean shouldSerializeMember(Class definedIn, String fieldName) {
                    if (definedIn == Object.class) {
                        return false
                    }
                    return super.shouldSerializeMember(definedIn, fieldName)
                }
            }
        }
    }

    XmlConvertor() {
        xstream = new CustomizedXStream(new DomDriver("UTF-8", new XmlFriendlyNameCoder("_-", "_")))
        xstream.processAnnotations(Batch)
        xstream.processAnnotations(SabrixAddress)
        xstream.processAnnotations(RegistrationValidationRequest)
        xstream.processAnnotations(TaxCalculationResponse)
        xstream.processAnnotations(AddressValidationResponse)
        xstream.processAnnotations(RegistrationValidationResponse)
        xstream.setMode(XStream.NO_REFERENCES)
    }

    String getXml(Batch batch) {
        return xstream.toXML(batch)
    }

    TaxCalculationResponse getTaxCalculationResponse(String xml) {
        return (TaxCalculationResponse)xstream.fromXML(xml)
    }

    String getXml(SabrixAddress address) {
        return xstream.toXML(address)
    }

    AddressValidationResponse getAddressValidationResponse(String xml) {
        return (AddressValidationResponse)xstream.fromXML(xml)
    }

    String getXml(RegistrationValidationRequest request) {
        return xstream.toXML(request)
    }

    RegistrationValidationResponse getRegistrationValidationResponse(String xml) {
        return (RegistrationValidationResponse)xstream.fromXML(xml)
    }
}
