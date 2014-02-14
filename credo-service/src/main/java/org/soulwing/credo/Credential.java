/*
 * File created on Feb 13, 2014 
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
package org.soulwing.credo;


/**
 * An abstract representation of an X.509 credential.
 *
 * @author Carl Harris
 */
public interface Credential {

  /**
   * Gets the simple name assigned to this credential.
   * @return name
   */
  String getName();
  
  /**
   * Sets the simple name assigned to this credential.
   * @param name the name to set
   */
  void setName(String name);

}