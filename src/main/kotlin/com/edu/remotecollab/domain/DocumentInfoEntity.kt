package com.edu.remotecollab.domain

import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import org.hibernate.annotations.TypeDefs
import java.sql.Timestamp
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.validation.constraints.NotNull

@Entity(name = "document_metadata")
@TypeDefs(TypeDef(name = "jsonb", typeClass = JsonBinaryType::class))
data class DocumentInfoEntity(
        @Id
        @Column(name = "id")
        @GeneratedValue(generator = "UUID")
        @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
        var id: UUID? = null,

        @NotNull
        @Column(name = "bucketid")
        val bucketId: String,

        @NotNull
        @Column(name = "fileextension")
        val fileExtension: String,

        @NotNull
        @Column(name = "filename")
        val fileName: String,

        @NotNull
        val username: String,

        @NotNull
        @Type(type = "jsonb")
        @Column(name = "additionalinfo")
        val additionalInfo: AdditionalInfo,

        @NotNull
        @Column(name = "createddatetime")
        val createdDateTime: Timestamp,

        @Column(name = "lastmodifiedby")
        var lastModifiedBy: String
)
