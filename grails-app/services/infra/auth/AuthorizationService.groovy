package infra.auth

import infra.auth.domains.User
import infra.auth.tokens.AuthToken
import infra.auth.commands.SignUpCommand

import org.apache.shiro.SecurityUtils
import org.apache.shiro.authc.AuthenticationException
import org.apache.shiro.subject.Subject
import org.apache.shiro.grails.ConfigUtils

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.i18n.LocaleContextHolder

class AuthorizationService {

    @Autowired
    AuthRepo authRepo

    def messageSource

    /**
     *
     * @return
     */
    public Subject getSubject() {
        SecurityUtils.subject
    }

    /**
     *
     * @return
     */
    public String getPrincipal() {
        subject?.principal
    }

    /**
     *
     * @return
     */
    public boolean isAuthenticated() {
        subject?.isAuthenticated() ?: false
    }

    /**
     * @param user
     * @return
     */
    public Map<String, ?> signIn(ShiroUser user) {
        signIn(user.username, user.passwordHash)
    }

    /**
     *
     * @param username
     * @param passwordHash
     * @return
     */
    public Map<String, ?> signIn(String username, String passwordHash) {
        signIn(username, passwordHash, false)
    }

    /**
     *
     * @param username
     * @param passwordHash
     * @param rememberMe
     * @return
     */
    public Map<String, ?> signIn(String username, String passwordHash, boolean rememberMe) {
        if (isAuthenticated()) {
            return authStatus
        }

        if (username && passwordHash) {
            AuthToken authToken = new AuthToken(username: username, passwordHash: passwordHash)
            authToken.rememberMe = rememberMe

            try {
                subject.login(authToken)
            } catch (AuthenticationException ex) {
                log.info "User couldn`t authorize."
            }
        }
        authStatus
    }

    /**
     *
     * @param command
     * @param userCreator
     * @return
     */
    public Map<String, ?> signUp(SignUpCommand command) {
        if (SecurityUtils.subject?.isAuthenticated()) {
            return authStatus
        }

        User newUser = authRepo.createUser(command.username, command.password)
        if (newUser) {
            subject.login(new AuthToken(username: newUser.username, password: newUser.passwordHash))

            println "User created successfully"
        } else {
            println "User created unsuccessfully"
        }
        authStatus
    }

    /**
     *
     * @return
     */
    public Map<String, ?> signOut() {
        def principal = getPrincipal()

        subject?.logout()
        ConfigUtils.removePrincipal(principal)

        authStatus
    }

    /**
     *
     * @return
     */
    public Map<String, ?> getAuthStatus() {
        [isAuthenticated: isAuthenticated(), username: principal]
    }

    private String message(Map params) {
        messageSource.getMessage(params.code, [] as Object[], null, LocaleContextHolder.locale)
    }
}
