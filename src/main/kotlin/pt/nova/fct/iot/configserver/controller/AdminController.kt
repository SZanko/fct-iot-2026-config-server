package pt.nova.fct.iot.configserver.controller

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pt.nova.fct.iot.configserver.dto.AdminStopDto
import pt.nova.fct.iot.configserver.dto.BuzzerStageDto
import pt.nova.fct.iot.configserver.dto.StopConfigRequest
import pt.nova.fct.iot.configserver.service.AdminService

@RestController
@RequestMapping("/api/admin")
class AdminController(private val adminService: AdminService) {

    @GetMapping("/stops")
    fun getAllStops(): ResponseEntity<List<AdminStopDto>> =
        ResponseEntity.ok(adminService.getAllStops())

    @PutMapping("/stops/{stopId}/config")
    fun updateConfig(
        @PathVariable stopId: String,
        @Valid @RequestBody request: StopConfigRequest,
    ): ResponseEntity<AdminStopDto> =
        ResponseEntity.ok(adminService.updateConfig(stopId, request))

    @PostMapping("/stops/{stopId}/stages")
    fun addStage(
        @PathVariable stopId: String,
        @Valid @RequestBody request: BuzzerStageDto,
    ): ResponseEntity<BuzzerStageDto> =
        ResponseEntity.ok(adminService.addBuzzerStage(stopId, request))

    @DeleteMapping("/stages/{stageId}")
    fun deleteStage(@PathVariable stageId: String): ResponseEntity<Unit> {
        adminService.deleteBuzzerStage(stageId)
        return ResponseEntity.noContent().build()
    }
}
