package com.nayax

import nayax.ActivityAttendence
import nayax.NayaxUserActivityDescription
import nayax.NayaxUser

import grails.plugin.asyncmail.AsynchronousMailService

class ThankyouAttendeJob {

//    def timeout = 1000l * 60 * 10 // execute job once in 5 MINUTES

    def cronExpression = "0 26 18 * * ? " // FIRE THE EMAIL AT 10.15 AM EVERYDAY TO THOSE PEOPLE WHO HAVE NOT RECEIEVED WELCOME EMAIL
    def mailService
    def userService
    AsynchronousMailService asynchronousMailService

    def execute() {
        // execute task
        log.debug "STARTING EXECUTING"
        List<ActivityAttendence> attendenceList = ActivityAttendence.findAllByEmailSent(false)
        log.debug "ATTENDEE LIST IS ${attendenceList}"
        attendenceList.each {ActivityAttendence attendee ->
            if (attendee.nayaxUserActivity.expiry < new Date()) {
                log.debug "INSIDE IF"
                List<NayaxUser> usersAttended = getAllUsersForCurrentActivity(attendee)
                log.debug("${attendee.userAttendingEvent.fullName} HAS ATTENDED EVENT ${attendee.nayaxUserActivity.name} HOSTED BY ${attendee.nayaxUserActivity.nayaxUser.fullName} ,SO SENDING HIM EMAIL")
                asynchronousMailService.sendAsynchronousMail {
                    to attendee.userAttendingEvent.username
                    subject 'Thank You For Attending Event'
                    body(
                            view: "/email/thank_you_for_attending_event",
                            model: [userFullName: attendee.userAttendingEvent.fullName,
                                    eventName: attendee.nayaxUserActivity.name,
                                    hostName: attendee.nayaxUserActivity.nayaxUser.fullName,
                                    allAttendies: usersAttended]
                    )
                }
                attendee.emailSent = true
                attendee.save(flush: true)
                log.debug("EMAIL SENT")
            }
        }
        log.debug("Finishing Job")
    }

    List<NayaxUser> getAllUsersForCurrentActivity(ActivityAttendence activityAttendenceInstance) {
        NayaxUserActivityDescription attendingNayaxUserActivity = activityAttendenceInstance.nayaxUserActivity
        List<ActivityAttendence> rowsOfUsersAttendedEvent = ActivityAttendence.findAllByNayaxUserActivity(attendingNayaxUserActivity)
        List<NayaxUser> usersWhichAttendedTheEvent = []
        List<NayaxUser> test = []
        NayaxUser currentUser = activityAttendenceInstance.userAttendingEvent
        rowsOfUsersAttendedEvent.each { ActivityAttendence currentRow ->
            test += currentRow.userAttendingEvent
//            if (currentRow.id != activityAttendenceInstance.id) {
            usersWhichAttendedTheEvent += currentRow.userAttendingEvent
//            }
        }

        usersWhichAttendedTheEvent = usersWhichAttendedTheEvent - currentUser
        log.debug "THE LIST FOR USER ${activityAttendenceInstance.userAttendingEvent.fullName} is ${usersWhichAttendedTheEvent*.fullName}"
        log.debug "THE ALL LIST FOR USER ${activityAttendenceInstance.userAttendingEvent.fullName} is ${test*.fullName}"
        return usersWhichAttendedTheEvent
    }

}

