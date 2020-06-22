package com.edu.remotecollab.controller

import com.edu.remotecollab.domain.MimeType
import com.edu.remotecollab.service.DocumentService
import javassist.NotFoundException
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException
import org.everit.json.schema.ValidationException
import org.slf4j.LoggerFactory
import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@RestController
@RequestMapping("/documents")
class DocumentsController(private val service : DocumentService) {
    private final val log = LoggerFactory.getLogger(this.javaClass.name)!!

    @PostMapping
    fun uploadToS3(@RequestParam file: MultipartFile, @RequestParam metadata: String, @RequestParam clientIp: String):
        ResponseEntity<UUID> {
        return try {
            val uuid = service.uploadDocToS3(file, metadata, clientIp)
            ResponseEntity.ok(uuid)
        } catch (e: SecurityException) {
            log.error("Virus found!", e)
            ResponseEntity(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
        } catch (e: ValidationException) {
            log.error("Validation exception", e)
            ResponseEntity(HttpStatus.BAD_REQUEST)
        } catch (e: FileSizeLimitExceededException) {
            log.error("File size exception", e)
            ResponseEntity(HttpStatus.PAYLOAD_TOO_LARGE)
        } catch (e: Exception) {
            log.error("Unexpected exception uploading docs to S3", e)
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @DeleteMapping("/{uuid}")
    fun delete(@PathVariable uuid: String): ResponseEntity<HttpStatus> {
        try {
            service.deleteDoc(uuid)
        } catch (e: Exception) {
            return ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
        return ResponseEntity(HttpStatus.OK)
    }

    @GetMapping("/{uuid}")
    fun getDocumentById(@PathVariable uuid: String): ResponseEntity<InputStreamResource> {
        return try {
            val pair = service.getDocumentContentById(uuid)
            val headers = HttpHeaders()
            val extension = pair.second.split(".").last().toLowerCase()
            val contentType = MimeType.findValueByFileExtension(extension)
            headers.add("Content-Disposition", "attachment; filename=${pair.second} " +
                "filename*=${pair.second}")
            headers.accessControlExposeHeaders = listOf("Content-Disposition")

            ResponseEntity
                    .ok()
                    .contentType(
                            MediaType.parseMediaType(contentType))
                    .headers(headers)
                    .body(InputStreamResource(pair.first))
        } catch (e: NotFoundException) {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }
}
