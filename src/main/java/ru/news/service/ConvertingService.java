package ru.news.service;

import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.news.properties.AppProperties;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Service
@RequiredArgsConstructor
public class ConvertingService {
    private final AppProperties properties;

    public void convertMdToHtml(InputStream md, OutputStream html) throws IOException {
        Process pandocProcess = new ProcessBuilder(properties.getConvertParams()).start();
        try (OutputStream outputStream = pandocProcess.getOutputStream()) {
            IOUtils.copy(md, outputStream);
        }

        try (InputStream inputStream = pandocProcess.getInputStream()) {
            IOUtils.copy(inputStream, html);
        }
    }
}
