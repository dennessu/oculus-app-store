package com.junbo.identity.spec.v1.model;

import com.junbo.common.jackson.annotation.XSSFreeString;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by dell on 1/19/2015.
 */
public class TaxExempt {
    @XSSFreeString
    @ApiModelProperty(position = 1, required = false, value = "The certification number for the user.")
    private String taxExemptionCertificateNumber;

    @XSSFreeString
    @ApiModelProperty(position = 1, required = false, value = "The url for Tax Exemption Certification that user submit.")
    private String taxExemptionCertificateFile;

    @XSSFreeString
    @ApiModelProperty(position = 1, required = false, value = "The Reason code for taexempt, such as \"Education\", \"NATO\", that Vertex supports.")
    private String taxExemptionReason;

    @XSSFreeString
    @ApiModelProperty(position = 1, required = false, value = "True if Oculus has verified user taxExemption material, False if Oculus hasn't verified user taxExemption material, " +
            "this value should only be set by internal tool by Oculus employee.")
    private Boolean isTaxExemptionValidated;

    public String getTaxExemptionCertificateNumber() {
        return taxExemptionCertificateNumber;
    }

    public void setTaxExemptionCertificateNumber(String taxExemptionCertificateNumber) {
        this.taxExemptionCertificateNumber = taxExemptionCertificateNumber;
    }

    public String getTaxExemptionCertificateFile() {
        return taxExemptionCertificateFile;
    }

    public void setTaxExemptionCertificateFile(String taxExemptionCertificateFile) {
        this.taxExemptionCertificateFile = taxExemptionCertificateFile;
    }

    public String getTaxExemptionReason() {
        return taxExemptionReason;
    }

    public void setTaxExemptionReason(String taxExemptionReason) {
        this.taxExemptionReason = taxExemptionReason;
    }

    public Boolean getIsTaxExemptionValidated() {
        return isTaxExemptionValidated;
    }

    public void setIsTaxExemptionValidated(Boolean isTaxExemptionValidated) {
        this.isTaxExemptionValidated = isTaxExemptionValidated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaxExempt taxExempt = (TaxExempt) o;

        if (isTaxExemptionValidated != null ? !isTaxExemptionValidated.equals(taxExempt.isTaxExemptionValidated) : taxExempt.isTaxExemptionValidated != null)
            return false;
        if (taxExemptionCertificateFile != null ? !taxExemptionCertificateFile.equals(taxExempt.taxExemptionCertificateFile) : taxExempt.taxExemptionCertificateFile != null)
            return false;
        if (taxExemptionCertificateNumber != null ? !taxExemptionCertificateNumber.equals(taxExempt.taxExemptionCertificateNumber) : taxExempt.taxExemptionCertificateNumber != null)
            return false;
        if (taxExemptionReason != null ? !taxExemptionReason.equals(taxExempt.taxExemptionReason) : taxExempt.taxExemptionReason != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = taxExemptionCertificateNumber != null ? taxExemptionCertificateNumber.hashCode() : 0;
        result = 31 * result + (taxExemptionCertificateFile != null ? taxExemptionCertificateFile.hashCode() : 0);
        result = 31 * result + (taxExemptionReason != null ? taxExemptionReason.hashCode() : 0);
        result = 31 * result + (isTaxExemptionValidated != null ? isTaxExemptionValidated.hashCode() : 0);
        return result;
    }
}
