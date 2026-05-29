package pt.nova.fct.iot.configserver.controller

import org.slf4j.LoggerFactory
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.RestControllerAdvice
import pt.nova.fct.iot.configserver.service.exceptions.DemoArrivalNotFoundException
import pt.nova.fct.iot.configserver.service.exceptions.IotConfigNotFoundException
import pt.nova.fct.iot.configserver.service.exceptions.IotEnvironmentNotFoundException

@RestControllerAdvice
class ApiExceptionHandler {
    companion object {
        private val log = LoggerFactory.getLogger(ApiExceptionHandler::class.java)
    }

    @ExceptionHandler(IotConfigNotFoundException::class)
    fun handleIotConfigNotFound(): ResponseEntity<Unit> = ResponseEntity.notFound().build()

    @ExceptionHandler(IotEnvironmentNotFoundException::class)
    fun handleIotEnvironmentNotFound(): ResponseEntity<Unit> = ResponseEntity.notFound().build()

    @ExceptionHandler(DemoArrivalNotFoundException::class)
    fun handleDemoArrivalNotFound(): ResponseEntity<Unit> = ResponseEntity.notFound().build()

    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthenticationException(): ResponseEntity<Unit> =
        ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

    @ExceptionHandler(MethodArgumentNotValidException::class, HttpMessageNotReadableException::class)
    fun handleBadRequest(): ResponseEntity<Unit> = ResponseEntity.badRequest().build()

    @ExceptionHandler(Exception::class)
    fun handleUnexpectedException(exception: Exception): ResponseEntity<Unit> {
        log.error("Unhandled exception: {}: {}", exception.javaClass.simpleName, exception.message)
        return ResponseEntity.internalServerError().build()
    }
}
