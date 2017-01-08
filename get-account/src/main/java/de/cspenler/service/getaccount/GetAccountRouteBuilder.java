package de.cspenler.service.getaccount;

import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.cdi.ContextName;
import org.apache.camel.model.rest.RestBindingMode;

import de.cspenler.interfaces.cms.GetAccountRequest;
import de.cspenler.interfaces.cms.GetAccountResponse;

@ApplicationScoped
@Startup
@ContextName("account-context")
public class GetAccountRouteBuilder extends RouteBuilder {

    private final String CLAZZ = GetAccountRouteBuilder.class.getName();

    @Override
    public void configure() throws Exception {
        restConfiguration().component("servlet")
                .bindingMode(RestBindingMode.json)
                .dataFormatProperty("prettyPrint", "true")
                .apiContextPath("api-doc")
                .apiProperty("api.title", "Account API").apiProperty("api.version", "1.0.0")
                .apiProperty("cors", "true")
                .apiProperty("base.path", "/camel");
        rest("/account/").description("Account rest service")
                .consumes("application/json")
                .produces("application/json")
                .post().description("Find account by id")
                .type(GetAccountRequest.class).outType(GetAccountResponse.class)
                .to("direct:get-account");

        from("direct:get-account").id(CLAZZ + ".get-account")
                .process(new Processor() {

                    public void process(Exchange exchange) throws Exception {
                        GetAccountResponse response = new GetAccountResponse();
                        response.setId(exchange.getIn().getBody(GetAccountRequest.class).getId());
                        response.setAccountOwner("John Doe");
                        exchange.getIn().setBody(response);
                    }
                });
    }

}
