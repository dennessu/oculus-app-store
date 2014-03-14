package com.junbo.email.clientproxy.impl.mandrill

/**
 * Created by Wei on 3/14/14.
 */
enum SendStatus {
    SENT('sent'),
    QUEUED('queued'),
    INVALID('invalid'),
    REJECTED('rejected'),
    SCHEDULED('scheduled')

    final String status

    SendStatus(String status) {
        this.status = status
    }
}