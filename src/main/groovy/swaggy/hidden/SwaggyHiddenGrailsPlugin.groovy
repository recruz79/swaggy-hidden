package swaggy.hidden

import com.github.rahulsom.swaggydoc.SwaggyDataService
import grails.plugins.*
import org.springframework.boot.context.embedded.FilterRegistrationBean
import org.springframework.core.Ordered
import swaggy.hidden.filter.ApiKeyFilter

class SwaggyHiddenGrailsPlugin extends Plugin {

    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "3.1.4 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]

    def title = "Swaggy Hidden" // Headline display name of the plugin
    def author = "Raul Cruz"
    def authorEmail = "raul.cruz@whiteprompt.com"
    def description = '''\
Brief summary/description of the plugin.
'''
    def profiles = ['web']

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/swaggy-hidden"

    def dependsOn = [swaggydoc:"0.26.0"]
    def loadBefore = ['controllers']
    def loadAfter = ['swaggydoc']

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
//    def license = "APACHE"

    // Details of company behind the plugin (if there is one)
//    def organization = [ name: "My Company", url: "http://www.my-company.com/" ]

    // Any additional developers beyond the author specified above.
//    def developers = [ [ name: "Joe Bloggs", email: "joe@bloggs.net" ]]

    // Location of the plugin's issue tracker.
//    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]

    // Online location of the plugin's browseable source code.
//    def scm = [ url: "http://svn.codehaus.org/grails-plugins/" ]

    Closure doWithSpring() { {->

        /**
         * We want APIController to inject our extended SwaggyDataService, WPSwaggyDataService
         * So we overwrite the swaggyDataService bean with our own implementation
         */
        swaggyDataService(WPSwaggyDataService){
            pluginSwaggyDataService = ref('pluginSwaggyDataService')
            grailsApplication = grails.util.Holders.getGrailsApplication()
        }

        /**
         * Our WPSwaggyDataService uses the original SwaggyDataService. In order to do this we define a bean with the original SwaggyDataService and we name
         * it pluginSwaggyDataService
         */
        pluginSwaggyDataService(SwaggyDataService){
            grailsApplication = grails.util.Holders.getGrailsApplication()
            grailsLinkGenerator = ref('grailsLinkGenerator')
            grailsUrlMappingsHolder = ref('grailsUrlMappingsHolder')
            grailsMimeUtility = ref('grailsMimeUtility')
        }

        apiKeyFilter(ApiKeyFilter)

        swaggyTokenFilter(FilterRegistrationBean) {
            filter = bean(ApiKeyFilter)
            urlPatterns = ['/*']
            order = Ordered.HIGHEST_PRECEDENCE
        }

    }
    }

    void doWithDynamicMethods() {
        // TODO Implement registering dynamic methods to classes (optional)
    }

    void doWithApplicationContext() {
        // TODO Implement post initialization spring config (optional)
    }

    void onChange(Map<String, Object> event) {
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    void onConfigChange(Map<String, Object> event) {
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    void onShutdown(Map<String, Object> event) {
        // TODO Implement code that is executed when the application shuts down (optional)
    }
}
