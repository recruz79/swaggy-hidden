    INTRODUCTION
    ============

    We want to have a project with automatic CRUD scaffolding, swagger and swagger Rest Client.
    We learnt that swaggydoc plugin is showing modal properties like version that we dont want to
    expose to the Rest Api. We extended the plugin for hiding such properties.

    STEPS TO INSTALL THE PLUGIN
    ===========================

    To make the plugin work you need to configure some components inside the grails project.

	Grails Configuration:
	=====================
	On build.gradble add the following line in the dependencies closure to install the plugin.
	
	compile 'swaggy.hidden:swaggy-hidden:0.6'
	
	Spring configuration:
	=====================
	
	WPSwaggyDataService is an extension of swaggyDataService. It adds the features of hiding domain properties.
	ApiKeyFilter is the filter which intercepts the api_key param coming from swagger and includes it in the
	header request so Spring Security can intercept it and authenticate.
	
	You have to inject the beans below in the Resources.groovy file to make the plugin work.
	
	/**
     * We want APIController to inject our extended SwaggyDataService, WPSwaggyDataService
     * So we overwrite the swaggyDataService bean with our own implementation
     */
    swaggyDataService(WPSwaggyDataService){
        pluginSwaggyDataService = ref('pluginSwaggyDataService')
        grailsApplication = grails.util.Holders.getGrailsApplication()
    }
	
	Domain Configuration:
	=====================
	
	The HiddenApiRest annotation can be used on any domain class for hiding unwanted domain properties so swagger do not show them
	in the api services screen. It supports multiple properties to hide and can be used as seen below.
	
	@HiddenApiRest(values = ["version", "address"])
	class Author {

		String name
		String address
		String phone
		String email
	}
	
	Controller configuration:
	=========================
	This plugin uses the swaggyDoc plugin to map controllers for standard methods (create, index, update, save, edit). There is no need to
	add any annotations. Methods will be documented automatically. You can add annotations only if you want to customize them.
	
	@Api("Book Controller services")
	class BookController extends RestfulController {

		BookController(){
			super(Book)
		}

		@ApiOperation(value = "Returns an json list of Books domain", httpMethod = "GET", response = Book)
		def index(Integer max) {
			super.index(max)
		}

		@ApiOperation(value = "Updates an Book domain object", httpMethod = "PUT")
		@ApiResponses([
				@ApiResponse(code = 405, message = "Bad method. Only POST is allowed"),
				@ApiResponse(code = 401, message = "Unauthorized"),
				@ApiResponse(code = 400, message = "Invalid request json")
		])

		def update() {
			super.update()
		}
	}


	Scaffolding:
	============

	Creating default scaffolding pages will not work. We need scaffolding routes to be different from rest routes.
	We need to modify the scaffolding templates.

	<g:link> will not work and must be changed to <a> to set manually the urls as shown below:
	
	orignal: <g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link>
	new: <a class="home" href="\${createLink(uri: '/')}/${entityName}/create"><g:message code="default.new.label" args="[entityName]" /></a>
	
	
	Setting mapping for Rest Services:
	==================================
	
	Swagger can detect rest resources by reading the UrlMappings.groovy file. The examples below are settings two rest artifacts
	which will be created using all the CRUD methods (save, show, update). Notice that we are excluding unwanted methods from being
	shown in Swagger like create, edit and patch which are used by the scaffolding feature.
	
	static mappings = {
        "/rest/books" (resources:'book', excludes:['create', 'edit', 'patch']) { format = 'json' }
        "/rest/authors" (resources:'author', excludes:['create', 'edit', 'patch']) { format = 'json' }
    }

    Spring Security:
    ================

    Both types of authentication can work together in the same application. Token and Login in the example below are being
    set to share the same path configuration. Scaffolding will require login authentication while rest services
	will need token authentication.

    Tip:
        Remember to create the user domains (User, Role, etc..) and basic configuration on application.groovy by running 
		the command "grails s2-quickstart" 
		
    grails.plugin.springsecurity.useSecurityEventListener = true
    grails.plugin.springsecurity.userLookup.userDomainClassName = 'com.app.User'
    grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'com.app.UserRole'
    grails.plugin.springsecurity.authority.className = 'com.app.Role'
    grails.plugin.springsecurity.password.algorithm='SHA-256'

    grails.plugin.springsecurity.rest.login.active = true
    grails.plugin.springsecurity.rest.login.failureStatusCode = 401

    grails.plugin.springsecurity.controllerAnnotations.staticRules = [
    		[pattern: '/error', access: ['permitAll']],
    		[pattern: '/index', access: ['permitAll']],
    		[pattern: '/index.gsp', access: ['permitAll']],
    		[pattern: '/shutdown', access: ['permitAll']],
    		[pattern: '/assets/**', access: ['permitAll']],
    		[pattern: '/**/js/**', access: ['permitAll']],
    		[pattern: '/**/css/**', access: ['permitAll']],
    		[pattern: '/**/images/**', access: ['permitAll']],
    		[pattern: '/**/favicon.ico', access: ['permitAll']],
    		[pattern: '/rest/**', access: ['permitAll']],
    		[pattern: '/rest/public/**', access: ['permitAll']],
    		[pattern: '/api/**', access: ['permitAll']]
    ]

    grails.plugin.springsecurity.filterChain.chainMap = [
    		//Public chain
    		[
    				pattern: '/rest/public/**',
    				filters: 'anonymousAuthenticationFilter,restTokenValidationFilter,restExceptionTranslationFilter,filterInvocationInterceptor'
    		],
    		//Stateless chain
    		[
    				pattern: '/rest/**',
    				filters: 'JOINED_FILTERS,-anonymousAuthenticationFilter,-exceptionTranslationFilter,-authenticationProcessingFilter,-securityContextPersistenceFilter,-rememberMeAuthenticationFilter'
    		],
    		//Traditional chain
    		[
    				pattern: '/**',
    				filters: 'JOINED_FILTERS,-restTokenValidationFilter,-restExceptionTranslationFilter'
    		]
    ]
