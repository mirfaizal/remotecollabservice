package com.edu.remotecollab.domain

import com.fasterxml.jackson.annotation.JsonProperty

data class AdditionalInfo (
        @JsonProperty
        val educationType: String?,

        @JsonProperty
        var status: String?,

        @JsonProperty
        val firstName: String?,

        @JsonProperty
        val lastName: String?,

        @JsonProperty
        var userRole: String?

)
