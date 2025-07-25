package io.krakau.genaifinderapi.controller;

import io.krakau.genaifinderapi.schema.iscc.ExplainedISCC;
import io.krakau.genaifinderapi.schema.iscc.ISCC;
import io.krakau.genaifinderapi.service.IsccWebService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Dominik
 */
@CrossOrigin(origins = "*")
@Tag(name = "ISCC", description = "International Standard Content Code")
@RestController
@RequestMapping("/iscc")
public class IsccController {
    
    private final IsccWebService isccWebService;

    @Autowired
    public IsccController(IsccWebService isccWebService) {
        this.isccWebService = isccWebService;
    }
    
    @Operation(
            summary = "Endpoint for creating ISCC from file",
            description = "This api endpoint is beeing used for creating an ISCC from uploaded file.",
            tags = {"ISCC"})
    @ApiResponses({
        @ApiResponse(responseCode = "201",
                description = "Created ISCC",
                content = {
                    @Content(schema = @Schema(implementation = ISCC.class), mediaType = "application/json")}),
        @ApiResponse(responseCode = "400",
                description = "Error message if the request failed",
                content = {
                    @Content(schema = @Schema(implementation = Error.class), mediaType = "application/json")})
                })
    @PostMapping(
            value = "/create",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE} // Note the consumes in the mapping
    )
    public ResponseEntity<ISCC> createIscc(@Parameter(description = "Files to be uploaded", content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)) @RequestParam("file") MultipartFile file) throws IOException, Exception {
        return ResponseEntity.ok().body(this.isccWebService.createISCC(file.getInputStream(), file.getOriginalFilename()));
    }
    
    @Operation(
            summary = "Endpoint for creating ISCC from base64 encoded url",
            description = "This api endpoint is beeing used for creating an ISCC from content presend at Base64 encoded resource url.",
            tags = {"ISCC"})
    @ApiResponses({
        @ApiResponse(responseCode = "201",
                description = "Created ISCC",
                content = {
                    @Content(schema = @Schema(implementation = ISCC.class), mediaType = "application/json")}),
        @ApiResponse(responseCode = "400",
                description = "Error message if the request failed",
                content = {
                    @Content(schema = @Schema(implementation = Error.class), mediaType = "application/json")})
    })
    @PostMapping("/create/{urlBase64}")
    public ResponseEntity<ISCC> createISCCFromUrl(@Parameter(description = "Base64 encoded url", example = "aHR0cHM6Ly9wYnMudHdpbWcuY29tL21lZGlhL0dyS3ZqanBXQUFBaDFJej9mb3JtYXQ9anBnJm5hbWU9bGFyZ2U") @PathVariable String urlBase64) throws Exception {
        // https://pbs.twimg.com/media/GrKvjjpWAAAh1Iz?format=jpg&name=large aHR0cHM6Ly9wYnMudHdpbWcuY29tL21lZGlhL0dyS3ZqanBXQUFBaDFJej9mb3JtYXQ9anBnJm5hbWU9bGFyZ2U=
        return ResponseEntity.ok().body(this.isccWebService.createIsccFromUrl(urlBase64));
    }
    
    @Operation(
            summary = "Endpoint for explaining an ISCC",
            description = "This api endpoint is beeing used for explaining an ISCC.",
            tags = {"ISCC"})
    @ApiResponses({
        @ApiResponse(responseCode = "200",
                description = "Explained ISCC",
                content = {
                    @Content(schema = @Schema(implementation = ExplainedISCC.class), mediaType = "application/json")}),
        @ApiResponse(responseCode = "400",
                description = "Error message if the request failed",
                content = {
                    @Content(schema = @Schema(implementation = Error.class), mediaType = "application/json")})
    })
    @GetMapping("/explain/{iscc}")
    public ResponseEntity<ExplainedISCC> explainIscc(@Parameter(description = "ISCC that will be explained", example = "ISCC:KEC2G5AYLD6L665LQFZQFCW57VZKGGCDAIQFEYVGU4XWYQPDPD2IVSI") @PathVariable String iscc) throws Exception {
        return ResponseEntity.ok().body(this.isccWebService.explainISCC(iscc));
    }
    
}