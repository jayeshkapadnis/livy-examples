package com.hashmapinc.configs

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class LivyConfigurations {

	@Value("${livy.host}")
	var host: String = _

	@Value("${livy.port}")
	var port: Int = _

	@Value("${livy.app.location}")
	var location: String = _

}
