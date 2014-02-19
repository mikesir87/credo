/*
 * File created on Feb 19, 2014 
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
package org.soulwing.credo.service.x509;

import java.io.IOException;

/**
 * A wrapper for a private key object implementation.
 *
 * @author Carl Harris
 */
public interface PrivateKeyWrapper {

  /**
   * Tests whether this private key requires a passphrase.
   * @return {@code true} if the key requires a passphrase
   */
  boolean isPassphraseRequired();
  
  /**
   * Gets the passphrase.
   * @return passphrase or {@code null} if none has been set
   */
  char[] getPassphrase();
  
  /**
   * Sets the passphrase
   * @param passphrase the passphrase to set
   */
  void setPassphrase(char[] passphrase);
  
  /**
   * Gets the content of this private key in a suitable string encoding
   * (typically PEM).
   * @return private key content
   * @throws IOException
   */
  String getContent() throws IOException, IllegalStateException;
  
}
