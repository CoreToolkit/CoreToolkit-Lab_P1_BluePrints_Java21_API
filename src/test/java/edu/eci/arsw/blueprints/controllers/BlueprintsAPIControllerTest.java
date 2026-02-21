package edu.eci.arsw.blueprints.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import edu.eci.arsw.blueprints.persistence.BlueprintAlreadyExistsException;
import edu.eci.arsw.blueprints.persistence.BlueprintNotFoundException;
import edu.eci.arsw.blueprints.services.BlueprintsServices;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BlueprintsAPIController.class)
@Import(RestExceptionHandler.class)
class BlueprintsAPIControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BlueprintsServices services;

    @Test
    @DisplayName("GET /api/v1/blueprints returns 200 with ApiResponse envelope")
    void getAllBlueprints() throws Exception {
        Set<Blueprint> blueprints = Set.of(new Blueprint("john", "house", List.of(new Point(0, 0))));
        when(services.getAllBlueprints()).thenReturn(blueprints);

        mockMvc.perform(get("/api/v1/blueprints"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("execute ok"))
                .andExpect(jsonPath("$.data[0].author").value("john"))
                .andExpect(jsonPath("$.data[0].name").value("house"));
    }

    @Test
    @DisplayName("POST /api/v1/blueprints creates blueprint and returns 201")
    void createBlueprint() throws Exception {
        doNothing().when(services).addNewBlueprint(new Blueprint("john", "villa", List.of(new Point(1, 2))));

        String payload = objectMapper.writeValueAsString(Map.of(
                "author", "john",
                "name", "villa",
                "points", List.of(Map.of("x", 1, "y", 2))
        ));

        mockMvc.perform(post("/api/v1/blueprints")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.message").value("created"));
    }

    @Test
    @DisplayName("GET /api/v1/blueprints/{author} returns 404 when not found")
    void getByAuthorNotFound() throws Exception {
        when(services.getBlueprintsByAuthor("ghost")).thenThrow(new BlueprintNotFoundException("Author not found"));

        mockMvc.perform(get("/api/v1/blueprints/ghost"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("Author not found"));
    }

    @Test
    @DisplayName("POST /api/v1/blueprints returns 409 when blueprint already exists")
    void createBlueprintConflict() throws Exception {
        doThrow(new BlueprintAlreadyExistsException("Blueprint already exists: john/house"))
                .when(services).addNewBlueprint(new Blueprint("john", "house", List.of()));

        String payload = objectMapper.writeValueAsString(Map.of(
                "author", "john",
                "name", "house",
                "points", List.of()
        ));

        mockMvc.perform(post("/api/v1/blueprints")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(409))
                .andExpect(jsonPath("$.message").value("Blueprint already exists: john/house"));
    }

    @Test
    @DisplayName("POST /api/v1/blueprints validates empty author and returns 400 ApiResponse")
    void createBlueprintValidationError() throws Exception {
        String payload = objectMapper.writeValueAsString(Map.of(
                "author", "",
                "name", "house",
                "points", List.of()
        ));

        mockMvc.perform(post("/api/v1/blueprints")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message", containsString("author: must not be blank")));
    }
}
