package net.ravendb.client.test.issues;

import net.ravendb.client.RemoteTestBase;
import net.ravendb.client.documents.IDocumentStore;
import net.ravendb.client.documents.operations.counters.CountersDetail;
import net.ravendb.client.documents.operations.counters.GetCountersOperation;
import net.ravendb.client.documents.session.IDocumentSession;
import net.ravendb.client.documents.session.ISessionDocumentCounters;
import net.ravendb.client.infrastructure.entities.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RavenDB_15134Test extends RemoteTestBase {

    @Test
    public void getCountersOperationShouldReturnNullForNonExistingCounter() throws Exception {
        try (IDocumentStore store = getDocumentStore()) {
            String docId = "users/1";

            try (IDocumentSession session = store.openSession()) {
                session.store(new User(), docId);

                ISessionDocumentCounters c = session.countersFor(docId);

                c.increment("likes");
                c.increment("dislikes", 2);

                session.saveChanges();
            }

            CountersDetail vals = store.operations().send(new GetCountersOperation(docId, new String[] { "likes", "downloads", "dislikes" }));
            assertThat(vals.getCounters())
                    .hasSize(3);

            assertThat(vals.getCounters())
                    .containsNull();
            assertThat(vals.getCounters())
                    .anyMatch(x -> x != null && x.getTotalValue() == 1);
            assertThat(vals.getCounters())
                    .anyMatch(x -> x != null && x.getTotalValue() == 2);

            vals = store.operations().send(new GetCountersOperation(docId, new String[] { "likes", "downloads", "dislikes" }, true));
            assertThat(vals.getCounters())
                    .hasSize(3);

            assertThat(vals.getCounters())
                    .containsNull();
            assertThat(vals.getCounters())
                    .anyMatch(x -> x != null && x.getCounterValues().size() == 1);
        }
    }
}
