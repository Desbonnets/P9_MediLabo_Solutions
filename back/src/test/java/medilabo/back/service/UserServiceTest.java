package medilabo.back.service;

import medilabo.back.model.User;
import medilabo.back.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserService service;

    private User sampleUser() {
        return new User("Jean", "Dupont", LocalDate.of(1980, 3, 15), "M", "1 rue de la Paix", "01-23-45-67-89");
    }

    @Test
    void findAll_returnsAll() {
        when(repository.findAll()).thenReturn(List.of(sampleUser()));
        assertThat(service.findAll()).hasSize(1);
    }

    @Test
    void findById_existingId_returnsUser() {
        User user = sampleUser();
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        assertThat(service.findById(1L)).isEqualTo(user);
    }

    @Test
    void findById_unknownId_throws404() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("statusCode", HttpStatus.NOT_FOUND);
    }

    @Test
    void create_savesAndReturnsUser() {
        User user = sampleUser();
        when(repository.save(user)).thenReturn(user);
        assertThat(service.create(user)).isEqualTo(user);
        verify(repository).save(user);
    }

    @Test
    void update_existingId_updatesFields() {
        User existing = sampleUser();
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        User updated = new User("Marie", "Martin", LocalDate.of(1990, 6, 1), "F", "2 av. Victor Hugo", "06-00-00-00-00");
        User result = service.update(1L, updated);

        assertThat(result.getFirstName()).isEqualTo("Marie");
        assertThat(result.getLastName()).isEqualTo("Martin");
        assertThat(result.getGender()).isEqualTo("F");
    }

    @Test
    void update_unknownId_throws404() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.update(99L, sampleUser()))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("statusCode", HttpStatus.NOT_FOUND);
    }

    @Test
    void delete_existingId_callsRepository() {
        when(repository.findById(1L)).thenReturn(Optional.of(sampleUser()));
        service.delete(1L);
        verify(repository).deleteById(1L);
    }

    @Test
    void delete_unknownId_throws404() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("statusCode", HttpStatus.NOT_FOUND);
        verify(repository, never()).deleteById(any());
    }
}
