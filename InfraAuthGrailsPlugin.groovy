import infra.auth.ShiroAuthRepo
import infra.auth.ShiroPermissionDeniedBehavior
import infra.auth.settings.AuthConfigs
import infra.auth.settings.AuthConfigsWrapper
import infra.auth.utils.ShiroPermissionUtils
import org.apache.shiro.authc.credential.Sha1CredentialsMatcher
import org.apache.shiro.grails.ShiroSecurityService

class InfraAuthGrailsPlugin {

    def version = "0.1-SNAPSHOT"
    def grailsVersion = "2.2 > *"
    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]

    def title       = "Infra Auth Plugin"
    def author      = "Svyat Podmogayev, Dmitry Kurinskiy"
    def authorEmail = "s.podmogayev@gmail.com, name.alari@gmail.com"

    def description = ""

    def documentation = "http://grails.org/plugin/infra-auth"

    def license = "APACHE"

    def developers = [
            [ name: "Svyat Podmogayev", email: "s.podmogayev@gmail.com" ],
            [ name: "Dmitry Kurinskiy", email: "name.alari@gmail.com" ]
    ]

//
//    def organization = [ name: "My Company", url: "http://www.my-company.com/" ]
//
//    Location of the plugin's issue tracker.
//    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]
//
//    Online location of the plugin's browseable source code.
//    def scm = [ url: "http://svn.codehaus.org/grails-plugins/" ]
//

    def doWithSpring = {

        authRepo(ShiroAuthRepo)
        permissionUtils(ShiroPermissionUtils)
        permissionDeniedBehavior(ShiroPermissionDeniedBehavior)
        shiroSecurityService(ShiroSecurityService)

        credentialMatcher(Sha1CredentialsMatcher) {
            storedCredentialsHexEncoded = true
        }


        def infraConfig = application.config.infra
        if(infraConfig) {
            def authConfigNode = infraConfig.auth

            def signInConfigs = authConfigNode?.signIn
            String signInView = null
            if (signInConfigs != [:]) {
                if (signInConfigs.view != [:])
                    signInView = signInConfigs.view
            }

            def signUpConfigs = authConfigNode?.signUp
            String signUpView = null
            if(signUpConfigs != [:]) {
                if (signUpConfigs.view != [:])
                    signUpView = signUpConfigs.view
            }

            def unauthorizedConfigs = authConfigNode?.unauthorized
            String unauthorizedView = null
            if (unauthorizedConfigs != [:])
                if (unauthorizedConfigs.view != [:])
                    unauthorizedView = unauthorizedConfigs.view

            AuthConfigsWrapper authConfigsWrapper = AuthConfigsWrapper.getInstance()
            AuthConfigs authConfigs = new AuthConfigs(signInView, signUpView, unauthorizedView)
            authConfigsWrapper.setConfigs(authConfigs)
        }
    }
}
