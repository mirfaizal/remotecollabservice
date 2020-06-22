package com.edu.remotecollab.domain

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import org.joda.time.DateTime
import java.util.UUID

@JsonInclude
data class DocumentInfo(
        @JsonProperty
        var id: UUID?,

        @JsonProperty
        var fileExtension: String,

        @JsonProperty
        var fileName: String,

        @JsonProperty
        val username: String,

        @JsonProperty
        val submittedDate: DateTime?,

        @JsonProperty
        var additionalInfo: AdditionalInfo,

        @JsonProperty
        var lastModifiedBy: String?
)
