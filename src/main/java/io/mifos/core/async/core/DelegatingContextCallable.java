package io.mifos.core.async.core;

import io.mifos.core.api.util.UserContext;
import io.mifos.core.api.util.UserContextHolder;
import io.mifos.core.lang.TenantContextHolder;

import java.util.concurrent.Callable;

public class DelegatingContextCallable<V> implements Callable<V> {

  private final Callable<V> delegate;
  private final String tenantIdentifier;
  private final UserContext userContext;

  DelegatingContextCallable(Callable<V> delegate) {
    this.delegate = delegate;
    this.tenantIdentifier = null;
    this.userContext = null;
  }

  DelegatingContextCallable(final Callable<V> delegate, final String tenantIdentifier,
                            final UserContext userContext) {
    super();
    this.delegate = delegate;
    this.tenantIdentifier = tenantIdentifier;
    this.userContext = userContext;
  }

  @Override
  public V call() throws Exception {
    try {
      TenantContextHolder.clear();
      if(this.tenantIdentifier != null) {
        TenantContextHolder.setIdentifier(this.tenantIdentifier);
      }
      UserContextHolder.clear();
      if (this.userContext != null) {
        UserContextHolder.setUserContext(this.userContext);
      }
      return this.delegate.call();
    } finally {
      TenantContextHolder.clear();
      UserContextHolder.clear();
    }
  }
}
