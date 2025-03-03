package vn.hoidanit.jobhunter.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import vn.hoidanit.jobhunter.domain.response.file.ResUpLoadFileDTO;
import vn.hoidanit.jobhunter.service.FileService;
import vn.hoidanit.jobhunter.util.error.StorageException;

@RestController
@RequestMapping("/api/v1/files")
public class FileController {

    @Value("${hoidanit.upload-file.base-uri}")
    private String baseUri;
    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping()
    public ResponseEntity<ResUpLoadFileDTO> uploadFile(
            @RequestParam(name = "file", required = false) MultipartFile file,
            @RequestParam("folder") String folder) throws URISyntaxException, IOException, StorageException {
        // validate
        if (file == null || file.isEmpty()) {
            throw new StorageException("File upload is empty !");
        }
        String fileName = file.getOriginalFilename();
        List<String> allowedExtensions = Arrays.asList("pdf", "jpg", "jpeg", "png", "doc", "docx");
        boolean allowed = allowedExtensions.stream().anyMatch(item -> fileName.toLowerCase().endsWith(item));
        if (!allowed) {
            throw new StorageException("Invalid file name extension , Only allows :" + allowedExtensions.toString());
        }

        // create directory
        this.fileService.createUploadFolder(baseUri + folder);

        // store file
        String fileName1 = this.fileService.store(file, folder);
        return ResponseEntity.ok(new ResUpLoadFileDTO(fileName1, Instant.now()));
    }
}
