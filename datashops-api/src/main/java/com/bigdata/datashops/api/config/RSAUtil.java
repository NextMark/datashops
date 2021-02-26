package com.bigdata.datashops.api.config;

import java.io.DataInputStream;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class RSAUtil {
    private final static Logger LOG = LoggerFactory.getLogger(RSAUtil.class);

    private final String algorithm = "RSA";

    private byte[] replaceAndBase64Decode(final String file, final String headReplace, final String tailReplace)
            throws Exception {
        ClassPathResource resource = new ClassPathResource(file);
        InputStream inputStream = resource.getInputStream();
        final DataInputStream dis = new DataInputStream(inputStream);
        final byte[] keyBytes = new byte[inputStream.available()];
        dis.readFully(keyBytes);
        dis.close();
        final String temp = new String(keyBytes);
        String publicKeyPEM = temp.replace(headReplace, "");
        publicKeyPEM = publicKeyPEM.replace(tailReplace, "");

        return Base64.decodeBase64(publicKeyPEM);
    }

    public PublicKey loadPemPublicKey(final String pem) {
        try {
            final byte[] decoded =
                    replaceAndBase64Decode(pem, "-----BEGIN PUBLIC KEY-----\n", "-----END PUBLIC KEY-----");
            final X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
            final KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
            return keyFactory.generatePublic(spec);
        } catch (final Exception e) {
            LOG.error(e.getMessage());
            return null;
        }
    }

    public PrivateKey loadPemPrivateKey(final String pem) {
        try {
            final byte[] decoded =
                    replaceAndBase64Decode(pem, "-----BEGIN PRIVATE KEY-----\n", "-----END PRIVATE KEY-----");
            final PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
            final KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
            return keyFactory.generatePrivate(spec);
        } catch (final Exception e) {
            LOG.error("", e);
            return null;
        }
    }
}
