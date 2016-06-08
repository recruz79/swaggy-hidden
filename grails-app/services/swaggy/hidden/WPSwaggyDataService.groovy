package swaggy.hidden

import com.github.rahulsom.swaggydoc.ControllerDefinition
import com.github.rahulsom.swaggydoc.Resources
import grails.artefact.Service
import grails.transaction.Transactional
import swaggy.hidden.annotations.HiddenApiRest

import java.lang.annotation.Annotation

/**
 * This WPSwaggyDataService class extends the behaviour of the original SwaggyDataService in order to read the annotation
 * @Hidden and hidding the annotated properties and methods from the Rest API Swagger client.
 */
@Transactional
class WPSwaggyDataService implements Service {

    def pluginSwaggyDataService
    def grailsApplication

    ControllerDefinition apiDetails(String controllerName) {
        log.info "controllerName : " + controllerName
        def resp = pluginSwaggyDataService.apiDetails(controllerName)
        eliminateHiddenProperties(controllerName, resp)
        resp
    }

    def eliminateHiddenProperties(def domainName, def resp) {
        def domainClass = grailsApplication.domainClasses.find {
            domainName == it.logicalPropertyName
        }

        if (!domainClass) {
            return null
        }

        def hiddenApi = domainClass.clazz.annotations.find { Annotation annotation -> annotation.annotationType() == HiddenApiRest }
        if(hiddenApi) {
            def fields = hiddenApi?.values()
            def values = resp?.models?.values()
            values.each { itValues ->
                fields.each { itFields ->
                    itValues.properties.remove(itFields)
                }
            }
        }

        resp
    }

    /**
     * Generates map of Swagger Resources.
     * @return Map
     */
    Resources resources() {
        return pluginSwaggyDataService.resources()
    }

}
