package io.krakau.genaifinderapi.configuration;

import io.krakau.genaifinderapi.GenaifinderapiApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;

/**
 *
 * @author Dominik
 */
@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI myOpenAPI() {
        Server server = new Server();
        server.setUrl(GenaifinderapiApplication.env.getProperty("springdoc.api-docs.server.url"));
        server.setDescription(GenaifinderapiApplication.env.getProperty("springdoc.api-docs.server.description"));

        Contact contact = new Contact();
        contact.setEmail(GenaifinderapiApplication.env.getProperty("springdoc.api-docs.contact.email"));
        contact.setName(GenaifinderapiApplication.env.getProperty("springdoc.api-docs.contact.name"));
        contact.setUrl(GenaifinderapiApplication.env.getProperty("springdoc.api-docs.contact.url"));

        License licence = new License()
                .name(GenaifinderapiApplication.env.getProperty("springdoc.api-docs.licence.name"))
                .identifier(GenaifinderapiApplication.env.getProperty("springdoc.api-docs.licence.identifier"))
                .url(GenaifinderapiApplication.env.getProperty("springdoc.api-docs.licence.url"));

        Info info = new Info()
                .contact(contact)
                .license(licence)
                .title(GenaifinderapiApplication.env.getProperty("springdoc.api-docs.info.title"))
                .summary(GenaifinderapiApplication.env.getProperty("springdoc.api-docs.info.summary"))
                .version(GenaifinderapiApplication.env.getProperty("springdoc.api-docs.info.vision"))
                .termsOfService(GenaifinderapiApplication.env.getProperty("springdoc.api-docs.info.terms-of-service"))
                .description(GenaifinderapiApplication.env.getProperty("springdoc.api-docs.info.description"));

        return new OpenAPI()
                .info(info)
                .servers(List.of(server));
    }
}