package nayax



import static org.springframework.http.HttpStatus.*

import org.codehaus.groovy.grails.commons.ConfigurationHolder;

import grails.transaction.Transactional

@Transactional(readOnly = true)
class ExternalEventController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond ExternalEvent.list(params), model:[externalEventInstanceCount: ExternalEvent.count()]
    }
    
    def event ={
        
        render view: '/eventindex', model: [image: "", description:""]
    }
    
	def pics(){
		def pics =[]
		def messages = EventMessage.createCriteria().list{
			eq("externalEvent", ExternalEvent.get(params.eventId.toLong()))
			isNotNull("picture")
		}
		
		messages.each{message->
			def filename = 'https://lisnx.com/user_images/'+message.picture.filename
			//log.info('Adding pic:' + filename)
			pics.add(filename)
		}
		
//		pics = ['http://lisnx.com/user_images/df3567e0-6c83-4c81-a375-da1a7f6e5e20.jpg',
//					'http://lisnx.com/user_images/002fc8a4-a0fc-495c-ab66-00c120220eba.jpg',
//					'http://lisnx.com/user_images/04b88d40-fea7-4a7e-a613-6da35b38f211.jpg']
		log.info(pics)
		[pics:pics]
	}
	def pics2(){
		/*def pics =[]
		def messages = EventMessage.createCriteria().list{
			eq("externalEvent", ExternalEvent.get(params.eventId.toLong()))
			isNotNull("picture")
		}
		
		messages.each{message->
			def filename = 'http://lisnx.com/user_images/'+message.picture.filename
			//log.info('Adding pic:' + filename)
			pics.add(filename)
		}*/
		
		def pics = ['http://lisnx.com/user_images/df3567e0-6c83-4c81-a375-da1a7f6e5e20.jpg',
					'http://lisnx.com/user_images/002fc8a4-a0fc-495c-ab66-00c120220eba.jpg',
					'http://lisnx.com/user_images/04b88d40-fea7-4a7e-a613-6da35b38f211.jpg']
		log.info(pics)
		[pics:pics]
	}
	def pics3(){
		def pics = ['http://lisnx.com/user_images/df3567e0-6c83-4c81-a375-da1a7f6e5e20.jpg',
					'http://lisnx.com/user_images/002fc8a4-a0fc-495c-ab66-00c120220eba.jpg',
					'http://lisnx.com/user_images/04b88d40-fea7-4a7e-a613-6da35b38f211.jpg']
		log.info(pics)
		[pics:pics]
	}
	
	def shareOnFb(){
		log.info('Sharing on fb' + params.id)
	}
	

    def show(ExternalEvent externalEventInstance) {
		def pics = [[path:'http://lisnx.com/images/t1.jpg', 
					thumbnailPath:'http://lisnx.com/images/t1.jpg'],
				[path:'http://lisnx.com/images/t2.jpg',
					thumbnailPath:'http://lisnx.com/images/t2.jpg']
				    ]
		/*EventMessage.createCriteria().list{
			eq("externalEvent", ExternalEvent.get(19))
			isNotNull("picture")
		}*/
        [externalEventInstance: externalEventInstance, pics:pics]
    }
	

    def create() {
        respond new ExternalEvent(params)
    }

    @Transactional
    def save(ExternalEvent externalEventInstance) {
        if (externalEventInstance == null) {
            notFound()
            return
        }

        if (externalEventInstance.hasErrors()) {
            respond externalEventInstance.errors, view:'create'
            return
        }

        externalEventInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: 'externalEventInstance.label', default: 'ExternalEvent'), externalEventInstance.id])
                redirect externalEventInstance
            }
            '*' { respond externalEventInstance, [status: CREATED] }
        }
    }

    def edit(ExternalEvent externalEventInstance) {
        respond externalEventInstance
    }

    @Transactional
    def update(ExternalEvent externalEventInstance) {
        if (externalEventInstance == null) {
            notFound()
            return
        }

        if (externalEventInstance.hasErrors()) {
            respond externalEventInstance.errors, view:'edit'
            return
        }

        externalEventInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'ExternalEvent.label', default: 'ExternalEvent'), externalEventInstance.id])
                redirect externalEventInstance
            }
            '*'{ respond externalEventInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(ExternalEvent externalEventInstance) {

        if (externalEventInstance == null) {
            notFound()
            return
        }

        externalEventInstance.delete flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'ExternalEvent.label', default: 'ExternalEvent'), externalEventInstance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'externalEventInstance.label', default: 'ExternalEvent'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
