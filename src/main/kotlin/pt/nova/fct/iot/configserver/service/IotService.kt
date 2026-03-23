package pt.nova.fct.iot.configserver.service

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

    fun findConfigById(id: String): IotConfigDto {
        val result =  configRepo.findById(id = id)

        if(result.isPresent) {
            throw IotConfigNotFoundException()
        }

        return configMapper.toDto(result.get())
    }
}