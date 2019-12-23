package com.deepexi.devops.proxy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.KeyStore;

/**
 * @author linyuan - linyuan@deepexi.com
 * @since 2019-12-19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeyStoreModel {

    /**
     * keystore
     */
    private KeyStore keyStore;

    /**
     * password
     */
    private KeyStore.PasswordProtection password;

}
