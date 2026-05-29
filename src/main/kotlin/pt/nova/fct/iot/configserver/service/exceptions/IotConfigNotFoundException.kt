package pt.nova.fct.iot.configserver.service.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.NOT_FOUND)
class IotConfigNotFoundException : RuntimeException("The config was not found"){
}