package pt.nova.fct.iot.configserver.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import pt.nova.fct.iot.configserver.dto.IotConfigDto
import pt.nova.fct.iot.configserver.mapper.IotConfigMapper
import pt.nova.fct.iot.configserver.repos.IotConfigRepo
import pt.nova.fct.iot.configserver.service.exceptions.IotConfigNotFoundException

@Service
class IotService(
    private val configRepo: IotConfigRepo,
    private val configMapper: IotConfigMapper,
) {

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    fun findConfigById(id: String): IotConfigDto {
        val result = configRepo.findById(id)

        if (result.isEmpty) {
            log.warn("Config with id '$id' was not found")
            throw IotConfigNotFoundException()
        }

        return configMapper.toDto(result.get())
    }

    fun createNewConfig(config: IotConfigDto): IotConfigDto {
        val toCreated = configMapper.toModel(config)

        val result = configRepo.save(toCreated);

        return configMapper.toDto(result)
    }
}