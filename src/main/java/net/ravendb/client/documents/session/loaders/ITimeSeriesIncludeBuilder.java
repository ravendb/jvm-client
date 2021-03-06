package net.ravendb.client.documents.session.loaders;

import java.util.Date;

public interface ITimeSeriesIncludeBuilder<TBuilder> {
    TBuilder includeTimeSeries(String name);

    TBuilder includeTimeSeries(String name, Date from, Date to);
}
