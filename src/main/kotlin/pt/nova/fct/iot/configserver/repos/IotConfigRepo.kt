package pt.nova.fct.iot.configserver.repos

import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import pt.nova.fct.iot.configserver.models.IotConfigModel
import java.util.Optional

@Repository
interface IotConfigRepo: PagingAndSortingRepository<IotConfigModel, String> {
    fun findById(id: String): Optional<IotConfigModel>
}