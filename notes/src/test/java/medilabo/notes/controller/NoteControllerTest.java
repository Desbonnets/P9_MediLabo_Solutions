package medilabo.notes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import medilabo.notes.model.Note;
import medilabo.notes.service.NoteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NoteController.class)
class NoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private NoteService noteService;

    private Note sampleNote() {
        return new Note(1L, "Patient présente un cholestérol élevé.");
    }

    @Test
    void getByPatient_returns200WithList() throws Exception {
        when(noteService.findByPatId(1L)).thenReturn(List.of(sampleNote()));
        mockMvc.perform(get("/api/notes/patient/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Patient présente un cholestérol élevé."));
    }

    @Test
    void getById_existingId_returns200() throws Exception {
        when(noteService.findById("abc")).thenReturn(sampleNote());
        mockMvc.perform(get("/api/notes/abc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Patient présente un cholestérol élevé."));
    }

    @Test
    void getById_unknownId_returns404() throws Exception {
        when(noteService.findById("xyz"))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));
        mockMvc.perform(get("/api/notes/xyz"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_validBody_returns201() throws Exception {
        Note note = sampleNote();
        when(noteService.create(any())).thenReturn(note);

        mockMvc.perform(post("/api/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(note)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("Patient présente un cholestérol élevé."));
    }

    @Test
    void update_existingId_returns200() throws Exception {
        Note note = sampleNote();
        when(noteService.update(eq("abc"), any())).thenReturn(note);

        mockMvc.perform(put("/api/notes/abc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(note)))
                .andExpect(status().isOk());
    }

    @Test
    void update_unknownId_returns404() throws Exception {
        when(noteService.update(eq("xyz"), any()))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        mockMvc.perform(put("/api/notes/xyz")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleNote())))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_existingId_returns204() throws Exception {
        mockMvc.perform(delete("/api/notes/abc"))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_unknownId_returns404() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND))
                .when(noteService).delete("xyz");
        mockMvc.perform(delete("/api/notes/xyz"))
                .andExpect(status().isNotFound());
    }
}
