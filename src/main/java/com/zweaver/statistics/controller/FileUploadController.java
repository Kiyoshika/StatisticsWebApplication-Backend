package com.zweaver.statistics.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.zweaver.statistics.entity.FileStorageEntity;
import com.zweaver.statistics.lib.DataSet;
import com.zweaver.statistics.repository.FileStorageRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/api/v1/")
public class FileUploadController {

    public FileUploadController() {
    }

    @Autowired
    private FileStorageRepository fileStorageRepository;

    // upload CSV files to server and write them to statdb.file_storage
    @PostMapping("/uploadData")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
            @RequestParam("filename") String filename,
            @CurrentSecurityContext(expression = "authentication?.name") String username) {
        try {

            byte[] bytes = file.getBytes();
            Path path = Paths.get(file.getOriginalFilename());
            Files.write(path, bytes);

            List<List<String>> fileContent = new ArrayList<List<String>>();
            List<String> fileHeaders = new ArrayList<String>();
            List<String> fileLines = Files.readAllLines(path);
            boolean firstLine = true;
            for (String currentLine : fileLines) {
                if (firstLine) { fileHeaders = Arrays.asList(currentLine.split("\\s*,\\s*")); firstLine = false; continue; }
                fileContent.add(Arrays.asList(currentLine.split("\\s*,\\s*")));
            }

            DataSet dataSet = new DataSet(fileHeaders, fileContent);

            FileStorageEntity fileStorage = new FileStorageEntity(username, filename, dataSet);
            fileStorageRepository.save(fileStorage);
            return new ResponseEntity<>("File uploaded successfully.", HttpStatus.OK);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>("There was a problem uploading your file.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/viewData")
    public @ResponseBody DataSet viewFile(@RequestParam("filename") String filename,
            @CurrentSecurityContext(expression = "authentication?.name") String username) {
        FileStorageEntity file = fileStorageRepository.findByUsernameAndFilename(username, filename);
        return file.getDataSet();
    }

    @DeleteMapping("/removeData")
    public ResponseEntity<String> removeFile(@RequestParam("filename") String filename,
        @CurrentSecurityContext(expression = "authentication?.name") String username) {
            FileStorageEntity file = fileStorageRepository.findByUsernameAndFilename(username, filename);
            if (file != null) {
                fileStorageRepository.delete(file);
                return new ResponseEntity<>("File was successfully deleted.", HttpStatus.OK);
            }

            return new ResponseEntity<>("Could not find requested file.", HttpStatus.BAD_REQUEST);
        }
}
