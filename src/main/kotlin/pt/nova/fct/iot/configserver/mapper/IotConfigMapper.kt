package pt.nova.fct.iot.configserver.mapper

import org.mapstruct.Mapper
import pt.nova.fct.iot.configserver.dto.IotConfigDto
import pt.nova.fct.iot.configserver.models.IotConfigModel

@Mapper
interface IotConfigMapper: DefaultMapper<IotConfigDto, IotConfigModel> {

}