package pt.nova.fct.iot.configserver.service

import org.springframework.stereotype.Service
import pt.nova.fct.iot.configserver.dto.AdminStopDto
import pt.nova.fct.iot.configserver.dto.BuzzerStageDto
import pt.nova.fct.iot.configserver.dto.DemoArrivalDto
import pt.nova.fct.iot.configserver.dto.StopConfigRequest
import pt.nova.fct.iot.configserver.models.BuzzerStageModel
import pt.nova.fct.iot.configserver.models.IotConfigModel
import pt.nova.fct.iot.configserver.repos.BuzzerStageRepo
import pt.nova.fct.iot.configserver.repos.DemoArrivalRepo
import pt.nova.fct.iot.configserver.repos.IotConfigRepo
import pt.nova.fct.iot.configserver.repos.IotEnvironmentRepo
import java.time.Instant

private const val ACTIVE_THRESHOLD_SECS = 180L

@Service
class AdminService(
    private val environmentRepo: IotEnvironmentRepo,
    private val configRepo: IotConfigRepo,
    private val demoRepo: DemoArrivalRepo,
    private val buzzerStageRepo: BuzzerStageRepo,
) {

    fun getAllStops(): List<AdminStopDto> {
        val nowSecs = Instant.now().epochSecond
        return environmentRepo.findAll()
            .map { env ->
                val config = configRepo.findByBusStopId(env.busStopId).orElse(null)
                val demo = demoRepo.findByBusStopId(env.busStopId).orElse(null)
                val stages = buzzerStageRepo.findByBusStopIdOrderByMinutesBeforeDesc(env.busStopId).map { it.toDto() }
                val secsAgo = nowSecs - env.updatedAt.epochSecond
                AdminStopDto(
                    stopId = env.busStopId,
                    deviceName = config?.name ?: env.busStopId,
                    temperature = env.temperature,
                    darkOutside = env.darkOutside,
                    isActive = secsAgo < ACTIVE_THRESHOLD_SECS,
                    lastSeenSeconds = secsAgo,
                    buzzerEnabled = config?.buzzerEnabled ?: false,
                    buzzerType = config?.buzzerType ?: "single",
                    buzzerDurationMs = config?.buzzerDurationMs ?: 200,
                    buzzerIntervalMs = config?.buzzerIntervalMs ?: 1000,
                    lightsEnabled = config?.lightsEnabled ?: false,
                    ldrThreshold = config?.ldrLimit ?: 2800,
                    fanTemperatureThreshold = config?.temperature ?: 30,
                    demo = demo?.let { DemoArrivalDto(it.busStopId, it.lineId, it.headsign, it.arrivalUnix) },
                    buzzerStages = stages,
                )
            }
            .sortedWith(compareByDescending<AdminStopDto> { it.isActive }.thenBy { it.stopId })
    }

    fun updateConfig(stopId: String, request: StopConfigRequest): AdminStopDto {
        val config = configRepo.findByBusStopId(stopId).orElse(null) ?: IotConfigModel(
            busStopId = stopId, name = stopId, ldrLimit = 128, temperature = 25,
        )
        config.buzzerEnabled = request.buzzerEnabled
        config.buzzerType = request.buzzerType
        config.buzzerDurationMs = request.buzzerDurationMs
        config.buzzerIntervalMs = request.buzzerIntervalMs
        config.lightsEnabled = request.lightsEnabled
        config.ldrLimit = request.ldrThreshold
        config.temperature = request.fanTemperatureThreshold
        configRepo.save(config)

        val nowSecs = Instant.now().epochSecond
        val env = environmentRepo.findByBusStopId(stopId).orElse(null)
        val demo = demoRepo.findByBusStopId(stopId).orElse(null)
        val stages = buzzerStageRepo.findByBusStopIdOrderByMinutesBeforeDesc(stopId).map { it.toDto() }
        val secsAgo = env?.let { nowSecs - it.updatedAt.epochSecond } ?: -1L
        return AdminStopDto(
            stopId = stopId,
            deviceName = config.name,
            temperature = env?.temperature ?: 0.0,
            darkOutside = env?.darkOutside ?: false,
            isActive = secsAgo in 0..<ACTIVE_THRESHOLD_SECS,
            lastSeenSeconds = secsAgo,
            buzzerEnabled = config.buzzerEnabled,
            buzzerType = config.buzzerType,
            buzzerDurationMs = config.buzzerDurationMs,
            buzzerIntervalMs = config.buzzerIntervalMs,
            lightsEnabled = config.lightsEnabled,
            ldrThreshold = config.ldrLimit,
            fanTemperatureThreshold = config.temperature,
            demo = demo?.let { DemoArrivalDto(it.busStopId, it.lineId, it.headsign, it.arrivalUnix) },
            buzzerStages = stages,
        )
    }

    fun addBuzzerStage(stopId: String, request: BuzzerStageDto): BuzzerStageDto {
        val model = BuzzerStageModel(
            busStopId = stopId,
            minutesBefore = request.minutesBefore,
            buzzerType = request.buzzerType,
            buzzerDurationMs = request.buzzerDurationMs,
        )
        return buzzerStageRepo.save(model).toDto()
    }

    fun deleteBuzzerStage(stageId: String) {
        buzzerStageRepo.deleteById(stageId)
    }

    private fun BuzzerStageModel.toDto() = BuzzerStageDto(
        id = id, minutesBefore = minutesBefore, buzzerType = buzzerType, buzzerDurationMs = buzzerDurationMs,
    )
}
