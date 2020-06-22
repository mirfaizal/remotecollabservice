CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE TABLE document_metadata
(
    id uuid DEFAULT uuid_generate_v4() PRIMARY KEY NOT NULL,
    bucketId varchar NOT NULL,
    filename varchar NOT NULL,
    fileExtension varchar NOT NULL,
    username varchar NOT NULL,
    createdDateTime timestamp NOT NULL,
    additionalInfo jsonb NOT NULL,
    lastModifiedBy varchar NOT NULL
)
WITH (
    OIDS = FALSE
);

--create indexes on elements in the jsonb object:
CREATE INDEX id_idx ON document_metadata (id);
CREATE INDEX bucketId_idx ON document_metadata (bucketId);
CREATE INDEX addlInfo_custId_idx ON document_metadata USING gin ((additionalInfo -> 'customerId'));
CREATE INDEX addlInfo_status_idx ON document_metadata USING gin ((additionalInfo -> 'status'));
