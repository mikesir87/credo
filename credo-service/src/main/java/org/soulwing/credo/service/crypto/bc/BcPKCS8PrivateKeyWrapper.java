/*
 * File created on Mar 2, 2014 
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
package org.soulwing.credo.service.crypto.bc;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import org.apache.commons.lang.Validate;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8DecryptorProviderBuilder;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.pkcs.PKCSException;
import org.soulwing.credo.service.crypto.IncorrectPassphraseException;
import org.soulwing.credo.service.crypto.PrivateKeyWrapper;
import org.soulwing.credo.service.pem.PemObjectBuilderFactory;

/**
 * A {@link PrivateKeyWrapper} that wraps a Bouncy Castle PKCS8 
 * EncryptedPrivateKeyInfo object.
 *
 * @author Carl Harris
 */
public class BcPKCS8PrivateKeyWrapper implements PrivateKeyWrapper {

  private final PKCS8EncryptedPrivateKeyInfo delegate;
  private final KeyFactory keyFactory;
  private final PemObjectBuilderFactory objectBuilderFactory;
  
  private char[] passphrase;
  
  /**
   * Constructs a new instance.
   * @param delegate PKCS8 encryptedPrivateKeyInfo
   * @param keyFactory RSA key factory
   * @param objectBuilderFactory PEM object builder factory
   */
  public BcPKCS8PrivateKeyWrapper(PKCS8EncryptedPrivateKeyInfo delegate,
      KeyFactory keyFactory, PemObjectBuilderFactory objectBuilderFactory) {
    this.delegate = delegate;
    this.keyFactory = keyFactory;
    this.objectBuilderFactory = objectBuilderFactory;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isProtected() {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public char[] getProtectionParameter() {
    return passphrase;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setProtectionParameter(Object parameter) {
    Validate.isTrue(parameter instanceof char[], "requires a passphrase");
    this.passphrase = (char[]) parameter;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getContent() {
    try {
      return objectBuilderFactory.newBuilder()
          .setType("ENCRYPTED PRIVATE KEY")
          .append(delegate.getEncoded())
          .build().getEncoded();
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }
    

  /**
   * {@inheritDoc}
   */
  @Override
  public PrivateKey derive() {
    Validate.notNull(passphrase, "passphrase is required");    
    InputDecryptorProvider decryptor = createPrivateKeyDecryptor();
    try {
      PrivateKeyInfo keyInfo = delegate.decryptPrivateKeyInfo(decryptor);
      PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(
          keyInfo.getEncoded());
      return keyFactory.generatePrivate(keySpec);
    }
    catch (PKCSException ex) {
      throw new IncorrectPassphraseException();
    }
    catch (InvalidKeySpecException ex) {
      throw new RuntimeException(ex);
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  private InputDecryptorProvider createPrivateKeyDecryptor() {
    try {
      InputDecryptorProvider decryptor = 
          new JceOpenSSLPKCS8DecryptorProviderBuilder().build(passphrase);
      return decryptor;
    }
    catch (OperatorCreationException ex) {
      throw new RuntimeException(ex);
    }
  }

}
