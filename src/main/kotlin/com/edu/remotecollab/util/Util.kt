package com.edu.remotecollab.util

import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component

@Component
class Util() {

    val logger = LoggerFactory.getLogger(this.javaClass)

    fun stripNonAlphaNumeric(string: String): String {
        val alphaNumericRegex = Regex("[^a-zA-Z\\d._\\-]")
        return alphaNumericRegex.replace(string, "")
    }

    fun getUserName(): String {
        val context = SecurityContextHolder.getContext()
        return if (context.authentication == null) {
            "SYSTEM"
        } else {
            val principal = context.authentication.principal
            logger.info(principal.toString())
            when (principal) {
                is UserDetails -> {
                    logger.info("Username: ${principal.username}")
                    principal.username
                }
                else -> {
                    try {

                        // Get user Id from Security Context
                        "userName"
                    } catch (e: Exception) {
                        logger.info("Token does not have loginid, set to SYSTEM")
                        "SYSTEM"
                    }
                }
            }
        }
    }
}
