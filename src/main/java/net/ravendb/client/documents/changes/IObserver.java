package net.ravendb.client.documents.changes;

public interface IObserver<T> {
    void onNext(T value);

    void onError(Exception error);

    void onCompleted();
}
