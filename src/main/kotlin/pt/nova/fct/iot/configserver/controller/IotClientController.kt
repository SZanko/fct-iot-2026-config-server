package pt.nova.fct.iot.configserver.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirements
import jakarta.annotation.security.PermitAll
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pt.nova.fct.iot.configserver.dto.DemoArrivalAdjustRequest
import pt.nova.fct.iot.configserver.dto.DemoArrivalDto
import pt.nova.fct.iot.configserver.dto.DemoArrivalRequest
import pt.nova.fct.iot.configserver.dto.IotEnvironmentDto
import pt.nova.fct.iot.configserver.dto.IotEnvironmentRequest
import pt.nova.fct.iot.configserver.dto.IotConfigDto
import pt.nova.fct.iot.configserver.service.DemoArrivalService
import pt.nova.fct.iot.configserver.service.IotEnvironmentService
import pt.nova.fct.iot.configserver.service.IotService


@RestController
@RequestMapping("/api/iot")
class IotClientController (
    private val iotService: IotService,
    private val iotEnvironmentService: IotEnvironmentService,
    private val demoArrivalService: DemoArrivalService,
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

    @GetMapping
    @Operation(summary = "Get all existing Configs")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Found configs")
        ]
    )
    fun getConfigs(@RequestParam page: Int, @RequestParam size: Int): ResponseEntity<List<IotConfigDto>> {
        val result = iotService.findConfigs(page, size)
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

    @SecurityRequirements
    @Operation(summary = "Get the latest environment reading for a bus stop")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Found latest reading"),
            ApiResponse(responseCode = "404", description = "Environment reading not found")
        ]
    )
    @GetMapping("/{id}/environment")
    @PermitAll
    fun findEnvironmentForBusStop(@PathVariable id: String): ResponseEntity<IotEnvironmentDto> {
        val result = iotEnvironmentService.findLatestByBusStopId(id)
        return ResponseEntity.ok(result)
    }

    @SecurityRequirements
    @Operation(summary = "Save the latest environment reading sent by a microcontroller")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Environment reading saved")
        ]
    )
    @PostMapping("/{id}/environment")
    @PermitAll
    fun saveEnvironmentForBusStop(
        @PathVariable id: String,
        @Valid @RequestBody request: IotEnvironmentRequest,
    ): ResponseEntity<IotEnvironmentDto> {
        val result = iotEnvironmentService.saveLatestReading(id, request)
        return ResponseEntity.ok(result)
    }

    @SecurityRequirements
    @Operation(summary = "Get the demo arrival for a bus stop")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Demo arrival found"),
            ApiResponse(responseCode = "404", description = "No demo arrival set for this stop"),
        ]
    )
    @GetMapping("/{id}/demo")
    @PermitAll
    fun getDemoArrival(@PathVariable id: String): ResponseEntity<DemoArrivalDto> {
        val result = demoArrivalService.findByBusStopId(id)
        return ResponseEntity.ok(result)
    }

    @Operation(summary = "Set the demo arrival for a bus stop (admin)")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Demo arrival set"),
            ApiResponse(responseCode = "401", description = "Authentication required"),
        ]
    )
    @PutMapping("/{id}/demo")
    fun setDemoArrival(
        @PathVariable id: String,
        @Valid @RequestBody request: DemoArrivalRequest,
    ): ResponseEntity<DemoArrivalDto> {
        val result = demoArrivalService.setDemoArrival(id, request)
        return ResponseEntity.ok(result)
    }

    @Operation(summary = "Advance or delay the demo bus arrival (admin)")
    @PatchMapping("/{id}/demo/adjust")
    fun adjustDemoArrival(
        @PathVariable id: String,
        @RequestBody request: DemoArrivalAdjustRequest,
    ): ResponseEntity<DemoArrivalDto> {
        val result = demoArrivalService.adjustDemoArrival(id, request)
        return ResponseEntity.ok(result)
    }

    @Operation(summary = "Remove the demo arrival for a bus stop (admin)")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Demo arrival removed"),
            ApiResponse(responseCode = "404", description = "No demo arrival found"),
            ApiResponse(responseCode = "401", description = "Authentication required"),
        ]
    )
    @DeleteMapping("/{id}/demo")
    fun deleteDemoArrival(@PathVariable id: String): ResponseEntity<Unit> {
        demoArrivalService.deleteDemoArrival(id)
        return ResponseEntity.noContent().build()
    }

}
