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
import pt.nova.fct.iot.configserver.models.UserModel
import pt.nova.fct.iot.configserver.repos.IotConfigRepo
import pt.nova.fct.iot.configserver.repos.UserRepo

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthIntegrationTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val userRepo: UserRepo,
    @Autowired private val iotConfigRepo: IotConfigRepo,
    @Autowired private val passwordEncoder: PasswordEncoder,
) {
    private val objectMapper = ObjectMapper().findAndRegisterModules()

    @BeforeEach
    fun setup() {
        userRepo.deleteAll()
        iotConfigRepo.deleteAll()
        userRepo.save(
            UserModel(
                username = "admin",
                password = passwordEncoder.encode("secret")!!,
            )
        )
        iotConfigRepo.save(IotConfigModel(busStopId = "stop-42", ldrLimit = 512, temperature = 20, name = "test"))
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
