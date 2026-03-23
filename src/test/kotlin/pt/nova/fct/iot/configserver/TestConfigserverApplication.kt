package pt.nova.fct.iot.configserver

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
	fromApplication<ConfigserverApplication>().with(TestcontainersConfiguration::class).run(*args)
}
