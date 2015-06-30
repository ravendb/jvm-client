package net.ravendb.client.changes;

import java.util.ArrayList;
import java.util.List;

import net.ravendb.abstractions.basic.EventHelper;
import net.ravendb.abstractions.basic.ExceptionEventArgs;
import net.ravendb.abstractions.closure.Action0;
import net.ravendb.abstractions.closure.Action1;
import net.ravendb.abstractions.data.*;


public class DatabaseConnectionState implements IChangesConnectionState {
  private final Action0 onZero;
  private int value;

  private Action1<DatabaseConnectionState> ensureConnection;

  private List<Action1<DocumentChangeNotification>> onDocumentChangeNotification = new ArrayList<>();
  private List<Action1<BulkInsertChangeNotification>> onBulkInsertChangeNotification = new ArrayList<>();
  private List<Action1<IndexChangeNotification>> onIndexChangeNotification = new ArrayList<>();
  private List<Action1<TransformerChangeNotification>> onTransformerChangeNotification = new ArrayList<>();
  private List<Action1<ReplicationConflictNotification>> onReplicationConflictNotification = new ArrayList<>();
  private List<Action1<DataSubscriptionChangeNotification>> onDataSubscriptionNotification = new ArrayList<>();

  private List<Action1<ExceptionEventArgs>> onError = new ArrayList<>();


  public List<Action1<DocumentChangeNotification>> getOnDocumentChangeNotification() {
    return onDocumentChangeNotification;
  }

  public List<Action1<BulkInsertChangeNotification>> getOnBulkInsertChangeNotification() {
    return onBulkInsertChangeNotification;
  }

  public List<Action1<IndexChangeNotification>> getOnIndexChangeNotification() {
    return onIndexChangeNotification;
  }

  public List<Action1<TransformerChangeNotification>> getOnTransformerChangeNotification() {
    return onTransformerChangeNotification;
  }

  public List<Action1<ReplicationConflictNotification>> getOnReplicationConflictNotification() {
    return onReplicationConflictNotification;
  }

  public List<Action1<DataSubscriptionChangeNotification>> getOnDataSubscriptionNotification() {
    return onDataSubscriptionNotification;
  }

  public List<Action1<ExceptionEventArgs>> getOnError() {
    return onError;
  }

  public void send(DocumentChangeNotification documentChangeNotification) {
    EventHelper.invoke(onDocumentChangeNotification, documentChangeNotification);
  }

  public void send(IndexChangeNotification indexChangeNotification) {
    EventHelper.invoke(onIndexChangeNotification, indexChangeNotification);
  }

  public void send(TransformerChangeNotification transformerChangeNotification) {
    EventHelper.invoke(onTransformerChangeNotification, transformerChangeNotification);
  }

  public void send(ReplicationConflictNotification replicationConflictNotification) {
    EventHelper.invoke(onReplicationConflictNotification, replicationConflictNotification);
  }

  public void send(BulkInsertChangeNotification bulkInsertChangeNotification) {
    EventHelper.invoke(onBulkInsertChangeNotification, bulkInsertChangeNotification);
  }

  public void send(DataSubscriptionChangeNotification dataSubscriptionChangeNotification) {
    EventHelper.invoke(onDataSubscriptionNotification, dataSubscriptionChangeNotification);
  }

  @Override
  public void error(Exception e) {
    EventHelper.invoke(onError, new ExceptionEventArgs(e));
  }

  public DatabaseConnectionState(Action0 onZero, Action1<DatabaseConnectionState> ensureConnection) {
    value =0;
    this.onZero = onZero;
    this.ensureConnection = ensureConnection;
  }

  @Override
  public void inc() {
    synchronized (this) {
      if (++value == 1)
        ensureConnection.apply(this);
    }
  }

  @Override
  public void dec() {
    synchronized (this) {
     if (--value == 0) {
       onZero.apply();
     }
    }
  }

}
