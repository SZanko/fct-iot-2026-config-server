package pt.nova.fct.iot.configserver.mapper

import org.mapstruct.Mapper
import org.mapstruct.MappingConstants
import org.springframework.context.annotation.Bean
import pt.nova.fct.iot.configserver.dto.IotConfigDto
import pt.nova.fct.iot.configserver.models.IotConfigModel

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
interface IotConfigMapper: DefaultMapper<IotConfigDto, IotConfigModel> {

}