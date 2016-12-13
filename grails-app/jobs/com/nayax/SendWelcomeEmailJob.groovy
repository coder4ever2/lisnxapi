package com.nayax

import nayax.NayaxUser
import grails.plugin.asyncmail.AsynchronousMailService

//TODO why we are using SendWelcome Emailjob..
class SendWelcomeEmailJob {

    def concurrent = false
    def cronExpression = "0 19 10 * * ? " // FIRE THE EMAIL AT 10.15 AM EVERYDAY TO THOSE PEOPLE WHO HAVE NOT RECEIEVED WELCOME EMAIL

    def userService
    def mailService
    AsynchronousMailService asynchronousMailService

    def execute() {
        // execute task
        log.debug("EXECUTING JOB")
        List<NayaxUser> nayaxUserList = NayaxUser.findAllByEmailSent(false)
        nayaxUserList.each {nayaxUser ->
            log.debug("SENDING EMAIL TO ${nayaxUser.fullName} with email ${nayaxUser.username}")
            asynchronousMailService.sendAsynchronousMail {
                to nayaxUser.username
				from "LISNx Team"
                subject 'Your registration with LISNX'
                body(
                        view: "/email/welcome_email_from_social_websites",
                        model: [userFullName: nayaxUser.fullName]
                )
            }
            nayaxUser.emailSent = true
            nayaxUser.save(flush: true)
            log.debug("EMAIL SENT")
        }
        log.debug("SendWelcomeEmail Job Finished")
    }
}
