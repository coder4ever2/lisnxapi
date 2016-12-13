package nayax

import nayax.Enum.NConnectionStatus

class NayaxConnectionService {

    static transactional = true
    def springSecurityService

    NConnection sendConnectionRequestToUser(NayaxUser receiver) {
        log.info "nConnectionService : sendConnectionRequestToUser : Entry"
        NayaxUser sender = springSecurityService.getCurrentUser()
        if (!sender.isConnectionRequestPendingWithUser(receiver)) {
            NConnection nConnection = new NConnection(owner: sender, connection: receiver)
            nConnection.connectionType = NConnection.ConnectionType.NAYAX_EVENT
            save(nConnection)
            log.info "nConnectionService : sendConnectionRequestToUser : Exit"
            return nConnection
        }
    }

    NConnection acceptConnection(NConnection nConnection) {
        nConnection.nConnectionStatus = NConnectionStatus.CONNECTED
        save(nConnection)
        return nConnection
    }

    NConnection ignoreConnection(NConnection nConnection) {
        nConnection.nConnectionStatus = NConnectionStatus.IGNORED
        save(nConnection)
        return nConnection
    }

    private Object save(Object object) {
        validateAndPrintErrors(object)
        Object result = object.save(flush: true)
        return result
    }

    private void validateAndPrintErrors(Object object) {
        if (!object.validate()) {
            log.error "Error saving object in NayaxConnectionService"
            object.errors.allErrors.each {error ->
                log.error error
            }
        }
    }
}
