package net.ravendb.client.documents.session;

import net.ravendb.client.documents.operations.compareExchange.CompareExchangeValue;

public interface IClusterTransactionOperationsBase {
    void deleteCompareExchangeValue(String key, long index);

    <T> void deleteCompareExchangeValue(CompareExchangeValue<T> item);

    <T> CompareExchangeValue<T> createCompareExchangeValue(String key, T value);
}
