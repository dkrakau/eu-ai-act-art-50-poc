package io.krakau.genaifinderapi.controller;

import io.krakau.genaifinderapi.schema.mongodb.Asset;
import io.krakau.genaifinderapi.service.FindService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
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
@Tag(name = "Find", description = "Find generated AI assets")
@RestController
@RequestMapping("/find")
public class FindController {

    private final FindService findService;

    @Autowired
    public FindController(FindService findService) {
        this.findService = findService;
    }

    @Operation(
            summary = "Endpoint for finding image assets",
            description = "This api endpoint is beeing used for finding image assets based on nearest neighbour vector search.",
            tags = {"Find"})
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
    @GetMapping("/image")
    public ResponseEntity<List<Asset>> findImage(
            @PathVariable("url")
            @Parameter(description = "URL of an image resource.", example = "https://pbs.twimg.com/media/GrAdSm-WAAA0yxV?format=jpg&name=large")
            @RequestParam String url) throws Exception {
        return ResponseEntity.ok().body(this.findService.findImage(url));
    }

}
