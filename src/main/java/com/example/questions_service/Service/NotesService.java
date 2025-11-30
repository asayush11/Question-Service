package com.example.questions_service.Service;

import com.example.questions_service.Cache.NotesCache;
import com.example.questions_service.Entity.Notes;
import com.example.questions_service.Entity.NotesId;
import com.example.questions_service.Repository.NotesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NotesService {
    @Autowired
    NotesRepository notesRepository;
    @Autowired
    NotesCache notesCache;
    private static final Logger logger = LoggerFactory.getLogger(NotesService.class);

    public List<String> getTopicsBySubject(String subject) {
        logger.info("Fetching topics for subject: {}", subject);
        return notesRepository.findBySubject(subject);
    }

    public String getContentBySubjectAndTopic(String subject, String topic) {
        String cachedContent = notesCache.get(subject, topic);
        if (cachedContent != null) {
            return cachedContent;
        } else {
            logger.info("Fetching content for subject: {} and topic: {}", subject, topic);
            NotesId id = new NotesId(topic, subject);
            String content = String.valueOf(notesRepository.findById(id));
            if (content != null && !content.isEmpty()) {
                notesCache.put(subject, topic, content);
                return content;
            }
            return "Stay tuned for updates!";
        }
    }

    public void addNote(String subject, String topic, String content) {
        logger.info("Adding note for subject: {}, topic: {}", subject, topic);
        notesRepository.save(new Notes(topic, subject, content));
    }

    public Notes updateNote(String topic, String subject, String newContent) {
        logger.info("Updating note for subject: {}, topic: {}", subject, topic);
        Notes newNote = new Notes(topic, subject, newContent);
        notesCache.put(subject, topic, newContent);
        return notesRepository.save(newNote);
    }

    public void deleteNote(String topic, String subject) {
        logger.info("Deleting note for subject: {}, topic: {}", subject, topic);
        NotesId id = new NotesId(topic, subject);
        notesRepository.deleteById(id);
        notesCache.remove(subject, topic);
    }
}
