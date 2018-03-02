package com.hashmapinc.models

import scala.beans.BeanProperty

object Requests {

	case class CreateBatch(@BeanProperty file: String, @BeanProperty className: String,
												@BeanProperty args: Option[Array[String]] = None, @BeanProperty jars: Option[Array[String]] = None,
												@BeanProperty executorCores: Option[Int] = None, @BeanProperty executorMemory: Option[String] = None){
		def this() = this("", "")
	}

}
