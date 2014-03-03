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
package org.soulwing.credo.service.crypto.jca;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.soulwing.credo.service.crypto.KeyGeneratorService;
import org.soulwing.credo.service.crypto.KeyPairWrapper;
import org.soulwing.credo.service.pem.PemObjectBuilderFactory;

/**
 * A {@link KeyGeneratorService} that is based on the JCA.
 *
 * @author Carl Harris
 */
@ApplicationScoped
public class JcaKeyGeneratorService implements KeyGeneratorService {

  @Inject
  protected PemObjectBuilderFactory objectBuilderFactory;
  
  private KeyPairGenerator keyPairGenerator;
  
  @PostConstruct
  public void init() {
    try {
      keyPairGenerator = KeyPairGenerator.getInstance("RSA");
    }
    catch (NoSuchAlgorithmException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public KeyPairWrapper generateKeyPair() {
    KeyPair keyPair = keyPairGenerator.generateKeyPair();
    return new JcaKeyPairWrapper(keyPair, objectBuilderFactory);
  }
  
}
