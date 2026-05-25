package pt.nova.fct.iot.configserver

import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import pt.nova.fct.iot.configserver.controller.IotClientController
import pt.nova.fct.iot.configserver.dto.IotConfigDto
import pt.nova.fct.iot.configserver.service.IotService
import pt.nova.fct.iot.configserver.service.exceptions.IotConfigNotFoundException
import pt.nova.fct.iot.configserver.service.security.JwtService
import pt.nova.fct.iot.configserver.service.security.SecurityConfig
import pt.nova.fct.iot.configserver.service.security.UserDetailsServiceImpl

@WebMvcTest(IotClientController::class)
@Import(SecurityConfig::class)
class SecurityConfigTests(
    @Autowired private val mockMvc: MockMvc,
) {
    @MockitoBean private lateinit var iotService: IotService
    @MockitoBean private lateinit var jwtService: JwtService
    @MockitoBean private lateinit var userDetailsService: UserDetailsServiceImpl

    @Test
    fun `iot config lookup is public and does not send a basic challenge`() {
        `when`(iotService.findConfigById("2")).thenReturn(IotConfigDto("2"))

        mockMvc.perform(get("/api/iot/2").accept(MediaType.ALL))
            .andExpect(status().isOk())
            .andExpect(header().doesNotExist(HttpHeaders.WWW_AUTHENTICATE))
    }

    @Test
    fun `missing iot config returns not found instead of forbidden`() {
        `when`(iotService.findConfigById("2")).thenThrow(IotConfigNotFoundException())

        mockMvc.perform(get("/api/iot/2").accept(MediaType.ALL))
            .andExpect(status().isNotFound())
            .andExpect(header().doesNotExist(HttpHeaders.WWW_AUTHENTICATE))
    }

    @Test
    fun `unexpected errors return internal server error without a stack trace response`() {
        `when`(iotService.findConfigById("2")).thenThrow(IllegalStateException("boom"))

        mockMvc.perform(get("/api/iot/2").accept(MediaType.ALL))
            .andExpect(status().isInternalServerError())
            .andExpect(header().doesNotExist(HttpHeaders.WWW_AUTHENTICATE))
            .andExpect(content().string(""))
    }

    @Test
    fun `protected requests without token return unauthorized without a basic challenge`() {
        mockMvc.perform(
            post("/api/iot")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"id":"2"}"""),
        )
            .andExpect(status().isUnauthorized())
            .andExpect(header().doesNotExist(HttpHeaders.WWW_AUTHENTICATE))
    }
}
