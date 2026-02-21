package org.sani.algolog.global.error;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TestExceptionController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("custom exception is converted to ApiResponse")
    void handleCustomException() throws Exception {
        mockMvc.perform(get("/test/custom"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Target resource does not exist."))
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()));
    }

    @Test
    @DisplayName("validation error returns INVALID_INPUT with field details")
    void handleValidationException() throws Exception {
        mockMvc.perform(post("/test/valid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("INVALID_INPUT"))
                .andExpect(jsonPath("$.data[0].field").value("title"))
                .andExpect(jsonPath("$.data[0].reason").value("title is required"));
    }

    @Test
    @DisplayName("type mismatch returns TYPE_MISMATCH")
    void handleTypeMismatchException() throws Exception {
        mockMvc.perform(get("/test/type").param("count", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("TYPE_MISMATCH"));
    }

    @Test
    @DisplayName("missing parameter returns MISSING_PARAMETER")
    void handleMissingParameterException() throws Exception {
        mockMvc.perform(get("/test/required"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("MISSING_PARAMETER"));
    }

    @Test
    @DisplayName("method not allowed returns METHOD_NOT_ALLOWED")
    void handleMethodNotAllowedException() throws Exception {
        mockMvc.perform(post("/test/type"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.status").value(405))
                .andExpect(jsonPath("$.code").value("METHOD_NOT_ALLOWED"));
    }

    @Test
    @DisplayName("malformed body returns MESSAGE_NOT_READABLE")
    void handleMessageNotReadableException() throws Exception {
        mockMvc.perform(post("/test/valid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("MESSAGE_NOT_READABLE"));
    }

    @Test
    @DisplayName("unexpected exception returns INTERNAL_SERVER_ERROR")
    void handleUnknownException() throws Exception {
        mockMvc.perform(get("/test/unexpected"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"));
    }

    @Test
    @DisplayName("entity not found exception returns NOT_FOUND")
    void handleEntityNotFoundException() throws Exception {
        mockMvc.perform(get("/test/jpa-not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }

    @Test
    @DisplayName("access denied exception returns FORBIDDEN")
    void handleAccessDeniedException() throws Exception {
        mockMvc.perform(get("/test/forbidden"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.code").value("FORBIDDEN"));
    }

}
