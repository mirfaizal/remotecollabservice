package com.edu.remotecollab.service

import com.amazonaws.SdkClientException
import com.edu.remotecollab.domain.DocumentInfo
import com.edu.remotecollab.domain.DocumentInfoEntity
import com.edu.remotecollab.domain.constants.Status
import com.edu.remotecollab.repository.DocumentInfoRepository
import com.edu.remotecollab.util.Util
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import javassist.NotFoundException
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException
import org.everit.json.schema.ValidationException
import org.everit.json.schema.loader.SchemaLoader
import org.hibernate.HibernateException
import org.json.JSONObject
import org.json.JSONTokener
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.InputStream
import java.sql.Timestamp
import java.util.UUID


@Component
class DocumentService(private var documentInfoRepository: DocumentInfoRepository,
                      private var utilities: Util,
                      private var s3Service: S3Service,
                      @Value("\${s3.uploadbucket}") private var uploadbucket: String,
                      @Value("\${file-size.upload-doc}") private var maxFileSize: Long) {

    private final val log = LoggerFactory.getLogger(javaClass)

    @Throws(SecurityException::class, ValidationException::class, FileSizeLimitExceededException::class)
    fun uploadDocToS3(multipartFile: MultipartFile, metadata: String, clientIp: String): UUID {

        validateJsonSchema(metadata)

        val documentInfo = getDocumentInfoFromMetadata(metadata)

        validateFileSize(multipartFile)

        val docEntity = saveMetadata(documentInfo, Status.S3_UPLOAD_SUCCESS.status)
        if (s3Service.uploadFile(multipartFile, docEntity.id!!.toString(), uploadbucket)) {
            return docEntity.id!!
        } else {
            log.error("file upload failed")
            documentInfoRepository.deleteById(docEntity.id!!)
            throw RuntimeException("file upload failed")
        }
    }

    fun getDocumentInfoFromMetadata(metadata: String): DocumentInfo {
        val mapper = jacksonObjectMapper()
        mapper.registerModule(JodaModule())
        val documentInfo = mapper.readValue(metadata, DocumentInfo::class.java)

        documentInfo.fileName = documentInfo.fileName.replace(" ", "_")
        documentInfo.fileName = utilities.stripNonAlphaNumeric(documentInfo.fileName)
        documentInfo.fileExtension = documentInfo.fileExtension.toLowerCase()

        return documentInfo
    }

    fun deleteDoc(uuid: String) {
        val documentOptional = documentInfoRepository.findById(UUID.fromString(uuid))
        if (documentOptional.isPresent) {
            try {
                documentInfoRepository.delete(documentOptional.get())
                s3Service.deleteFile(uuid, uploadbucket)
            } catch (e: SdkClientException) {
                log.error("Error deleting $uuid from s3", e)
                documentInfoRepository.save(documentOptional.get())
                throw e
            } catch (e: HibernateException) {
                log.error("Error deleting $uuid from database.", e)
                throw e
            }
        } else {
            log.error("Error deleting $uuid from database. No entry found")
        }
    }

    @Throws(NotFoundException::class)
    fun getDocumentContentById(uuid: String): Pair<InputStream, String> {
        val documentEntity = documentInfoRepository.findById(UUID.fromString(uuid))

        if (documentEntity.isPresent) {
            val s3Object = s3Service.getObject(uuid, uploadbucket)
            val entity = documentEntity.get()
            val fileName = utilities.stripNonAlphaNumeric(entity.fileName)
            return Pair(s3Object.objectContent, "$fileName.${entity.fileExtension}")
        } else {
            log.error("Resource not found for uuid $uuid")
            throw NotFoundException("Resource not found")
        }
    }

    fun saveMetadata(documentInfo: DocumentInfo, status: String): DocumentInfoEntity {
        documentInfo.additionalInfo.status = status
        val lastModifiedBy = utilities.getUserName()
        return documentInfoRepository.save(DocumentInfoEntity(bucketId = uploadbucket,
                fileExtension = documentInfo.fileExtension,
                fileName = documentInfo.fileName, username = documentInfo.username,
                createdDateTime = Timestamp(System.currentTimeMillis()),
                additionalInfo = documentInfo.additionalInfo, lastModifiedBy = lastModifiedBy))
    }

    @Throws(ValidationException::class)
    fun validateJsonSchema(metadata: String) {
        val jsonSchema = JSONObject(JSONTokener(this::class.java.getResourceAsStream("/schema/uploadDocument.json")))
        val jsonSubject = JSONObject(metadata)
        if (jsonSubject.get("fileExtension") != null) {
            jsonSubject.put("fileExtension", (jsonSubject.get("fileExtension") as String).toLowerCase())
        }
        val schema = SchemaLoader.load(jsonSchema)
        schema.validate(jsonSubject)
    }

    @Throws(FileSizeLimitExceededException::class)
    fun validateFileSize(file: MultipartFile) {
        val fileLength = file.size

        if(fileLength > maxFileSize) {
            throw FileSizeLimitExceededException("file too large", fileLength, maxFileSize)
        }
    }
}
