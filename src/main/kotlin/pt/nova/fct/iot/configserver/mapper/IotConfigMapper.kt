package pt.nova.fct.iot.configserver.mapper

import org.springframework.data.domain.Page
import org.springframework.stereotype.Component
import tech.mappie.api.ObjectMappie
import pt.nova.fct.iot.configserver.dto.IotConfigDto
import pt.nova.fct.iot.configserver.models.IotConfigModel

private object ModelToDtoMapper : ObjectMappie<IotConfigModel, IotConfigDto>() {
    override fun map(from: IotConfigModel) = mapping {
        to::name fromValue from.name
        to::light fromValue from.ldrLimit
        to::temperature fromValue from.temperature
        to::latitude fromValue from.latitude
        to::longitude fromValue from.longitude
        to::id fromValue from.busStopId
    }
}

private object DtoToModelMapper : ObjectMappie<IotConfigDto, IotConfigModel>() {
    override fun map(from: IotConfigDto) = mapping {
        to::name fromValue from.name
        to::busStopId fromValue from.id
        to::ldrLimit fromValue from.light
        to::temperature fromValue from.temperature
        to::latitude fromValue from.latitude
        to::longitude fromValue from.longitude
    }
}

@Component
class IotConfigMapper {
    fun toDto(data: IotConfigModel): IotConfigDto = ModelToDtoMapper.map(data)
    fun toDto(data: List<IotConfigModel>): List<IotConfigDto> = data.map { ModelToDtoMapper.map(it) }
    fun toModel(data: IotConfigDto): IotConfigModel = DtoToModelMapper.map(data)
    fun toModel(data: List<IotConfigDto>): List<IotConfigModel> = data.map { DtoToModelMapper.map(it) }
}
