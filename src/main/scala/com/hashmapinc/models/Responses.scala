package com.hashmapinc.models

import org.springframework.http.HttpStatus

import scala.beans.BeanProperty

object Responses {

	case class Batch(@BeanProperty id: Integer, @BeanProperty appId: String,
									 @BeanProperty appInfo: Map[String, String], @BeanProperty log: List[String],
									 @BeanProperty state: String)

	case class Batches(@BeanProperty from: Int = 0, @BeanProperty total: Int = 0, @BeanProperty sessions: List[Batch]){
		def this() = this(0, 0, Nil)
	}

	case class Unsuccessful(@BeanProperty status: HttpStatus, @BeanProperty error: String){
		def this() = this(HttpStatus.BAD_REQUEST, "")
	}

}
