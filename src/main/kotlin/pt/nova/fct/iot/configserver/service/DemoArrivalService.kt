package pt.nova.fct.iot.configserver.service

import org.springframework.stereotype.Service
import pt.nova.fct.iot.configserver.dto.DemoArrivalAdjustRequest
import pt.nova.fct.iot.configserver.dto.DemoArrivalDto
import pt.nova.fct.iot.configserver.dto.DemoArrivalRequest
import pt.nova.fct.iot.configserver.models.DemoArrivalModel
import pt.nova.fct.iot.configserver.repos.DemoArrivalRepo
import pt.nova.fct.iot.configserver.service.exceptions.DemoArrivalNotFoundException
import java.time.Instant

@Service
class DemoArrivalService(private val repo: DemoArrivalRepo) {

    fun findByBusStopId(busStopId: String): DemoArrivalDto =
        repo.findByBusStopId(busStopId)
            .orElseThrow { DemoArrivalNotFoundException() }
            .toDto()

    fun setDemoArrival(busStopId: String, request: DemoArrivalRequest): DemoArrivalDto {
        val arrivalUnix = Instant.now().epochSecond + request.minutesAway * 60L
        val model = repo.findByBusStopId(busStopId).orElse(null)?.also {
            it.lineId = request.lineId
            it.headsign = request.headsign
            it.arrivalUnix = arrivalUnix
        } ?: DemoArrivalModel(
            busStopId = busStopId,
            lineId = request.lineId,
            headsign = request.headsign,
            arrivalUnix = arrivalUnix,
        )
        return repo.save(model).toDto()
    }

    fun adjustDemoArrival(busStopId: String, request: DemoArrivalAdjustRequest): DemoArrivalDto {
        val model = repo.findByBusStopId(busStopId)
            .orElseThrow { DemoArrivalNotFoundException() }
        model.arrivalUnix += request.deltaMinutes * 60L
        return repo.save(model).toDto()
    }

    fun deleteDemoArrival(busStopId: String) {
        val model = repo.findByBusStopId(busStopId)
            .orElseThrow { DemoArrivalNotFoundException() }
        repo.delete(model)
    }

    private fun DemoArrivalModel.toDto() = DemoArrivalDto(
        busStopId = busStopId,
        lineId = lineId,
        headsign = headsign,
        arrivalUnix = arrivalUnix,
    )
}
