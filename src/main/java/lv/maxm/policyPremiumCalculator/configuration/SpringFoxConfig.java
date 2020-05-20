/**
 * 2020-05-20
 */
package lv.maxm.policyPremiumCalculator.configuration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;

import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author MaxM
 *
 */
@Configuration
@EnableSwagger2
public class SpringFoxConfig {   
	
    @Bean
    public Docket api() { 
    	   final List<ResponseMessage> globalResponses = Arrays.asList(
    		        new ResponseMessageBuilder()
    		            .code(200)
    		            .message("OK")
    		            .build(),
    		        new ResponseMessageBuilder()
    		            .code(400)
    		            .message("Bad Request")
    		            .build(),
    		        new ResponseMessageBuilder()
    		            .code(500)
    		            .message("Internal Error")
    		            .build());
    		    final ApiInfo apiInfo = new ApiInfo("Premium Calculator Service API",
    		    		"API to calculate premium for the policy",
    		    		"1.0", null, null, null, null, Collections.emptyList());

        return new Docket(DocumentationType.SWAGGER_2)  
        	.useDefaultResponseMessages(false)
        	.globalResponseMessage(RequestMethod.POST, globalResponses)
		    .select()
		    .apis(RequestHandlerSelectors.basePackage("lv.maxm.policyPremiumCalculator.controllers"))
		    .build()
		    .apiInfo(apiInfo);                                           
    }
    
}

