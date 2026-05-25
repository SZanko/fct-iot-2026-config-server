package pt.nova.fct.iot.configserver.mapper

import org.springframework.stereotype.Component
import tech.mappie.api.ObjectMappie
import pt.nova.fct.iot.configserver.dto.IotConfigDto
import pt.nova.fct.iot.configserver.models.IotConfigModel

private object ModelToDtoMapper : ObjectMappie<IotConfigModel, IotConfigDto>() {
    override fun map(from: IotConfigModel) = mapping {
        to::name fromValue ""
    }
}

private object DtoToModelMapper : ObjectMappie<IotConfigDto, IotConfigModel>() {
    override fun map(from: IotConfigDto) = mapping {
        to::busStopId fromValue ""
        to::ldrLimit fromValue 0
    }
}

@Component
class IotConfigMapper {
    fun toDto(data: IotConfigModel): IotConfigDto = ModelToDtoMapper.map(data)
    fun toDto(data: List<IotConfigModel>): List<IotConfigDto> = data.map { ModelToDtoMapper.map(it) }
    fun toModel(data: IotConfigDto): IotConfigModel = DtoToModelMapper.map(data)
    fun toModel(data: List<IotConfigDto>): List<IotConfigModel> = data.map { DtoToModelMapper.map(it) }
}
