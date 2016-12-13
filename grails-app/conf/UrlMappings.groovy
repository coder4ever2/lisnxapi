class UrlMappings {

	static mappings = {
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

		"/"(controller: "api", action: "promo")
		"/pub"(controller: "api", action: "pub")
		
		"/vlc"(view:"/vlc-p1")
		"500"(view:'/error')
		
		"/promo" (controller: "api", action: "promo")
                
                "/event" (controller: "externalEvent", action: "event")
                "/externalEvent" (controller: "externalEvent", action: "event")
                "/ExternalEvent" (controller: "externalEvent", action: "event")
                
	}
}
