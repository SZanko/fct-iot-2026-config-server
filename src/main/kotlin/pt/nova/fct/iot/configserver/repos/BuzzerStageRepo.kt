package pt.nova.fct.iot.configserver.repos

import org.springframework.data.repository.CrudRepository
import pt.nova.fct.iot.configserver.models.BuzzerStageModel

interface BuzzerStageRepo : CrudRepository<BuzzerStageModel, String> {
    fun findByBusStopIdOrderByMinutesBeforeDesc(busStopId: String): List<BuzzerStageModel>
    fun deleteByBusStopId(busStopId: String)
}
