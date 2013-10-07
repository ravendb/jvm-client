package raven.client.utils;

import raven.abstractions.exceptions.OperationCancelledException;


public class CancellationTokenSource {

  private boolean cancelled = false;



  public CancellationToken getToken() {
    return new CancellationToken();
  }

  public class CancellationToken {
    private CancellationToken() {

    }

    public void throwIfCancellationRequested() {
      if (cancelled) {
        throw new OperationCancelledException();
      }
    }
  }

  public void cancel() {
    cancelled = true;
  }
}
