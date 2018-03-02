package com.hashmapinc.services

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import com.hashmapinc.configs.LivyConfigurations
import com.hashmapinc.models.Requests.CreateBatch
import com.hashmapinc.models.Responses.{Batch, Batches, Unsuccessful}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.http._
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.stereotype.Service
import org.springframework.web.client.{ResourceAccessException, RestTemplate}
import scala.util.{Failure, Success, Try}

@Service
class LivyCommunicationService @Autowired()(config: LivyConfigurations){

	val baseUrl = s"http://${config.host}:${config.port}"

	def postBatch(batchRequest: CreateBatch): Either[String, Batch] ={
		val url = s"$baseUrl/batches"
		val headers = new HttpHeaders()
		headers.setContentType(MediaType.APPLICATION_JSON)
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE)
		val modifiedBatchRequest = batchRequest.copy(file = s"${config.location}${batchRequest.file}")
		Try {
			val response = restOperations.exchange(url, HttpMethod.POST,
				new HttpEntity[CreateBatch](modifiedBatchRequest, headers), classOf[Batch])
			response.getStatusCode match {
				case HttpStatus.CREATED => Right(response.getBody)
				case _ => Left("Error Occurred while posting batch")
			}
		} match {
			case Failure(e) => Left(e.getMessage)
			case Success(s) => s
		}
	}

	def fetchBatchById(id: Integer) ={
		val url = s"$baseUrl/batches/${Integer.toString(id)}"
		val headers = new HttpHeaders()
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE)
		Try {
			val response = restOperations.exchange(url, HttpMethod.GET,
				new HttpEntity[Void](headers), classOf[Batch])
			response.getStatusCode match {
				case HttpStatus.OK => Right(response.getBody)
				case s => Left(Unsuccessful(s, s"Error occurred while fetching batch for id $id"))
			}
		} match {
			case Failure(e: ResourceAccessException) => Left(Unsuccessful(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage))
			case Failure(e) => Left(Unsuccessful(HttpStatus.NOT_FOUND, e.getMessage))
			case Success(s) => s
		}
	}

	def batches(): Either[Unsuccessful, Batches] ={
		val url = s"$baseUrl/batches"
		val headers = new HttpHeaders()
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE)
		Try {
			val response = restOperations.exchange(url, HttpMethod.GET,
				new HttpEntity[Void](headers), classOf[Batches])
			response.getStatusCode match {
				case HttpStatus.OK => Right(response.getBody)
				case s => Left(Unsuccessful(s, s"Error occurred while fetching batches from Livy server"))
			}
		} match {
			case Failure(e: ResourceAccessException) => Left(Unsuccessful(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage))
			case Failure(e) => Left(Unsuccessful(HttpStatus.NOT_FOUND, e.getMessage))
			case Success(s) => s
		}
	}


	@Bean
	def restOperations: RestTemplate = {
		val rest = new RestTemplate()
		rest.getMessageConverters.add(0, jacksonHttpConverter)
		rest
	}

	@Bean
	def jacksonHttpConverter: MappingJackson2HttpMessageConverter = {
		new MappingJackson2HttpMessageConverter(mapper)
	}

	private def mapper = {
		val mapper = new ObjectMapper() with ScalaObjectMapper
		mapper.setSerializationInclusion(Include.NON_EMPTY)
		mapper.setSerializationInclusion(Include.NON_NULL)
		mapper.setSerializationInclusion(Include.NON_DEFAULT)
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
		mapper.registerModule(DefaultScalaModule)
		mapper
	}
}
