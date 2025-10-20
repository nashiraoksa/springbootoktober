package id.co.bpddiy.social.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger Configuration
 * Setup untuk dokumentasi API dengan authentication support
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // API Info
                .info(new Info()
                        .title("Blog API Documentation")
                        .version("1.0.0")
                        .description("Comprehensive REST API untuk Blog System dengan fitur authentication, " +
                                   "posts management, comments, dan user management. " +
                                   "API ini menggunakan JWT authentication dan role-based authorization.")
                        .contact(new Contact()
                                .name("Blog API Support")
                                .email("support@blogapi.com")
                                .url("https://github.com/example/blog-api"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                
                // Server Configuration
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Local Development Server"),
                        new Server()
                                .url("https://api.blogapp.com")
                                .description("Production Server")))
                
                // Security Configuration
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Masukkan JWT token yang didapat dari endpoint /api/auth/login")));
    }
}