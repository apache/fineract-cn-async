package io.mifos.core.async.core;

import io.mifos.core.api.util.UserContext;
import io.mifos.core.api.util.UserContextHolder;
import io.mifos.core.lang.TenantContextHolder;

public class DelegatingContextRunnable implements Runnable {

  private final Runnable delegate;
  private final String tenantIdentifier;
  private final UserContext userContext;

  DelegatingContextRunnable(final Runnable delegate) {
    this.delegate = delegate;
    this.tenantIdentifier = null;
    this.userContext = null;
  }

  DelegatingContextRunnable(final Runnable delegate, final String tenantIdentifier,
                            final UserContext userContext) {
    super();
    this.delegate = delegate;
    this.tenantIdentifier = tenantIdentifier;
    this.userContext = userContext;
  }

  @Override
  public void run() {
    try {
      TenantContextHolder.clear();
      if(this.tenantIdentifier != null) {
        TenantContextHolder.setIdentifier(this.tenantIdentifier);
      }
      UserContextHolder.clear();
      if (this.userContext != null) {
        UserContextHolder.setUserContext(this.userContext);
      }
      this.delegate.run();
    } finally {
      TenantContextHolder.clear();
      UserContextHolder.clear();;
    }
  }
}
