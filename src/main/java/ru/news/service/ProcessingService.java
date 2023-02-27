package ru.news.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessingService {
    private final static String EXT_MD = ".md";
    private final static String EXT_HTML = ".html";

    private final StorageService storageService;
    private final ConvertingService convertingService;

    @PostConstruct
    void init() {
        processMdFiles();
    }

    public void processMdFiles() {
        System.out.println(storageService.getAllMdFiles());
//        processMdFiles(storageService.getAllMdFiles());
    }

    private void processMdFiles(Collection<String> files) {
        var list = files.stream()
                .filter(this::isMdFile)
                .filter(this::isFileChanged)
                .map(this::processMdFileTask)
                .map(CompletableFuture::supplyAsync)
                .toList();

        CompletableFuture.allOf(list.toArray(new CompletableFuture[0])).join();
    }

    private Supplier<Boolean> processMdFileTask(String file) {
        return () -> {
            try {
                Path tempFile = Files.createTempFile("temp", EXT_HTML);
                try (InputStream md = storageService.getMdFileContent(file)) {
                    try (FileOutputStream output = new FileOutputStream(tempFile.toFile())) {
                        convertingService.convertMdToHtml(md, output);
                    }
                    storageService.putHtmlFile(linkedHtmlFile(file), tempFile.toFile());
                    return true;
                } finally {
                    Files.deleteIfExists(tempFile);
                }
            } catch (Exception exception) {
                log.error("Can not convert md file {}", file, exception);
                return false;
            }
        };
    }

    private boolean isFileChanged(String file) {
        LocalDateTime mdModified = storageService.getMdFileModificationTime(file);
        LocalDateTime htmlModified = storageService.getHtmlFileModificationTime(linkedHtmlFile(file));
        return mdModified.isAfter(htmlModified);
    }

    private String linkedHtmlFile(String file) {
        return FilenameUtils.removeExtension(file) + EXT_HTML;
    }

    private boolean isMdFile(String file) {
        return file.toLowerCase().endsWith(EXT_MD);
    }
}
