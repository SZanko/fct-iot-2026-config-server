package pt.nova.fct.iot.configserver.repos

import org.springframework.data.jpa.repository.JpaRepository
import pt.nova.fct.iot.configserver.models.UserModel

interface UserRepo : JpaRepository<UserModel, String> {
    fun findByUsername(username: String): UserModel?
}
