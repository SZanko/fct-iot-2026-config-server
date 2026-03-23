package pt.nova.fct.iot.configserver.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.annotation.security.PermitAll
import jakarta.websocket.server.PathParam
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pt.nova.fct.iot.configserver.dto.IotConfigDto
import pt.nova.fct.iot.configserver.service.IotService


@RestController
@RequestMapping("/api/iot")
class IotClientController (
    private val iotService: IotService,
) {
    @Operation(summary = "Get the Config for microcontroller")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Found config for Id"),
            ApiResponse(responseCode = "404", description = "Config not found")
        ]
    )
    @GetMapping("/{id}")
    @PermitAll
    fun findConfigForController(@PathVariable id: String): ResponseEntity<IotConfigDto> {
        val result = iotService.findConfigById(id)
        return ResponseEntity.ok(result)
    }

}