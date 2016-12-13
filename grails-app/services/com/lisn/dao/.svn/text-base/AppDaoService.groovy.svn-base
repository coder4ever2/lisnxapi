package com.lisn.dao

import grails.transaction.Transactional

@Transactional
class AppDaoService {

	
	def grailsApplication
	def springSecurityService
	
	public Boolean saveObject(def obj) {
		log.trace "Inside saveObject for ${obj}"
		if (obj.validate()) {
			log.trace "Object validated."
			obj.errors.allErrors.each { log.error it }
			obj.save(flush:true)
			return true
		} else {
			log.trace "Object validation failed."
			obj.errors.allErrors.each { log.error it }
			obj.discard()
			return false
		}
	}
}
