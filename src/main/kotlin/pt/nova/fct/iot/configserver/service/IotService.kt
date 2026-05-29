package pt.nova.fct.iot.configserver.service

import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import pt.nova.fct.iot.configserver.dto.BuzzerStageDto
import pt.nova.fct.iot.configserver.dto.IotConfigDto
import pt.nova.fct.iot.configserver.mapper.IotConfigMapper
import pt.nova.fct.iot.configserver.repos.BuzzerStageRepo
import pt.nova.fct.iot.configserver.repos.IotConfigRepo
import pt.nova.fct.iot.configserver.service.exceptions.IotConfigNotFoundException

@Service
class IotService(
    private val configRepo: IotConfigRepo,
    private val configMapper: IotConfigMapper,
    private val buzzerStageRepo: BuzzerStageRepo,
) {

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    fun findConfigById(id: String): IotConfigDto {
        val result = configRepo.findByBusStopId(id)

        if (result.isEmpty) {
            log.warn("Config with id '$id' was not found")
            throw IotConfigNotFoundException()
        }

        val stages = buzzerStageRepo.findByBusStopIdOrderByMinutesBeforeDesc(id)
            .map { BuzzerStageDto(id = it.id, minutesBefore = it.minutesBefore, buzzerType = it.buzzerType, buzzerDurationMs = it.buzzerDurationMs) }

        return configMapper.toDto(result.get()).copy(buzzerStages = stages)
    }

    fun createNewConfig(config: IotConfigDto): IotConfigDto {
        val toCreated = configMapper.toModel(config)

        val result = configRepo.save(toCreated);

        return configMapper.toDto(result)
    }

    fun findConfigs(page: Int, size: Int): List<IotConfigDto> {
        val page = Pageable.ofSize(size).withPage(page)
        val result = configRepo.findAll(page).toList()

        return configMapper.toDto(result)
    }
}
