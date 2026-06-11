package medilabo.back.controller;

import tools.jackson.databind.ObjectMapper;
import medilabo.back.model.User;
import medilabo.back.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    private User sampleUser() {
        return new User("Jean", "Dupont", LocalDate.of(1980, 3, 15), "M", "1 rue de la Paix", "01-23-45-67-89");
    }

    @Test
    void getAll_returns200WithList() throws Exception {
        when(userService.findAll()).thenReturn(List.of(sampleUser()));
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Jean"));
    }

    @Test
    void getById_existingId_returns200() throws Exception {
        when(userService.findById(1L)).thenReturn(sampleUser());
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Dupont"));
    }

    @Test
    void getById_unknownId_returns404() throws Exception {
        when(userService.findById(99L))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));
        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_validBody_returns201() throws Exception {
        User user = sampleUser();
        when(userService.create(any())).thenReturn(user);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Jean"));
    }

    @Test
    void create_missingRequiredField_returns400() throws Exception {
        String body = """
                {"firstName":"","lastName":"","birthDate":"1980-03-15","gender":""}
                """;
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_existingId_returns200() throws Exception {
        User user = sampleUser();
        when(userService.update(eq(1L), any())).thenReturn(user);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());
    }

    @Test
    void update_unknownId_returns404() throws Exception {
        when(userService.update(eq(99L), any()))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        mockMvc.perform(put("/api/users/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleUser())))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_existingId_returns204() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_unknownId_returns404() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND))
                .when(userService).delete(99L);
        mockMvc.perform(delete("/api/users/99"))
                .andExpect(status().isNotFound());
    }
}
