package pt.nova.fct.iot.configserver.repos

import org.springframework.data.jpa.repository.JpaRepository
import pt.nova.fct.iot.configserver.models.DemoArrivalModel
import java.util.Optional

interface DemoArrivalRepo : JpaRepository<DemoArrivalModel, String> {
    fun findByBusStopId(busStopId: String): Optional<DemoArrivalModel>
}
