package legion.google.bard;

import legion.google.bard.domain.Answer;

public interface AIClient {
    Answer ask(String question);
    void reset();
}
