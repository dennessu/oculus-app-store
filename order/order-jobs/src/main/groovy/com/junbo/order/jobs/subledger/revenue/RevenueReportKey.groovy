package com.junbo.order.jobs.subledger.revenue

import com.junbo.common.id.ItemId
import com.junbo.common.id.OrganizationId

/**
 * Created by acer on 2015/1/25.
 */
class RevenueReportKey {
    ItemId itemId
    OrganizationId seller
    String financialId
    String bankId
    String actionType
    String txnType
    boolean  mixedTxn

    ItemId getItemId() {
        return itemId
    }

    void setItemId(ItemId itemId) {
        this.itemId = itemId
    }

    OrganizationId getSeller() {
        return seller
    }

    void setSeller(OrganizationId seller) {
        this.seller = seller
    }

    String getFinancialId() {
        return financialId
    }

    void setFinancialId(String financialId) {
        this.financialId = financialId
    }

    String getBankId() {
        return bankId
    }

    void setBankId(String bankId) {
        this.bankId = bankId
    }

    String getActionType() {
        return actionType
    }

    void setActionType(String actionType) {
        this.actionType = actionType
    }

    String getTxnType() {
        return txnType
    }

    void setTxnType(String txnType) {
        this.txnType = txnType
    }

    boolean getMixedTxn() {
        return mixedTxn
    }

    void setMixedTxn(boolean mixedTxn) {
        this.mixedTxn = mixedTxn
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        RevenueReportKey that = (RevenueReportKey) o

        if (mixedTxn != that.mixedTxn) return false
        if (actionType != that.actionType) return false
        if (bankId != that.bankId) return false
        if (financialId != that.financialId) return false
        if (itemId != that.itemId) return false
        if (seller != that.seller) return false
        if (txnType != that.txnType) return false

        return true
    }

    int hashCode() {
        int result
        result = (itemId != null ? itemId.hashCode() : 0)
        result = 31 * result + (seller != null ? seller.hashCode() : 0)
        result = 31 * result + (financialId != null ? financialId.hashCode() : 0)
        result = 31 * result + (bankId != null ? bankId.hashCode() : 0)
        result = 31 * result + (actionType != null ? actionType.hashCode() : 0)
        result = 31 * result + (txnType != null ? txnType.hashCode() : 0)
        result = 31 * result + (mixedTxn ? 1 : 0)
        return result
    }
}
