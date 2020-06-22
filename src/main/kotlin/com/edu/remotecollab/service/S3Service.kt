package com.edu.remotecollab.service

import com.amazonaws.SdkClientException
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.DeleteObjectRequest
import com.amazonaws.services.s3.model.GetObjectRequest
import com.amazonaws.services.s3.model.ListObjectsV2Request
import com.amazonaws.services.s3.model.ListObjectsV2Result
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import com.amazonaws.services.s3.model.S3Object
import com.amazonaws.services.s3.model.S3ObjectSummary
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.InputStream

@Component
class S3Service(private var amazonS3: AmazonS3) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun uploadFile(multipartFile: MultipartFile, uuid: String, bucket: String): Boolean {
        try {
            val objectMetadata = ObjectMetadata()
            objectMetadata.contentType = "application/octet-stream"
            objectMetadata.contentLength = multipartFile.size
            val putObjectRequest = PutObjectRequest(bucket, uuid, multipartFile.inputStream, objectMetadata)
            amazonS3.putObject(putObjectRequest)
        } catch (e: SdkClientException) {
            logger.error("Error uploading document $uuid to $bucket", e)
            return false
        }
        return true
    }

    fun deleteFile(uuid: String, bucket: String): Boolean {
        try {
            val deleteObjectRequest = DeleteObjectRequest(bucket, uuid)
            logger.info("Deleting $uuid ...")
            amazonS3.deleteObject(deleteObjectRequest)
        } catch (e: SdkClientException) {
            logger.error("unable to delete $uuid")
            return false
        }
        return true
    }

    fun listObjects(bucket: String): List<S3ObjectSummary> {
        val listObjectsRequest: ListObjectsV2Request = ListObjectsV2Request().withBucketName(bucket)
        val listObjectsResult: ListObjectsV2Result = amazonS3.listObjectsV2(listObjectsRequest)

        return listObjectsResult.objectSummaries
    }

    fun retrieveDocument(documentId: String, filePath: String, bucket: String): InputStream {
        return try {
            amazonS3.getObject(bucket, documentId).objectContent
        } catch(e: SdkClientException) {
            logger.error("Error in retrieveDocument s3Service", e)
            throw e
        }
    }

    @Throws(RuntimeException::class)
    fun getObject(uuid: String, bucket: String): S3Object {
        val getObjectRequest = GetObjectRequest(bucket, uuid)
        return try {
            amazonS3.getObject(getObjectRequest)
        } catch (e: SdkClientException) {
            val message = "Unable to retrieve $uuid from $bucket"
            logger.error(message)
            throw RuntimeException(message, e)
        }
    }
}
