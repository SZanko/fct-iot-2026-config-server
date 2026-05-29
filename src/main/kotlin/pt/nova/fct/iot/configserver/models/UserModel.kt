package pt.nova.fct.iot.configserver.models

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "users")
data class UserModel(
    @Id
    val id: String = UUID.randomUUID().toString(),
    val username: String,
    val password: String,
)
