package io.krakau.genaifinderapi.controller;

import io.krakau.genaifinderapi.service.FinderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Dominik
 */
//@CrossOrigin(origins = "*") // just for testing
@Tag(name = "Finder", description = "Finding generative AI content")
@RestController
@RequestMapping("/finder")
public class FinderController {
    
     private final FinderService finderService;

    @Autowired
    public FinderController(FinderService finderService) {
        this.finderService = finderService;
    }
    
    @Operation(
            summary = "Search Assets by query",
            description = "Searching for Assets by a specific query.",
            tags = {"Finder"})
    @ApiResponses({
        @ApiResponse(responseCode = "200",
                description = "Assets that have matched the query.",
                content = {
                    @Content(schema = @Schema(implementation = Slice.class), mediaType = "application/json")}),
        @ApiResponse(responseCode = "400",
                description = "Error message if the request failed.",
                content = {
                    @Content(schema = @Schema(implementation = Error.class), mediaType = "application/json")})
    })
    @GetMapping("/getHelloWorld")
    public ResponseEntity<String> getHelloWorld() throws Exception {
        return ResponseEntity.ok().body(this.finderService.getHelloWorld());
    }
    
}
