/*
 * File created on Mar 3, 2014 
 *
 * Copyright (c) 2014 Virginia Polytechnic Institute and State University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.soulwing.credo.service.crypto.jca;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import org.soulwing.credo.service.crypto.SecretKeyWrapper;
import org.soulwing.credo.service.pem.PemObjectBuilderFactory;

/**
 * A {@link SecretKeyWrapper} that delegates to a JCA {@link SecretKey}.
 *
 * @author Carl Harris
 */
public class JcaEncryptedSecretKeyWrapper implements SecretKeyWrapper {

  private final String transform;
  private final byte[] cipherText;  
  private final PemObjectBuilderFactory objectBuilderFactory;
  
  private PrivateKey privateKey;
  
  /**
   * Constructs a new instance.
   * @param transform the cryptographic transform that was applied to 
   *    encrypt the key
   * @param cipherText cipher text of secret key's DER encoding
   * @param objectBuilderFactory PEM object builder factory
   */
  public JcaEncryptedSecretKeyWrapper(String transform,
      byte[] cipherText, PemObjectBuilderFactory objectBuilderFactory) {
    this.transform = transform;
    this.cipherText = cipherText;
    this.objectBuilderFactory = objectBuilderFactory;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isPrivateKeyRequired() {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PrivateKey getPrivateKey() {
    return privateKey;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setPrivateKey(PrivateKey privateKey) {
    this.privateKey = privateKey;    
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getContent() {
    return objectBuilderFactory.newBuilder()
        .setType("ENCRYPTED SECRET KEY")
        .setHeader("Proc-Type", "4,ENCRYPTED")
        .setHeader("DEK-Info", transform)
        .append(cipherText)
        .build().getEncoded();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SecretKey derive() {
    int delimiter = transform.indexOf('/');
    if (delimiter == -1) {
      throw new IllegalArgumentException("illegal transform syntax: " 
          + transform);
    }
    try {
      String algorithm = transform.substring(0, delimiter);
      Cipher decipher = Cipher.getInstance(transform);
      decipher.init(Cipher.UNWRAP_MODE, privateKey);
      return (SecretKey) decipher.unwrap(cipherText, algorithm, 
          Cipher.SECRET_KEY);
    }
    catch (NoSuchAlgorithmException ex) {
      throw new RuntimeException(ex);
    }
    catch (NoSuchPaddingException ex) {
      throw new RuntimeException(ex);
    }
    catch (InvalidKeyException ex) {
      throw new RuntimeException(ex);
    }
  }

}
