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
package org.soulwing.credo.service;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.nullValue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.soulwing.credo.CredentialBuilderFactory;
import org.soulwing.credo.service.x509.CertificateWrapper;
import org.soulwing.credo.service.x509.CredentialBag;
import org.soulwing.credo.service.x509.IncorrectPassphraseException;
import org.soulwing.credo.service.x509.PrivateKeyWrapper;

/**
 * Unit tests for {@link ConcreteCredentialImporter}.
 *
 * @author Carl Harris
 */
public class ConcreteCredentialImporterTest {

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();
  
  @Mock
  private CredentialBag bag;
 
  @Mock
  private CredentialBuilderFactory credentialBuilderFactory;
  
  @Mock
  private Errors errors;
  
  @Mock
  private PrivateKeyWrapper privateKey;

  @Mock
  private CertificateWrapper certificate;

  private final char[] passphrase = new char[0];

  private ConcreteCredentialImporter importer;
  
  @Before
  public void setUp() throws Exception {
    importer = new ConcreteCredentialImporter(bag, credentialBuilderFactory);
  }
  
  @Test
  public void testLoadFile() throws Exception {
    final InputStream inputStream = new ByteArrayInputStream(new byte[0]);
    context.checking(new Expectations() { { 
      oneOf(bag).addAllObjects(inputStream);
      will(returnValue(1));
    } });
    
    importer.loadFile(inputStream);    
  }
  
  @Test(expected = IOException.class)
  public void testLoadFileThrowsIOException() throws Exception {
    final InputStream inputStream = new ByteArrayInputStream(new byte[0]);
    context.checking(new Expectations() { { 
      oneOf(bag).addAllObjects(inputStream);
      will(throwException(new IOException()));
    } });
    
    importer.loadFile(inputStream);
  }

  @Test(expected = NoContentException.class)
  public void testLoadFileWithNoContent() throws Exception {
    final InputStream inputStream = new ByteArrayInputStream(new byte[0]);
    context.checking(new Expectations() { { 
      oneOf(bag).addAllObjects(inputStream);
      will(returnValue(0));
    } });
    
    importer.loadFile(inputStream);
  }
  
  @Test(expected = ImportException.class)
  public void testVerifyWithNoPrivateKey() throws Exception {
    context.checking(new Expectations() { { 
      oneOf(bag).findPrivateKey();
      will(returnValue(null));
      oneOf(errors).addError(with(containsString("NoPrivateKey")),
          with(emptyArray()));
    } });
    
    importer.validate(errors);
  }
  
  @Test(expected = ImportException.class)
  public void testVerifyWithMultiplePrivateKeys() throws Exception {
    context.checking(new Expectations() { { 
      exactly(2).of(bag).findPrivateKey();
      will(onConsecutiveCalls(
          returnValue(privateKey),
          returnValue(privateKey)));
      oneOf(bag).removeObject(with(same(privateKey)));
      will(returnValue(true));
      oneOf(errors).addError(with(containsString("Multiple")),
          with(emptyArray()));
    } });
    
    importer.validate(errors);
  }
  
  @Test(expected = PassphraseException.class)
  public void testVerifyWithIncorrectPassphrase() throws Exception {
    context.checking(new Expectations() { { 
      exactly(2).of(bag).findPrivateKey();
      will(onConsecutiveCalls(
          returnValue(privateKey),
          returnValue(null)));
      oneOf(bag).removeObject(with(same(privateKey)));
      will(returnValue(true));
      oneOf(privateKey).setPassphrase(with(same(passphrase)));
      oneOf(bag).findSubjectCertificate(with(same(privateKey)));
      will(throwException(new IncorrectPassphraseException()));
    } });
    
    importer.setPassphrase(passphrase);
    importer.validate(errors);
  }
  
  @Test(expected = ImportException.class)
  public void testVerifyWhenSubjectCertificateNotFound() throws Exception {
    context.checking(new Expectations() { { 
      exactly(2).of(bag).findPrivateKey();
      will(onConsecutiveCalls(
          returnValue(privateKey),
          returnValue(null)));
      oneOf(bag).removeObject(with(same(privateKey)));
      will(returnValue(true));
      allowing(privateKey).setPassphrase(with(nullValue(char[].class)));
      oneOf(bag).findSubjectCertificate(with(same(privateKey)));
      will(returnValue(null));
      oneOf(errors).addError(with(containsString("NoSubject")), 
          with(emptyArray()));
    } });

    importer.validate(errors);
  }
  
  @Test
  public void testVerifyWhenAuthorityChainEmpty() throws Exception {
    context.checking(new Expectations() { { 
      exactly(2).of(bag).findPrivateKey();
      will(onConsecutiveCalls(
          returnValue(privateKey),
          returnValue(null)));
      oneOf(bag).removeObject(with(same(privateKey)));
      will(returnValue(true));
      allowing(privateKey).setPassphrase(with(nullValue(char[].class)));
      oneOf(bag).findSubjectCertificate(with(same(privateKey)));
      will(returnValue(certificate));
      oneOf(bag).findAuthorityCertificates(certificate);
      will(returnValue(Collections.emptyList()));
      oneOf(errors).addWarning(with(containsString("Incomplete")), 
          with(emptyArray()));
    } });

    importer.validate(errors);
  }

  @Test
  public void testVerifyWhenAuthorityChainIncomplete() throws Exception {
    context.checking(new Expectations() { { 
      exactly(2).of(bag).findPrivateKey();
      will(onConsecutiveCalls(
          returnValue(privateKey),
          returnValue(null)));
      oneOf(bag).removeObject(with(same(privateKey)));
      will(returnValue(true));
      allowing(privateKey).setPassphrase(with(nullValue(char[].class)));
      oneOf(bag).findSubjectCertificate(with(same(privateKey)));
      will(returnValue(certificate));
      oneOf(bag).findAuthorityCertificates(certificate);
      will(returnValue(Collections.singletonList(certificate)));
      oneOf(certificate).isSelfSigned();
      will(returnValue(false));
      oneOf(errors).addWarning(with(containsString("Incomplete")), 
          with(emptyArray()));
    } });

    importer.validate(errors);
  }

  
}