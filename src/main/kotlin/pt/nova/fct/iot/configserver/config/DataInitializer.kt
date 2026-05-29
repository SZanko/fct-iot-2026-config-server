package pt.nova.fct.iot.configserver.config

import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import pt.nova.fct.iot.configserver.models.IotConfigModel
import pt.nova.fct.iot.configserver.models.UserModel
import pt.nova.fct.iot.configserver.repos.IotConfigRepo
import pt.nova.fct.iot.configserver.repos.UserRepo

@Component
class DataInitializer(
    private val userRepo: UserRepo,
    private val passwordEncoder: PasswordEncoder,
    private val configRepo: IotConfigRepo
) : ApplicationRunner {


    override fun run(args: ApplicationArguments) {
        if (userRepo.findByUsername(DEFAULT_USERNAME) == null) {
            val user = UserModel(
                username = DEFAULT_USERNAME,
                password = passwordEncoder.encode(DEFAULT_PASSWORD)!!,
            )
            userRepo.save(user)
            logger.info("Default user '{}' created", DEFAULT_USERNAME)
        } else {
            logger.info("Default user '{}' already exists", DEFAULT_USERNAME)
        }

        val exampleConfig = IotConfigModel(
            busStopId = DEFAULT_BUSSTOP_ID, ldrLimit = 170,
            temperature = 30, name = "example-config"
        )
        configRepo.save(exampleConfig)
        logger.info("Example config for bus stop '020387' created")

        if (configRepo.findByBusStopId(ESP_BUSSTOP_ID).isEmpty) {
            configRepo.save(IotConfigModel(
                busStopId = ESP_BUSSTOP_ID, ldrLimit = 170,
                temperature = 30, name = "MTE CAPARICA R ALFREDO CUNHA (ESCOLA)"
            ))
            logger.info("Default config for ESP32 bus stop '{}' created", ESP_BUSSTOP_ID)
        }
    }

    companion object {
        private const val DEFAULT_USERNAME = "admin"
        private const val DEFAULT_PASSWORD = "admin"
        private const val DEFAULT_BUSSTOP_ID = "020387"
        private const val ESP_BUSSTOP_ID = "020359"
        private val logger = LoggerFactory.getLogger(DataInitializer::class.java)
    }
}
