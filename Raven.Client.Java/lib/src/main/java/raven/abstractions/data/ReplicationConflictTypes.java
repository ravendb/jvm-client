package raven.abstractions.data;

import raven.abstractions.basic.SerializeUsingValue;
import raven.abstractions.basic.UseSharpEnum;

@UseSharpEnum
@SerializeUsingValue
public enum ReplicationConflictTypes {
  NONE(0),

  DOCUMENT_REPLICATION_CONFLICT(1),

  ATTACHMENT_REPLICATION_CONFLICT(2);

  private int value;

  public int getValue() {
    return value;
  }

  private ReplicationConflictTypes(int value) {
    this.value = value;
  }

}
