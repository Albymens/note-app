package com.albymens.note_app.config;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI(){
        Tag noteTag = new Tag();
         noteTag.name("Note Management")
                .description("Endpoints for creating, editing, deleting (soft delete), restoring, filtering, and searching user notes.");

         return new OpenAPI()
                 .info(new Info()
                         .title("Notes App API")
                         .description("""
                                A Spring Boot notes application with JWT authentication, built with Java 21 and modern web technologies.

                                This API provides a comprehensive set of endpoints for managing user notes — including creation, editing, soft deletion, restoration, filtering by tags, and keyword-based search across titles and content.
                                
                                📂 **Source Code**: [🛠️GitHub Repository](https://github.com/Albymens/note-app.git)
                               
                                💻 **Live Demo**: [🌐 UI Demo (/signup)](https://note-app-uzp9.onrender.com/signup)
                                
                                ⚠️ Note: The application is hosted on Render and may take 2–3 minutes to load initially due to cold starts. If it does not appear after 3 minutes, please refresh the page.
                                """))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components().addSecuritySchemes("Bearer Authentication",
                        new SecurityScheme()
                                .name("Bearer Authentication")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                        .bearerFormat("JWT")))
                 .tags(List.of(noteTag));
    }
}
