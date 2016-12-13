package nayax



import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class NayaxEventController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond NayaxEvent.list(params), model:[nayaxEventInstanceCount: NayaxEvent.count()]
    }

    def show(NayaxEvent nayaxEventInstance) {
        respond nayaxEventInstance
    }

    def create() {
        respond new NayaxEvent(params)
    }

    @Transactional
    def save(NayaxEvent nayaxEventInstance) {
        if (nayaxEventInstance == null) {
            notFound()
            return
        }

        if (nayaxEventInstance.hasErrors()) {
            respond nayaxEventInstance.errors, view:'create'
            return
        }

        nayaxEventInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: 'nayaxEventInstance.label', default: 'NayaxEvent'), nayaxEventInstance.id])
                redirect nayaxEventInstance
            }
            '*' { respond nayaxEventInstance, [status: CREATED] }
        }
    }

    def edit(NayaxEvent nayaxEventInstance) {
        respond nayaxEventInstance
    }

    @Transactional
    def update(NayaxEvent nayaxEventInstance) {
        if (nayaxEventInstance == null) {
            notFound()
            return
        }

        if (nayaxEventInstance.hasErrors()) {
            respond nayaxEventInstance.errors, view:'edit'
            return
        }

        nayaxEventInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'NayaxEvent.label', default: 'NayaxEvent'), nayaxEventInstance.id])
                redirect nayaxEventInstance
            }
            '*'{ respond nayaxEventInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(NayaxEvent nayaxEventInstance) {

        if (nayaxEventInstance == null) {
            notFound()
            return
        }

        nayaxEventInstance.delete flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'NayaxEvent.label', default: 'NayaxEvent'), nayaxEventInstance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'nayaxEventInstance.label', default: 'NayaxEvent'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
