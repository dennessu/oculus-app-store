package com.junbo.identity.spec.v1.model;

import com.junbo.common.jackson.annotation.XSSFreeString;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.Date;

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
    @ApiModelProperty(position = 1, required = false, value = "The country code in which tax exempt certificate is valid.")
    private String taxExemptionCountry;

    @XSSFreeString
    @ApiModelProperty(position = 1, required = false, value = "The subcountry in which tax exempt certificate is valid")
    private String taxExemptionSubcountry;

    @XSSFreeString
    @ApiModelProperty(position = 1, required = false, value = "The start date of the tax exempt certificate, must be ISO 8601")
    private Date taxExemptionStartDate;

    @XSSFreeString
    @ApiModelProperty(position = 1, required = false, value = "The end date of the tax exempt certificate, must be ISO 8601")
    private Date taxExemptionEndDate;

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

    public String getTaxExemptionCountry() {
        return taxExemptionCountry;
    }

    public void setTaxExemptionCountry(String taxExemptionCountry) {
        this.taxExemptionCountry = taxExemptionCountry;
    }

    public String getTaxExemptionSubcountry() {
        return taxExemptionSubcountry;
    }

    public void setTaxExemptionSubcountry(String taxExemptionSubcountry) {
        this.taxExemptionSubcountry = taxExemptionSubcountry;
    }

    public Date getTaxExemptionStartDate() {
        return taxExemptionStartDate;
    }

    public void setTaxExemptionStartDate(Date taxExemptionStartDate) {
        this.taxExemptionStartDate = taxExemptionStartDate;
    }

    public Date getTaxExemptionEndDate() {
        return taxExemptionEndDate;
    }

    public void setTaxExemptionEndDate(Date taxExemptionEndDate) {
        this.taxExemptionEndDate = taxExemptionEndDate;
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
        if (taxExemptionCountry != null ? !taxExemptionCountry.equals(taxExempt.taxExemptionCountry) : taxExempt.taxExemptionCountry != null)
            return false;
        if (taxExemptionEndDate != null ? !taxExemptionEndDate.equals(taxExempt.taxExemptionEndDate) : taxExempt.taxExemptionEndDate != null)
            return false;
        if (taxExemptionReason != null ? !taxExemptionReason.equals(taxExempt.taxExemptionReason) : taxExempt.taxExemptionReason != null)
            return false;
        if (taxExemptionStartDate != null ? !taxExemptionStartDate.equals(taxExempt.taxExemptionStartDate) : taxExempt.taxExemptionStartDate != null)
            return false;
        if (taxExemptionSubcountry != null ? !taxExemptionSubcountry.equals(taxExempt.taxExemptionSubcountry) : taxExempt.taxExemptionSubcountry != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = taxExemptionCertificateNumber != null ? taxExemptionCertificateNumber.hashCode() : 0;
        result = 31 * result + (taxExemptionCertificateFile != null ? taxExemptionCertificateFile.hashCode() : 0);
        result = 31 * result + (taxExemptionReason != null ? taxExemptionReason.hashCode() : 0);
        result = 31 * result + (taxExemptionCountry != null ? taxExemptionCountry.hashCode() : 0);
        result = 31 * result + (taxExemptionSubcountry != null ? taxExemptionSubcountry.hashCode() : 0);
        result = 31 * result + (taxExemptionStartDate != null ? taxExemptionStartDate.hashCode() : 0);
        result = 31 * result + (taxExemptionEndDate != null ? taxExemptionEndDate.hashCode() : 0);
        result = 31 * result + (isTaxExemptionValidated != null ? isTaxExemptionValidated.hashCode() : 0);
        return result;
    }
}
