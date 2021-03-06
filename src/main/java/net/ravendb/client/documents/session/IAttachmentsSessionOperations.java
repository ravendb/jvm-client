package net.ravendb.client.documents.session;

import net.ravendb.client.documents.operations.attachments.AttachmentRequest;
import net.ravendb.client.documents.operations.attachments.CloseableAttachmentResult;
import net.ravendb.client.documents.operations.attachments.CloseableAttachmentsResult;

import java.util.List;

/**
 * Attachments advanced synchronous session operations
 */
public interface IAttachmentsSessionOperations extends IAttachmentsSessionOperationsBase {

    /**
     * Check if attachment exists
     * @param documentId Document Id
     * @param name Attachment name
     * @return true, if attachment exists
     */
    boolean exists(String documentId, String name);

    /**
     * Returns the attachment by the document id and attachment name.
     * @param documentId Document Id
     * @param name Name of attachment
     * @return Attachment
     */
    CloseableAttachmentResult get(String documentId, String name);

    /**
     * Returns the attachment by the entity and attachment name.
     * @param entity Entity
     * @param name Name of attachment
     * @return Attachment
     */
    CloseableAttachmentResult get(Object entity, String name);

    /**
     * Returns enumerator of attachment name and stream.
     * @param attachments Attachments to get
     * @return attachments
     */
    CloseableAttachmentsResult get(List<AttachmentRequest> attachments);

    /**
     * Returns the revision attachment by the document id and attachment name.
     * @param documentId Document Id
     * @param name Name of attachment
     * @param changeVector Change vector
     * @return Attachment
     */
    CloseableAttachmentResult getRevision(String documentId, String name, String changeVector);


}
