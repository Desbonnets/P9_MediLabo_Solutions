package medilabo.notes.service;

import medilabo.notes.model.Note;
import medilabo.notes.repository.NoteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

    @Mock
    private NoteRepository noteRepository;

    @InjectMocks
    private NoteService noteService;

    @Test
    void findByPatId_returnsNotesForPatient() {
        Note n1 = new Note(1L, "Note A");
        Note n2 = new Note(1L, "Note B");
        when(noteRepository.findByPatId(1L)).thenReturn(List.of(n1, n2));

        List<Note> result = noteService.findByPatId(1L);

        assertThat(result).hasSize(2);
    }

    @Test
    void findById_existingId_returnsNote() {
        Note note = new Note(1L, "Contenu");
        when(noteRepository.findById("abc")).thenReturn(Optional.of(note));

        assertThat(noteService.findById("abc").getContent()).isEqualTo("Contenu");
    }

    @Test
    void findById_unknownId_throws404() {
        when(noteRepository.findById("xyz")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> noteService.findById("xyz"))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("statusCode", HttpStatus.NOT_FOUND);
    }

    @Test
    void create_savesNote() {
        Note note = new Note(2L, "Nouvelle note");
        when(noteRepository.save(note)).thenReturn(note);

        Note result = noteService.create(note);

        assertThat(result.getContent()).isEqualTo("Nouvelle note");
        verify(noteRepository).save(note);
    }

    @Test
    void update_existingId_updatesContent() {
        Note existing = new Note(1L, "Ancien contenu");
        when(noteRepository.findById("abc")).thenReturn(Optional.of(existing));
        when(noteRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Note updated = new Note(1L, "Nouveau contenu");
        Note result = noteService.update("abc", updated);

        assertThat(result.getContent()).isEqualTo("Nouveau contenu");
    }

    @Test
    void update_unknownId_throws404() {
        when(noteRepository.findById("xyz")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> noteService.update("xyz", new Note(1L, "x")))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("statusCode", HttpStatus.NOT_FOUND);
    }

    @Test
    void delete_existingId_callsRepository() {
        when(noteRepository.findById("abc")).thenReturn(Optional.of(new Note(1L, "x")));

        noteService.delete("abc");

        verify(noteRepository).deleteById("abc");
    }

    @Test
    void delete_unknownId_throws404() {
        when(noteRepository.findById("xyz")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> noteService.delete("xyz"))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("statusCode", HttpStatus.NOT_FOUND);
        verify(noteRepository, never()).deleteById(any());
    }
}
