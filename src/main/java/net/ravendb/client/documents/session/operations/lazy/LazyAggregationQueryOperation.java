package net.ravendb.client.documents.session.operations.lazy;

import net.ravendb.client.documents.commands.multiGet.GetRequest;
import net.ravendb.client.documents.commands.multiGet.GetResponse;
import net.ravendb.client.documents.conventions.DocumentConventions;
import net.ravendb.client.documents.queries.IndexQuery;
import net.ravendb.client.documents.queries.QueryResult;
import net.ravendb.client.documents.queries.facets.FacetResult;
import net.ravendb.client.extensions.JsonExtensions;

import java.io.IOException;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class LazyAggregationQueryOperation implements ILazyOperation {

    private final DocumentConventions _conventions;
    private final IndexQuery _indexQuery;
    private final Consumer<QueryResult> _invokeAfterQueryExecuted;
    private final BiFunction<QueryResult, DocumentConventions, Map<String, FacetResult>> _processResults;

    public LazyAggregationQueryOperation(DocumentConventions conventions, IndexQuery indexQuery, Consumer<QueryResult> invokeAfterQueryExecuted,
                                         BiFunction<QueryResult, DocumentConventions, Map<String, FacetResult>> processResults) {
        _conventions = conventions;
        _indexQuery = indexQuery;
        _invokeAfterQueryExecuted = invokeAfterQueryExecuted;
        _processResults = processResults;
    }

    public GetRequest createRequest() {
        GetRequest request = new GetRequest();
        request.setUrl("/queries");
        request.setMethod("POST");
        request.setQuery("?queryHash=" + _indexQuery.getQueryHash());
        request.setContent(new IndexQueryContent(_conventions, _indexQuery));
        return request;
    }

    private Object result;
    private QueryResult queryResult;
    private boolean requiresRetry;

    @Override
    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    @Override
    public QueryResult getQueryResult() {
        return queryResult;
    }

    public void setQueryResult(QueryResult queryResult) {
        this.queryResult = queryResult;
    }

    @Override
    public boolean isRequiresRetry() {
        return requiresRetry;
    }

    public void setRequiresRetry(boolean requiresRetry) {
        this.requiresRetry = requiresRetry;
    }

    public void handleResponse(GetResponse response) {
        if (response.isForceRetry()) {
            result = null;
            requiresRetry = true;
            return;
        }

        try {
            QueryResult queryResult = JsonExtensions.getDefaultMapper().readValue(response.getResult(), QueryResult.class);
            handleResponse(queryResult);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleResponse(QueryResult queryResult) {
        _invokeAfterQueryExecuted.accept(queryResult);
        result = _processResults.apply(queryResult, _conventions);
        this.queryResult = queryResult;
    }
}
