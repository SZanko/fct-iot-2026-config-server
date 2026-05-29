package pt.nova.fct.iot.configserver

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import pt.nova.fct.iot.configserver.dto.LoginRequest
import pt.nova.fct.iot.configserver.dto.RegisterRequest
import pt.nova.fct.iot.configserver.models.IotConfigModel
import pt.nova.fct.iot.configserver.models.IotEnvironmentModel
import pt.nova.fct.iot.configserver.models.UserModel
import pt.nova.fct.iot.configserver.repos.IotConfigRepo
import pt.nova.fct.iot.configserver.repos.IotEnvironmentRepo
import pt.nova.fct.iot.configserver.repos.UserRepo

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthIntegrationTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val userRepo: UserRepo,
    @Autowired private val iotConfigRepo: IotConfigRepo,
    @Autowired private val iotEnvironmentRepo: IotEnvironmentRepo,
    @Autowired private val passwordEncoder: PasswordEncoder,
) {
    private val objectMapper = ObjectMapper().findAndRegisterModules()

    @BeforeEach
    fun setup() {
        iotEnvironmentRepo.deleteAll()
        userRepo.deleteAll()
        iotConfigRepo.deleteAll()
        userRepo.save(
            UserModel(
                username = "admin",
                password = passwordEncoder.encode("secret")!!,
            )
        )
        iotConfigRepo.save(IotConfigModel(busStopId = "stop-42", ldrLimit = 512, temperature = 20, name = "test"))
        iotEnvironmentRepo.save(IotEnvironmentModel(busStopId = "stop-42", temperature = 18.5, darkOutside = true))
    }

    @Test
    fun `login with valid credentials returns a JWT token`() {
        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(LoginRequest("admin", "secret")))
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").isNotEmpty())
    }

    @Test
    fun `login with wrong password returns unauthorized`() {
        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(LoginRequest("admin", "wrong")))
        )
            .andExpect(status().isUnauthorized())
    }

    @Test
    fun `login with unknown user returns unauthorized`() {
        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(LoginRequest("ghost", "secret")))
        )
            .andExpect(status().isUnauthorized())
    }

    @Test
    fun `public iot config endpoint is accessible without a token`() {
        mockMvc.perform(get("/api/iot/stop-42"))
            .andExpect(status().isOk())
    }

    @Test
    fun `public environment endpoint returns latest reading without a token`() {
        mockMvc.perform(get("/api/iot/stop-42/environment"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("stop-42"))
            .andExpect(jsonPath("$.temperature").value(18.5))
            .andExpect(jsonPath("$.darkOutside").value(true))
            .andExpect(jsonPath("$.updatedAt").isNotEmpty())
    }

    @Test
    fun `missing environment reading returns not found`() {
        mockMvc.perform(get("/api/iot/unknown-stop/environment"))
            .andExpect(status().isNotFound())
    }

    @Test
    fun `esp can post environment reading without a token`() {
        mockMvc.perform(
            post("/api/iot/stop-99/environment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mapOf("temperature" to 23.75, "darkOutside" to false)))
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("stop-99"))
            .andExpect(jsonPath("$.temperature").value(23.75))
            .andExpect(jsonPath("$.darkOutside").value(false))

        mockMvc.perform(get("/api/iot/stop-99/environment"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.temperature").value(23.75))
            .andExpect(jsonPath("$.darkOutside").value(false))
    }

    @Test
    fun `environment post updates the latest reading for the bus stop`() {
        mockMvc.perform(
            post("/api/iot/stop-42/environment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mapOf("temperature" to 21.25, "darkOutside" to false)))
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("stop-42"))
            .andExpect(jsonPath("$.temperature").value(21.25))
            .andExpect(jsonPath("$.darkOutside").value(false))

        mockMvc.perform(get("/api/iot/stop-42/environment"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.temperature").value(21.25))
            .andExpect(jsonPath("$.darkOutside").value(false))
    }

    @Test
    fun `environment post with invalid payload returns bad request`() {
        mockMvc.perform(
            post("/api/iot/stop-42/environment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mapOf("temperature" to 100.0, "darkOutside" to true)))
        )
            .andExpect(status().isBadRequest())
    }

    @Test
    fun `protected endpoint accepts requests with a valid JWT`() {
        val token = obtainToken("admin", "secret")

        val status = mockMvc.perform(
            post("/api/iot")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .content(objectMapper.writeValueAsString(mapOf("id" to "device-2", "name" to "Test Device")))
        ).andReturn().response.status

        // Auth passed — the request is not rejected with 401/403
        assert(status != 401 && status != 403) {
            "Expected request to pass authentication, but got HTTP $status"
        }
    }

    @Test
    fun `protected endpoint without token returns unauthorized`() {
        mockMvc.perform(
            post("/api/iot")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mapOf("id" to "device-2", "name" to "Test Device")))
        )
            .andExpect(status().isUnauthorized())
    }

    @Test
    fun `protected endpoint with tampered token returns unauthorized`() {
        mockMvc.perform(
            post("/api/iot")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer this.is.not.a.valid.token")
                .content(objectMapper.writeValueAsString(mapOf("id" to "device-2", "name" to "Test Device")))
        )
            .andExpect(status().isUnauthorized())
    }

    @Test
    fun `register with a new username returns created`() {
        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(RegisterRequest("newuser", "password123")))
        )
            .andExpect(status().isCreated())
    }

    @Test
    fun `register with an existing username returns conflict`() {
        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(RegisterRequest("admin", "anypassword")))
        )
            .andExpect(status().isConflict())
    }

    @Test
    fun `after registration login works with new credentials`() {
        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(RegisterRequest("newuser", "password123")))
        ).andExpect(status().isCreated())

        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(LoginRequest("newuser", "password123")))
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").isNotEmpty())
    }

    private fun obtainToken(username: String, password: String): String {
        val result = mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(LoginRequest(username, password)))
        )
            .andExpect(status().isOk())
            .andReturn()

        return objectMapper.readTree(result.response.contentAsString)
            .get("token").asText()
    }
}
