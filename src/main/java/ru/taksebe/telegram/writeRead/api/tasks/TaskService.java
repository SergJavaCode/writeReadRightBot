package ru.taksebe.telegram.writeRead.api.tasks;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import ru.taksebe.telegram.writeRead.api.dictionaries.DictionaryRepository;
import ru.taksebe.telegram.writeRead.constants.resources.DictionaryResourcePathEnum;
import ru.taksebe.telegram.writeRead.exceptions.UserDictionaryNotFoundException;
import ru.taksebe.telegram.writeRead.model.Dictionary;
import ru.taksebe.telegram.writeRead.model.Word;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class TaskService {
    DictionaryRepository repository;

    public ByteArrayResource getAllDefaultDictionariesTasksDocument() throws IOException {
        List<Dictionary> defaultDictionaryList = Arrays.stream(DictionaryResourcePathEnum.values())
                .map(resourcePath -> repository.findById(resourcePath.name()).orElseThrow(UserDictionaryNotFoundException::new))
                .collect(Collectors.toList());
        return new ByteArrayResource(new byte[1]);
    }

    public ByteArrayResource getTasksDocument(String dictionaryId, String fileName) throws IOException {
        Dictionary dictionary = repository.findById(dictionaryId).orElseThrow(UserDictionaryNotFoundException::new);
        return new ByteArrayResource(new byte[1]);
    }




    private List<String> getVariants(Word word) {
        List<String> mistakes = new ArrayList<>(word.getMistakes());
        mistakes.add(word.getWord());
        Collections.shuffle(mistakes);
        return mistakes;
    }

    private void setValue(XWPFRun run, String text) {
        run.setFontSize(14);
        run.setFontFamily("Times New Roman");
        run.setText(text);
    }
}