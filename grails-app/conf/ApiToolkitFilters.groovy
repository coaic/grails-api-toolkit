import java.util.ArrayList;
import java.util.List;
import java.util.Map

import grails.converters.JSON
import grails.converters.XML
import net.nosegrind.apitoolkit.Api;
import net.nosegrind.apitoolkit.Method;

class ApiToolkitFilters {
	
	def apiToolkitService
	def grailsApplication
	def apiCacheService

	def filters = {
		
		String apiName = grailsApplication.config.apitoolkit.apiName
		String apiVersion = grailsApplication.metadata['app.version']
		
		apitoolkit(uri:"/${apiName}_${apiVersion}/**"){
			before = { Map model ->
				
				params.action = (params.action)?params.action:'index'
				
				def controller = grailsApplication.getArtefactByLogicalPropertyName('Controller', params.controller)
				def cache = (params.controller)?apiCacheService.getApiCache(params.controller):null
				if(cache){
					if(cache["${params.action}"]){
						if (apiToolkitService.isApiCall()) {
							// USER HAS ACCESS?
							if(!apiToolkitService.checkAuthority(cache["${params.action}"]['apiRoles'])){
								return false
							}
							// DOES METHOD MATCH?
							def method = cache["${params.action}"]['method'].replace('[','').replace(']','').split(',')*.trim() as List
							
							if(!apiToolkitService.isRequestMatch(method)){
								return false
							}
						}else{
							return false
						}
					}
				}
			}
			
			after = { Map model ->

				params.action = (params.action)?params.action:'index'
				
				def controller = grailsApplication.getArtefactByLogicalPropertyName('Controller', params.controller)
				def cache = (params.controller)?apiCacheService.getApiCache(params.controller):null
				
				def newModel

				if(cache){
					if(cache["${params.action}"]){
						def formats = ['text/html','application/json','application/xml']
						def type = (request.getHeader('Content-Type'))?formats.findAll{ request.getHeader('Content-Type')?.startsWith(it) }[0].toString():null
						if(type){
						
							if (apiToolkitService.isApiCall()) {
								def methods = cache["${params.action}"]['method'].replace('[','').replace(']','').split(',')*.trim() as List
								def method = (methods.contains(request.method))?request.method:null
								def queryString = request.'javax.servlet.forward.query_string'
								def path = (queryString)?queryString.split('&'):[]
								
								response.setHeader('Allow', methods.join(', '))
								response.setHeader('Content-Type', "${type};charset=UTF-8")
								response.setHeader('Authorization', cache["${params.action}"]['apiRoles'].join(', '))
								
								newModel = (grailsApplication.isDomainClass(model.getClass()))?model:apiToolkitService.formatModel(model)

								if(method){
									switch(method) {
										case 'HEAD':
											break;
										case 'OPTIONS':
											switch(type){
												case 'application/xml':
													render(text:cache["${params.action}"]['doc'] as XML, contentType: "${type}")
													break
												case 'application/json':
												default:
													render(text:cache["${params.action}"]['doc'] as JSON, contentType: "${type}")
													break
											}
											break;
										case 'GET':
											def map = newModel
											if(!newModel.isEmpty()){
											switch(type){
												case 'application/xml':
													if(path){
														String uri = isChainedApi(path)
														if(uri){
															redirect(uri: "${uri}")
														}else{
															String msg = "Path was unable to be parsed"
															return apiToolkitService._404_NOTFOUND(msg)
														}
													}
				
													render(text:map as XML, contentType: "${type}")
													break
												case 'text/html':
													def map = newModel
													if(path){
														String uri = isChainedApi(path)
														if(uri){
															redirect(uri: "${uri}")
														}else{
															String msg = "Path was unable to be parsed"
															return apiToolkitService._404_NOTFOUND(msg)
														}
													}
													break
												case 'application/json':
												default:
													def map = newModel
													if(path){
														String uri = isChainedApi(path)
														if(uri){
															redirect(uri: "${uri}")
														}else{
															String msg = "Path was unable to be parsed"
															return apiToolkitService._404_NOTFOUND(msg)
														}
													}
														
													render(text:map as JSON, contentType: "${type}")
													break
												}
											}
											break
										case 'POST':
											switch(type){
												case 'application/xml':
													String uri = isChainedApi(path)
													if(uri){
														redirect(uri: "${uri}")
													}else{
														String msg = "Path was unable to be parsed"
														return apiToolkitService._404_NOTFOUND(msg)
													}
													return response.status
													break
												case 'application/json':
												default:
													String uri = isChainedApi(path)
													if(uri){
														redirect(uri: "${uri}")
													}else{
														String msg = "Path was unable to be parsed"
														return apiToolkitService._404_NOTFOUND(msg)
													}
													return response.status
													break
											}
											break
										case 'PUT':
											switch(type){
												case 'application/xml':
													String uri = isChainedApi(path)
													if(uri){
														redirect(uri: "${uri}")
													}else{
														String msg = "Path was unable to be parsed"
														return apiToolkitService._404_NOTFOUND(msg)
													}
													return response.status
													break
												case 'application/json':
												default:
													String uri = isChainedApi(path)
													if(uri){
														redirect(uri: "${uri}")
													}else{
														String msg = "Path was unable to be parsed"
														return apiToolkitService._404_NOTFOUND(msg)
													}
													return response.status
													break
											}
											break
										case 'DELETE':
											switch(type){
												case 'application/xml':
													String uri = isChainedApi(path)
													return response.status
													break
												case 'application/json':
												default:
													String uri = isChainedApi(path)
													return response.status
													break;
											}
											break
									}
								}
								return false
							}	
						}else{
							//render(view:params.action,model:model)
						}
					}else{
						//render(view:params.action,model:model)
					}
				}
			}
		}

	}

}


		
