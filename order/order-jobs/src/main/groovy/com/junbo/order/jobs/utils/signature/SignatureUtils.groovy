package com.junbo.order.jobs.utils.signature

import com.junbo.crypto.spec.model.ItemCryptoMessage
import com.junbo.crypto.spec.resource.ItemCryptoResource
import groovy.transform.CompileStatic
import org.apache.commons.codec.binary.Base64
import org.springframework.stereotype.Component

import javax.annotation.Resource
import java.nio.file.Files
import java.nio.file.Paths
import java.security.MessageDigest

/**
 * The SignatureUtils class.
 */
@CompileStatic
@Component('order.SignatureUtils')
class SignatureUtils {

    private final static String DIGEST_ALGORITHM = 'SHA1'

    @Resource(name = 'order.cryptoClient')
    private ItemCryptoResource itemCryptoResource

    String generateRSASignatureBase64Encoded(File file, String itemId) {
        // generate file digest
        MessageDigest messageDigest = MessageDigest.getInstance(DIGEST_ALGORITHM)
        byte [] bytes = Files.readAllBytes(Paths.get(file.path))
        messageDigest.update(bytes)
        byte []digest = messageDigest.digest()
        ItemCryptoMessage itemCryptoMessage = itemCryptoResource.sign(itemId, new ItemCryptoMessage(message: Base64.encodeBase64String(digest)), true).get()
        return itemCryptoMessage.message
    }
}
