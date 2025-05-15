package io.krakau.genaifinderapi.controller;

import io.krakau.genaifinderapi.schema.mongodb.Asset;
import io.krakau.genaifinderapi.service.CreateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Dominik
 */
@CrossOrigin(origins = "localhost") // just for testing
@Tag(
    name = "Create",
    description = "Create an entry for assets")
@RestController
@RequestMapping("/create")
public class CreateController {
    
     private final CreateService createService;

    @Autowired
    public CreateController(CreateService createService) {
        this.createService = createService;
    }
    
    @Operation(
            summary = "Creates an image asset",
            description = "Creating an image asset.",
            tags = {"Create"})
    @ApiResponses({
        @ApiResponse(responseCode = "200",
                description = "Asset have been created.",
                content = {
                    @Content(schema = @Schema(implementation = Slice.class),
                    mediaType = "application/json")
                }),
        @ApiResponse(responseCode = "400",
                description = "Error message if the request failed.",
                content = {
                    @Content(schema = @Schema(implementation = Error.class),
                    mediaType = "application/json")
                })
    })
 @RequestMapping(
    path = "/image", 
    method = RequestMethod.POST, 
    consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Asset> createImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("provider") String provider,
            @RequestParam("prompt") String prompt,
            @RequestParam("timestamp") Long timestamp) throws Exception {
        return ResponseEntity.ok().body(this.createService.createImage(file, provider, prompt, timestamp));
    }
    
}
