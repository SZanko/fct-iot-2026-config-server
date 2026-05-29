package pt.nova.fct.iot.configserver.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import pt.nova.fct.iot.configserver.dto.IotEnvironmentDto
import pt.nova.fct.iot.configserver.dto.IotEnvironmentRequest
import pt.nova.fct.iot.configserver.models.IotEnvironmentModel
import pt.nova.fct.iot.configserver.repos.IotEnvironmentRepo
import pt.nova.fct.iot.configserver.service.exceptions.IotEnvironmentNotFoundException
import java.time.Instant

@Service
class IotEnvironmentService(
    private val environmentRepo: IotEnvironmentRepo,
) {

    companion object {
        private val log = LoggerFactory.getLogger(IotEnvironmentService::class.java)
    }

    fun findLatestByBusStopId(busStopId: String): IotEnvironmentDto {
        val result = environmentRepo.findByBusStopId(busStopId)

        if (result.isEmpty) {
            log.warn("Environment reading for bus stop '$busStopId' was not found")
            throw IotEnvironmentNotFoundException()
        }

        return result.get().toDto()
    }

    fun saveLatestReading(busStopId: String, request: IotEnvironmentRequest): IotEnvironmentDto {
        val now = Instant.now()
        val existing = environmentRepo.findByBusStopId(busStopId)

        val reading = if (existing.isPresent) {
            existing.get().also {
                it.temperature = request.temperature
                it.darkOutside = request.darkOutside
                it.updatedAt = now
            }
        } else {
            IotEnvironmentModel(
                busStopId = busStopId,
                temperature = request.temperature,
                darkOutside = request.darkOutside,
                updatedAt = now,
            )
        }

        return environmentRepo.save(reading).toDto()
    }

    private fun IotEnvironmentModel.toDto(): IotEnvironmentDto =
        IotEnvironmentDto(
            id = busStopId,
            temperature = temperature,
            darkOutside = darkOutside,
            updatedAt = updatedAt,
        )
}
