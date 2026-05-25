package pt.nova.fct.iot.configserver.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirements
import jakarta.annotation.security.PermitAll
import jakarta.websocket.server.PathParam
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pt.nova.fct.iot.configserver.dto.IotConfigDto
import pt.nova.fct.iot.configserver.service.IotService


@RestController
@RequestMapping("/api/iot")
class IotClientController (
    private val iotService: IotService,
) {
    @SecurityRequirements
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

    @Operation(summary = "Add a new config for a microcontroller")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Config created"),
            ApiResponse(responseCode = "404", description = "Microcontroller not found"),
            ApiResponse(responseCode = "401", description = "Authentication failed")

        ]
    )
    @PostMapping
    fun addConfigForMicrocontroller(@RequestBody config: IotConfigDto): ResponseEntity<IotConfigDto> {
        val result = iotService.createNewConfig(config)
        return ResponseEntity.ok(result)
    }

}