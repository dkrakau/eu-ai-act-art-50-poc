package io.krakau.genaifinderapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Dominik
 */
@CrossOrigin(origins = "*")
@Tag(name = "Resources", description = "Get generated AI data")
@RestController
@RequestMapping("/resources")
public class ResourceController {

    private static Logger logger = Logger.getLogger(ResourceController.class.getName());

    @Value("${spring.storage.dir}")
    private String resourcesDir;
    
    @Operation(
            summary = "Endpoint to get image data",
            description = "This api endpoint is beeing used for getting image resources.",
            tags = {"Resources"})
    @ApiResponses({
        @ApiResponse(responseCode = "200",
                description = "Resources that have been served"),
        @ApiResponse(responseCode = "400",
                description = "Error message if the request failed",
                content = {
                    @Content(schema = @Schema(implementation = Error.class), mediaType = "application/json")})
    })
    @GetMapping("/images/{filename}")
    public ResponseEntity<Resource> getImage(@Parameter(description = "Filename of an image resource", example = "Zerstoertes Haus in Berlin.png") @PathVariable String filename) throws Exception {
        
        Resource resource = null;
        logger.log(Level.INFO, "Accessing file: " + filename);

        // Validate filename to prevent directory traversal attacks
        if (filename.contains("..")) {
            logger.log(Level.WARNING, "Invalid file access attempt: " + filename);
            return ResponseEntity.badRequest().build();
        }

        try {
            Path fileLocation = Paths.get(resourcesDir).normalize().toAbsolutePath();
            Path filePath = fileLocation.resolve(filename).normalize();

            // Security check - make sure the resolved path is within the storage directory
            if (!filePath.toAbsolutePath().startsWith(fileLocation)) {
                logger.log(Level.WARNING, "Directory traversal attempt: " + filename);
                return ResponseEntity.badRequest().build();
            }

            // Create load resource
            resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                logger.log(Level.WARNING, "File not found or not readable: " + filename);
                return ResponseEntity.notFound().build();
            }

            // Determine content type
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            
            // Return response
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
            
        } catch (MalformedURLException e) {
            logger.log(Level.SEVERE, "Malformed URL for file: " + e);
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error while accessing file: " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}