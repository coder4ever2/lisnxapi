package nayax

import org.codehaus.groovy.grails.commons.ConfigurationHolder

class Picture {
    Date dateCreated
    Date lastUpdated
    String originalFilename
    String mimeType
    Integer fileSize
    String filename
    String filePath

    static constraints = {
    }
}
