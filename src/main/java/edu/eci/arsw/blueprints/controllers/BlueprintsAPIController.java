package edu.eci.arsw.blueprints.controllers;

import edu.eci.arsw.blueprints.model.ApiResponse;
import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import edu.eci.arsw.blueprints.persistence.BlueprintNotFoundException;
import edu.eci.arsw.blueprints.persistence.BlueprintPersistenceException;
import edu.eci.arsw.blueprints.services.BlueprintsServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@Validated
@RestController
@RequestMapping("/api/v1/blueprints")
public class BlueprintsAPIController {

    private final BlueprintsServices services;

    public BlueprintsAPIController(BlueprintsServices services) { this.services = services; }

        // GET /api/v1/blueprints
    @Operation(summary = "List all blueprints")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "execute ok",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(value = "{\"code\":200,\"message\":\"execute ok\",\"data\":[]}"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500", description = "internal error",
            content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @GetMapping
    public ResponseEntity<ApiResponse<Set<Blueprint>>> getAll() {
        Set<Blueprint> data = services.getAllBlueprints();
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "execute ok", data));
    }

    // GET /api/v1/blueprints/{author}
    @Operation(summary = "Get blueprints by author")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "execute ok",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(value = "{\"code\":200,\"message\":\"execute ok\",\"data\":[{\"author\":\"john\",\"name\":\"house\"}]}"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500", description = "internal error",
            content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @GetMapping("/{author}")
    public ResponseEntity<ApiResponse<Set<Blueprint>>> byAuthor(@PathVariable String author)
            throws BlueprintNotFoundException {
        try {
            Set<Blueprint> data = services.getBlueprintsByAuthor(author);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "execute ok", data));
        } catch (BlueprintNotFoundException e) {
            // Se considera respuesta vacía como 200 para mantener idempotencia de consulta
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "execute ok", Set.of()));
        }
    }

    // GET /api/v1/blueprints/{author}/{bpname}
    @Operation(summary = "Get blueprint by author and name")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "execute ok",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(value = "{\"code\":200,\"message\":\"execute ok\",\"data\":{\"author\":\"john\",\"name\":\"house\"}}"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404", description = "blueprint not found",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500", description = "internal error",
            content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @GetMapping("/{author}/{bpname}")
    public ResponseEntity<ApiResponse<Blueprint>> byAuthorAndName(@PathVariable String author, @PathVariable String bpname)
            throws BlueprintNotFoundException {
        Blueprint data = services.getBlueprint(author, bpname);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "execute ok", data));
    }

        // POST /api/v1/blueprints
    @Operation(summary = "Create a blueprint")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201", description = "created",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(value = "{\"code\":201,\"message\":\"created\",\"data\":null}"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400", description = "validation/business error",
            content = @Content(schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(value = "{\"code\":400,\"message\":\"author: must not be blank\",\"data\":null}"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "409", description = "duplicate blueprint",
            content = @Content(schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(value = "{\"code\":409,\"message\":\"Blueprint already exists: john/house\",\"data\":null}"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500", description = "internal error",
            content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> add(@Valid @RequestBody NewBlueprintRequest req)
            throws BlueprintPersistenceException {
        Blueprint bp = new Blueprint(req.author(), req.name(), req.points());
        services.addNewBlueprint(bp);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(HttpStatus.CREATED.value(), "created", null));
    }

    // PUT /api/v1/blueprints/{author}/{bpname}/points
    @Operation(summary = "Add a point to an existing blueprint")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "202", description = "updated",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(value = "{\"code\":202,\"message\":\"updated\",\"data\":null}"))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404", description = "blueprint not found",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400", description = "invalid payload",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500", description = "internal error",
            content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @PutMapping("/{author}/{bpname}/points")
    public ResponseEntity<ApiResponse<Void>> addPoint(@PathVariable String author, @PathVariable String bpname,
                                                      @RequestBody Point p)
            throws BlueprintNotFoundException {
        services.addPoint(author, bpname, p.x(), p.y());
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new ApiResponse<>(HttpStatus.ACCEPTED.value(), "updated", null));
    }

    public record NewBlueprintRequest(
            @NotBlank String author,
            @NotBlank String name,
            @Valid java.util.List<Point> points
    ) { }
}
