package pt.nova.fct.iot.configserver.config

import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import pt.nova.fct.iot.configserver.models.UserModel
import pt.nova.fct.iot.configserver.repos.UserRepo

@Component
class DataInitializer(
    private val userRepo: UserRepo,
    private val passwordEncoder: PasswordEncoder,
) : ApplicationRunner {

    private val logger = LoggerFactory.getLogger(DataInitializer::class.java)

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
    }

    companion object {
        private const val DEFAULT_USERNAME = "admin"
        private const val DEFAULT_PASSWORD = "admin"
    }
}
