package pt.nova.fct.iot.configserver.repos

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import pt.nova.fct.iot.configserver.models.IotEnvironmentModel
import java.util.Optional

@Repository
interface IotEnvironmentRepo : CrudRepository<IotEnvironmentModel, String> {
    fun findByBusStopId(busStopId: String): Optional<IotEnvironmentModel>
}
