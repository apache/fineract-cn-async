/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.cn.async.core;


import java.util.Optional;
import org.apache.fineract.cn.api.util.UserContext;
import org.apache.fineract.cn.api.util.UserContextHolder;
import org.apache.fineract.cn.lang.TenantContextHolder;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class DelegatingContextRunnable implements Runnable {

  private final Runnable delegate;
  private final Optional<String> optionalTenantIdentifier;
  private final Optional<UserContext> optionalUserContext;

  DelegatingContextRunnable(final Runnable delegate, final String tenantIdentifier,
                            final UserContext userContext) {
    super();
    this.delegate = delegate;
    this.optionalTenantIdentifier = Optional.ofNullable(tenantIdentifier);
    this.optionalUserContext = Optional.ofNullable(userContext);
  }

  @Override
  public void run() {
    try {
      TenantContextHolder.clear();
      optionalTenantIdentifier.ifPresent(TenantContextHolder::setIdentifier);

      UserContextHolder.clear();
      optionalUserContext.ifPresent(UserContextHolder::setUserContext);

      this.delegate.run();
    } finally {
      TenantContextHolder.clear();
      UserContextHolder.clear();
    }
  }
}
