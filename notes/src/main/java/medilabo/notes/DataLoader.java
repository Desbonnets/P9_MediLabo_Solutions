package medilabo.notes;

import medilabo.notes.model.Note;
import medilabo.notes.repository.NoteRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final NoteRepository noteRepository;

    public DataLoader(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    @Override
    public void run(String... args) {
        if (noteRepository.count() > 0) {
            return;
        }

        // Patient 1 — TestNone
        noteRepository.save(new Note(1L,
                "Le patient déclare qu'il 'se sent très bien'\nPoids égal ou inférieur au poids recommandé"));

        // Patient 2 — TestBorderline
        noteRepository.save(new Note(2L,
                "Le patient déclare qu'il ressent beaucoup de stress au travail\nIl se plaint également que son audition est anormale dernièrement"));
        noteRepository.save(new Note(2L,
                "Le patient déclare avoir fait une réaction aux médicaments au cours des 3 derniers mois\nIl remarque également que son audition continue d'être anormale"));

        // Patient 3 — TestInDanger
        noteRepository.save(new Note(3L,
                "Le patient déclare qu'il fume depuis peu"));
        noteRepository.save(new Note(3L,
                "Le patient déclare qu'il est fumeur et qu'il a cessé de fumer l'année dernière\nIl se plaint également de crises d'apnée respiratoire anormales\nTests de laboratoire indiquant un taux de cholestérol LDL élevé"));

        // Patient 4 — TestEarlyOnset
        noteRepository.save(new Note(4L,
                "Le patient déclare qu'il lui est devenu difficile de monter les escaliers\nIl se plaint également d'être essoufflé\nTests de laboratoire indiquant que les anticorps sont élevés\nRéaction aux médicaments"));
        noteRepository.save(new Note(4L,
                "Le patient déclare qu'il a mal au dos lorsqu'il reste assis pendant longtemps"));
        noteRepository.save(new Note(4L,
                "Le patient déclare avoir commencé à fumer depuis peu\nHémoglobine A1C supérieure au niveau recommandé"));
        noteRepository.save(new Note(4L,
                "Taille, Poids, Cholestérol, Vertige et Réaction"));

        System.out.println("9 notes de test chargées.");
    }
}
