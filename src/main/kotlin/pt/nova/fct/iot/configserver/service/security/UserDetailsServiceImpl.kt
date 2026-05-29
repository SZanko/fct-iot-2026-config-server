package pt.nova.fct.iot.configserver.service.security

import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import pt.nova.fct.iot.configserver.repos.UserRepo

@Service
class UserDetailsServiceImpl(private val userRepo: UserRepo) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepo.findByUsername(username)
            ?: throw UsernameNotFoundException("User not found: $username")
        return User.builder()
            .username(user.username)
            .password(user.password)
            .roles("ADMIN")
            .build()
    }
}
