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
package org.soulwing.credo.service.importer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.soulwing.credo.CredentialBuilderFactory;
import org.soulwing.credo.service.TimeOfDayService;
import org.soulwing.credo.service.crypto.CredentialBagFactory;

/**
 * A {@link CredentialImporterFactory} that produces 
 * {@link ConcreteCredentialImporter} objects.
 *
 * @author Carl Harris
 */
@ApplicationScoped
public class ConcreteCredentialImporterFactory
    implements CredentialImporterFactory {

  @Inject
  protected CredentialBagFactory credentialBagFactory;
  
  @Inject
  protected CredentialBuilderFactory credentialBuilderFactory;
  
  @Inject
  protected TimeOfDayService timeOfDayService;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public CredentialImporter newImporter() {
    return new ConcreteCredentialImporter(
        credentialBagFactory.newCredentialBag(), 
        credentialBuilderFactory, 
        timeOfDayService);
  }

}
