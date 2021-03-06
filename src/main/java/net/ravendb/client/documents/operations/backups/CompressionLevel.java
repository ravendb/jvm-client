package net.ravendb.client.documents.operations.backups;

import net.ravendb.client.primitives.UseSharpEnum;

@UseSharpEnum
public enum CompressionLevel {
    OPTIMAL,
    FASTEST,
    NO_COMPRESSION
}
