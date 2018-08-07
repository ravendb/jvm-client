package net.ravendb.client.documents.indexes;

import net.ravendb.client.primitives.UseSharpEnum;

@UseSharpEnum
public enum FieldIndexing {
    /**
     * Do not index the field value. This field can thus not be searched, but one can still access its contents provided it is stored.
     */
    NO,
    /**
     * Index the tokens produced by running the field's value through an Analyzer. This is useful for common text.
     */
    SEARCH,

    /**
     * Index the field's value without using an Analyzer, so it can be searched.  As no analyzer is used the
     * value will be stored as a single term. This is useful for unique Ids like product numbers.
     */
    EXACT,

    /**
     * Index the tokens produced by running the field's value through an Analyzer (same as Search),
     * store them in index and track term vector positions and offsets. This is mandatory when highlighting is used.
     */
    HIGHLIGHTING,

    /**
     *  Index this field using the default internal analyzer: LowerCaseKeywordAnalyzer
     */
    DEFAULT
}
