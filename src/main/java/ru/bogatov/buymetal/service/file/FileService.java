package ru.bogatov.buymetal.service.file;

import com.jlefebure.spring.boot.minio.MinioException;
import com.jlefebure.spring.boot.minio.MinioService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.bogatov.buymetal.entity.File;
import ru.bogatov.buymetal.error.ApplicationError;
import ru.bogatov.buymetal.error.ErrorUtils;
import ru.bogatov.buymetal.repository.FileRepository;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.UUID;

@Service
@Slf4j
public class FileService {

    private final FileRepository fileRepository;
    private final MinioService minioService;
    @Value("${spring.minio.url}")
    private String minioHost;
    @Value("${spring.minio.bucket}")
    private String minioBucket;


    public FileService(FileRepository fileRepository, MinioService minioService) {
        this.fileRepository = fileRepository;
        this.minioService = minioService;
    }

    public File saveFile(MultipartFile file) {
        try {
            Path path = getPathForFile();
            minioService.upload(path, file.getInputStream(), file.getContentType());
            File toSave = new File();
            toSave.setFileName(path.toString());
            toSave.setHref(generateHrefToFile(path.toString()));
            return fileRepository.save(toSave);
        } catch (MinioException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public File saveFile(byte[] file) {
        try {
            Path path = getPathForFile();
            InputStream targetStream = new ByteArrayInputStream(file);
            minioService.upload(path, targetStream, "pdf");
            File toSave = new File();
            toSave.setFileName(path.toString());
            toSave.setHref(generateHrefToFile(path.toString()));
            return fileRepository.save(toSave);
        } catch (MinioException e) {
            throw new RuntimeException(e);
        }
    }
    @SneakyThrows
    public File deleteFile(UUID fileId) {
        File file = findById(fileId);
        Path path = Path.of(file.getFileName());
        file.setHref("deleted");
        minioService.remove(path);
        return fileRepository.save(file);
    }
    @SneakyThrows
    public void deleteFileWithEntity(UUID fileId) {
        if (fileId == null) return;
        File file = findById(fileId);
        Path path = Path.of(file.getFileName());
        file.setHref("deleted");
        minioService.remove(path);
        fileRepository.deleteById(fileId);
    }

    @SneakyThrows
    public void deleteFile(String fileName) {
        Path path = Path.of(fileName);
        minioService.remove(path);
    }

    public File updateFile(UUID fileId, byte[] file) {
        File fileToUpdate = findById(fileId);
        Path path = getPathForFile();
        try {
            InputStream targetStream = new ByteArrayInputStream(file);
            minioService.upload(path, targetStream, "pdf");
            fileToUpdate.setFileName(path.toString());
            fileToUpdate.setHref(generateHrefToFile(path.toString()));
        } catch (MinioException e) {
            throw new RuntimeException(e);
        }
        return fileRepository.save(fileToUpdate);
    }

    private Path getPathForFile() {
        return Path.of(String.format("%s_document%s", UUID.randomUUID(), ".pdf"));
    }

    private File findById(UUID fileId) {
        return fileRepository.findById(fileId)
                .orElseThrow(() -> ErrorUtils.buildException(ApplicationError.COMMON_ERROR));
    }

    private String generateHrefToFile(String filePath) {
        return String.format("%s/%s/%s", this.minioHost, this.minioBucket, filePath);
    }
}