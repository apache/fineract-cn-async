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

import org.apache.fineract.cn.api.util.UserContext;
import org.apache.fineract.cn.api.util.UserContextHolder;
import org.apache.fineract.cn.lang.TenantContextHolder;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class DelegatingContextExecutor implements AsyncTaskExecutor {

  private final DelegatingSecurityContextAsyncTaskExecutor delegate;

  public DelegatingContextExecutor(final DelegatingSecurityContextAsyncTaskExecutor delegate) {
    super();
    this.delegate = delegate;
  }

  @Override
  public void execute(final Runnable task, final long startTimeout) {
    final Runnable taskWrapper = this.wrap(task);
    this.delegate.execute(taskWrapper, startTimeout);
  }

  @Override
  public Future<?> submit(final Runnable task) {
    final Runnable taskWrapper = this.wrap(task);
    return this.delegate.submit(taskWrapper);
  }

  @Override
  public <T> Future<T> submit(final Callable<T> task) {
    final Callable<T> taskWrapper = this.wrap(task);
    return this.delegate.submit(taskWrapper);
  }

  @Override
  public void execute(final Runnable task) {
    final Runnable taskWrapper = this.wrap(task);
    this.delegate.execute(taskWrapper);
  }

  private Runnable wrap(final Runnable task) {
    final String tenantIdentifier = TenantContextHolder.identifier().orElse(null);
    final UserContext userContext = UserContextHolder.getUserContext().orElse(null);
    return new DelegatingContextRunnable(task, tenantIdentifier, userContext);
  }

  private <T> Callable<T> wrap(final Callable<T> task) {
    final String tenantIdentifier = TenantContextHolder.identifier().orElse(null);
    final UserContext userContext = UserContextHolder.getUserContext().orElse(null);
    return new DelegatingContextCallable<>(task, tenantIdentifier, userContext);
  }
}
