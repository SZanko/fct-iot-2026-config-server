package pt.nova.fct.iot.configserver.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirements
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pt.nova.fct.iot.configserver.dto.LoginRequest
import pt.nova.fct.iot.configserver.dto.LoginResponse
import pt.nova.fct.iot.configserver.dto.RegisterRequest
import pt.nova.fct.iot.configserver.models.UserModel
import pt.nova.fct.iot.configserver.repos.UserRepo
import pt.nova.fct.iot.configserver.service.security.JwtService

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authenticationManager: AuthenticationManager,
    private val jwtService: JwtService,
    private val userRepo: UserRepo,
    private val passwordEncoder: PasswordEncoder,
) {
    @SecurityRequirements
    @Operation(summary = "Login with username and password")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Login successful, returns JWT token"),
            ApiResponse(responseCode = "401", description = "Invalid credentials"),
        ]
    )
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<LoginResponse> {
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(request.username, request.password)
        )
        return ResponseEntity.ok(LoginResponse(jwtService.generateToken(request.username)))
    }

    @SecurityRequirements
    @Operation(summary = "Register a new user account")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "User registered successfully"),
            ApiResponse(responseCode = "409", description = "Username already taken"),
        ]
    )
    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): ResponseEntity<Unit> {
        if (userRepo.findByUsername(request.username) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build()
        }
        userRepo.save(
            UserModel(
                username = request.username,
                password = passwordEncoder.encode(request.password)!!,
            )
        )
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }
}
