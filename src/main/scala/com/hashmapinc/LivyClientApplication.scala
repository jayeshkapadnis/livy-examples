package com.hashmapinc

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.{EnableAutoConfiguration, SpringBootApplication}
import org.springframework.context.annotation.{ComponentScan, Configuration}

@SpringBootApplication
@EnableAutoConfiguration
@Configuration
@ComponentScan
class LivyClientApplication

object LivyClientApplication extends App{
	SpringApplication.run(classOf[LivyClientApplication], args: _*)
}

