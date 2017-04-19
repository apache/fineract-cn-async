package io.mifos.core.async.core;

import io.mifos.core.lang.TenantContextHolder;

import java.util.concurrent.Callable;

public class DelegatingTenantContextCallable<V> implements Callable<V> {

  private final Callable<V> delegate;
  private final String tenantIdentifier;

  DelegatingTenantContextCallable(final Callable<V> delegate, final String tenantIdentifier) {
    super();
    this.delegate = delegate;
    this.tenantIdentifier = tenantIdentifier;
  }

  @Override
  public V call() throws Exception {
    try {
      TenantContextHolder.clear();
      TenantContextHolder.setIdentifier(this.tenantIdentifier);
      return this.delegate.call();
    } finally {
      TenantContextHolder.clear();
    }
  }
}
