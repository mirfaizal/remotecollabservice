package com.edu.remotecollab.repository

import com.edu.remotecollab.domain.DocumentInfoEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface DocumentInfoRepository : CrudRepository<DocumentInfoEntity, UUID> {
    @Query(value = "select * FROM document_metadata WHERE jb_contains(additionalinfo -> 'status',  text(:status))",
            nativeQuery = true)
    fun findDocumentsByStatus(@Param("status") status: String) : List<DocumentInfoEntity>

}
