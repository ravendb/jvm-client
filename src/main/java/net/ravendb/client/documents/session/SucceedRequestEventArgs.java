package net.ravendb.client.documents.session;

import net.ravendb.client.primitives.EventArgs;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.CloseableHttpResponse;

public class SucceedRequestEventArgs extends EventArgs {

    private String database;
    private String url;
    private CloseableHttpResponse response;
    private HttpRequest request;
    private int attemptNumber;

    public SucceedRequestEventArgs(String database, String url, CloseableHttpResponse response, HttpRequest request, int attemptNumber) {
        this.database = database;
        this.url = url;
        this.response = response;
        this.request = request;
        this.attemptNumber = attemptNumber;
    }

    public String getDatabase() {
        return database;
    }

    public String getUrl() {
        return url;
    }

    public CloseableHttpResponse getResponse() {
        return response;
    }

    public HttpRequest getRequest() {
        return request;
    }

    public int getAttemptNumber() {
        return attemptNumber;
    }
}
