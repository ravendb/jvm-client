package net.ravendb.client.documents.session.operations;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Defaults;
import net.ravendb.client.documents.commands.GetDocumentsCommand;
import net.ravendb.client.documents.commands.GetDocumentsResult;
import net.ravendb.client.documents.session.DocumentInfo;
import net.ravendb.client.documents.session.InMemoryDocumentSessionOperations;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

public class LoadOperation {

    private final InMemoryDocumentSessionOperations _session;
    private static final Log logger = LogFactory.getLog(LoadOperation.class);

    private String[] _ids;
    private String[] _includes;
    private String[] countersToInclude;
    private boolean includeAllCounters;
    private final List<String> _idsToCheckOnServer = new ArrayList<>();
    private GetDocumentsResult _currentLoadResults;

    public LoadOperation(InMemoryDocumentSessionOperations _session) {
        this._session = _session;
    }

    public GetDocumentsCommand createRequest() {
        if (_idsToCheckOnServer.size() == 0) {
            return null;
        }

        if (_session.checkIfIdAlreadyIncluded(_ids, _includes != null ? Arrays.asList(_includes) : null)) {
            return null;
        }

        _session.incrementRequestCount();

        if (logger.isInfoEnabled()) {
            logger.info("Requesting the following ids " + String.join(",", _idsToCheckOnServer) + " from " + _session.storeIdentifier());
        }

        if (includeAllCounters) {
            return new GetDocumentsCommand(_idsToCheckOnServer.toArray(new String[0]), _includes, true, false);
        }

        return countersToInclude != null
                ? new GetDocumentsCommand(_idsToCheckOnServer.toArray(new String[0]), _includes, countersToInclude, false)
                : new GetDocumentsCommand(_idsToCheckOnServer.toArray(new String[0]), _includes, false);
    }

    public LoadOperation byId(String id) {
        if (id == null) {
            return this;
        }

        if (_ids == null) {
            _ids = new String[] { id };
        }

        if (_session.isLoadedOrDeleted(id)) {
            return this;
        }

        _idsToCheckOnServer.add(id);
        return this;
    }

    public LoadOperation withIncludes(String[] includes) {
        _includes = includes;
        return this;
    }

    public LoadOperation withCounters(String[] counters) {
        if (counters != null) {
            countersToInclude = counters;
        }
        return this;
    }

    public LoadOperation withAllCounters() {
        includeAllCounters = true;
        return this;
    }

    public LoadOperation byIds(String[] ids) {
        return byIds(Arrays.asList(ids));
    }

    public LoadOperation byIds(Collection<String> ids) {
        _ids = ids.toArray(new String[0]);

        Set<String> distinct = new TreeSet<>(String::compareToIgnoreCase);
        for (String id : ids) {
            if (id != null) {
                distinct.add(id);
            }
        }

        for (String id : distinct) {
            byId(id);
        }

        return this;
    }

    public <T> T getDocument(Class<T> clazz) {

        if (_session.noTracking) {
            /* TODO
             if (_currentLoadResults == null)
+                    throw new InvalidOperationException($"Cannot execute '{nameof(GetDocument)}' before operation execution.");
+
+                var document = _currentLoadResults.Results[0] as BlittableJsonReaderObject;
+                if (document == null)
+                    return default;
+
+                var documentInfo = DocumentInfo.GetNewDocumentInfo(document);
+
+                return _session.TrackEntity<T>(documentInfo);
             */
        }

        return getDocument(clazz, _ids[0]);
    }

    private <T> T getDocument(Class<T> clazz, String id) {
        if (id == null) {
            return Defaults.defaultValue(clazz);
        }

        if (_session.isDeleted(id)) {
            return Defaults.defaultValue(clazz);
        }

        DocumentInfo doc = _session.documentsById.getValue(id);
        if (doc != null) {
            return _session.trackEntity(clazz, doc);
        }


        doc = _session.includedDocumentsById.get(id);
        if (doc != null) {
            return _session.trackEntity(clazz, doc);
        }

        return Defaults.defaultValue(clazz);
    }

    public <T> Map<String, T> getDocuments(Class<T> clazz) {
        Map<String, T> finalResults = new TreeMap<>(String::compareToIgnoreCase);

        /* TODO
        if (_session.NoTracking)
+            {
+                if (_currentLoadResults == null)
+                    throw new InvalidOperationException($"Cannot execute '{nameof(GetDocuments)}' before operation execution.");
+
+                foreach (var id in _ids)
+                {
+                    if (id == null)
+                        continue;
+
+                    finalResults[id] = default;
+                }
+
+                foreach (var document in GetDocumentsFromResult(_currentLoadResults))
+                    finalResults[document.Id] = _session.TrackEntity<T>(document);
+
+                return finalResults;
+            }
         */

        for (String id : _ids) {
            if (id == null) {
                continue;
            }

            finalResults.put(id, getDocument(clazz, id));
        }

        return finalResults;

    }
    public void setResult(GetDocumentsResult result) {
        if (result == null) {
            return;
        }

        if (_session.noTracking) {
            _currentLoadResults = result;
            return;
        }

        _session.registerIncludes(result.getIncludes());

        /* TODO
         if (_includeAllCounters || _countersToInclude != null)
            {
                _session.RegisterCounters(result.CounterIncludes, _ids, _countersToInclude, _includeAllCounters);
            }
         */

        for (JsonNode document : result.getResults()) {
            if (document == null || document.isNull()) {
                continue;
            }

            DocumentInfo newDocumentInfo = DocumentInfo.getNewDocumentInfo((ObjectNode) document);
            _session.documentsById.add(newDocumentInfo);
        }

        _session.registerMissingIncludes(result.getResults(), result.getIncludes(), _includes);
    }
}
