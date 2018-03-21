package com.hashmapinc.controllers

import com.hashmapinc.models.Requests.CreateBatch
import com.hashmapinc.services.LivyCommunicationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{MediaType, ResponseEntity}
import org.springframework.web.bind.annotation._
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

@RestController
@RequestMapping(Array("/api/v1/livy"))
class LivyController @Autowired()(service: LivyCommunicationService) {

	@RequestMapping(path = Array("/batches"), method = Array(RequestMethod.POST), consumes = Array(MediaType.APPLICATION_JSON_UTF8_VALUE))
	def addBatch(@RequestBody batch: CreateBatch) ={
		service.postBatch(batch) match {
			case Right(b) =>
				val uri = ServletUriComponentsBuilder
					.fromCurrentRequest()
					.path("/{id}")
					.buildAndExpand(b.id)
					.toUri
				 ResponseEntity.created(uri).body(b)
			case Left(m) => ResponseEntity.badRequest().body(m)
		}
	}

	@RequestMapping(path = Array("/batches/{id}"), method = Array(RequestMethod.GET))
	def getBatch(@PathVariable id: Integer) ={
		service.fetchBatchById(id) match {
			case Right(b) => ResponseEntity.ok(b)
			case Left(m) => ResponseEntity.badRequest().body(m)
		}
	}

	@RequestMapping(path = Array("/batches"), method = Array(RequestMethod.GET))
	def getBatches() ={
		service.batches() match {
			case Right(b) => ResponseEntity.ok(b)
			case Left(m) => ResponseEntity.badRequest().body(m)
		}
	}

	/*@RequestMapping(path = Array("/batches/{id}"), method = Array(RequestMethod.DELETE))
	def deleteBatch() ={
		service.batches match {
			case Right(b) => ResponseEntity.ok(b)
			case Left(m) => ResponseEntity.badRequest().body(m)
		}
	}*/


}
