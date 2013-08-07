package raven.abstractions.data;

import java.util.Map;
import java.util.TreeMap;

import org.codehaus.jackson.annotate.JsonIgnore;

public class GetRequest {
  private String url;
  private Map<String, String> headers;
  private String query;

  public GetRequest() {
    this.headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
  }

  public Map<String, String> getHeaders() {
    return headers;
  }
  public String getQuery() {
    return query;
  }
  public String getUrl() {
    return url;
  }
  public void setHeaders(Map<String, String> headers) {
    this.headers = headers;
  }
  public void setQuery(String query) {
    this.query = query;
  }
  public void setUrl(String url) {
    this.url = url;
  }

  @JsonIgnore
  public String getUrlAndQuery() {
    if (query == null) {
      return url;
    }
    if (query.startsWith("?")) {
      return url + query;
    }
    return url + "?" + query;
  }

}
