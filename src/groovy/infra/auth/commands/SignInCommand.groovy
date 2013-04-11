package infra.auth.commands

import groovy.transform.CompileStatic
import grails.validation.Validateable

/**
 * @author prostohz
 * @since 4/11/13 6:06 PM
 */
@CompileStatic
@Validateable
class SignInCommand {

    String username

    String password

    static constraints = {
        username nullable: false, blank: false, size: 3..127
        password nullable: false, blank: false, size: 3..127
    }
}
