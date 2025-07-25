package io.krakau.genaifinderapi.configuration;

import io.krakau.genaifinderapi.component.EnvironmentVariables;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Dominik
 */
@Configuration
public class OpenAPIConfig {
    
    private EnvironmentVariables env;
    
    @Autowired
    public OpenAPIConfig(EnvironmentVariables env) {
        this.env = env;
    }

    @Bean
    public OpenAPI myOpenAPI() {
        Server server = new Server();
        server.setUrl(env.OPENAPI_SERVER_URL);
        server.setDescription(env.OPENAPI_SERVER_DESCRIPTION);

        Contact contact = new Contact();
        contact.setEmail(env.OPENAPI_CONTRACT_EMAIL);
        contact.setName(env.OPENAPI_CONTRACT_NAME);
        contact.setUrl(env.OPENAPI_CONTRACT_URL);

        License licence = new License()
                .name(env.OPENAPI_LICENCE_NAME)
                .identifier(env.OPENAPI_LICENCE_IDENTIFIER)
                .url(env.OPENAPI_LICENCE_URL);

        Info info = new Info()
                .contact(contact)
                .license(licence)
                .title(env.OPENAPI_INFO_TITLE)
                .summary(env.OPENAPI_INFO_SUMMARY)
                .version(env.OPENAPI_INFO_VERSION)
                .termsOfService(env.OPENAPI_INFO_TOS)
                .description(env.OPENAPI_INFO_DESCRIPTION);

        return new OpenAPI()
                .info(info)
                .servers(List.of(server));
    }
}