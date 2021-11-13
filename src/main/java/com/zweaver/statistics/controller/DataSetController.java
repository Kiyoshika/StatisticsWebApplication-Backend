package com.zweaver.statistics.controller;

import java.util.List;

import com.zweaver.statistics.entity.DataSetEntity;
import com.zweaver.statistics.entity.FileStorageEntity;
import com.zweaver.statistics.lib.DataSet;
import com.zweaver.statistics.msg.Messages;
import com.zweaver.statistics.repository.FileStorageRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api/v1/")
public class DataSetController {

    public DataSetController() {}

    private DataSet fetchData(String username, String filename) {
        // get data set from repository
        FileStorageEntity file = fileStorageRepository.findByUsernameAndFilename(username, filename);
        return file == null ? null : file.getDataSet();
    }

    @Autowired
    private FileStorageRepository fileStorageRepository;

    @GetMapping("/filterData")
    public @ResponseBody Object filterData(@RequestParam("filename") String filename,
            @CurrentSecurityContext(expression = "authentication?.name") String username,
            @RequestBody DataSetEntity dataFilterEntity) {
            
            DataSet dataSet = fetchData(username, filename);
            if (dataSet == null) { return new ResponseEntity<>(Messages.couldNotFindRequestedFile, HttpStatus.BAD_REQUEST); }
            return dataSet.filter(dataFilterEntity);
    }

    @GetMapping("/selectData")
    public @ResponseBody Object selectData(@RequestParam("filename") String filename,
        @RequestBody List<String> desiredColumns,
        @CurrentSecurityContext(expression = "authentication?.name") String username) {
            
            DataSet dataSet = fetchData(username, filename);
            if (dataSet == null) { return new ResponseEntity<>(Messages.couldNotFindRequestedFile, HttpStatus.BAD_REQUEST); }
            return dataSet.select(desiredColumns);
        }

    @GetMapping("/dropData")
    public @ResponseBody Object dropData(@RequestParam("filename") String filename,
        @RequestBody List<String> dropColumns,
        @CurrentSecurityContext(expression = "authentication?.name") String username) {
            
            DataSet dataSet = fetchData(username, filename);
            if (dataSet == null) { return new ResponseEntity<>(Messages.couldNotFindRequestedFile, HttpStatus.BAD_REQUEST); }
            return dataSet.drop(dropColumns);
        }

    @PostMapping("/editCell")
    public ResponseEntity<String> editCell(@RequestParam("filename") String filename,
        @RequestParam("value") String value,
        @RequestBody List<Integer> cellIndices,
        @CurrentSecurityContext(expression = "authentication?.name") String username) {
            FileStorageEntity file = fileStorageRepository.findByUsernameAndFilename(username, filename);
            DataSet dataSet = file.getDataSet();
            dataSet.setCell(cellIndices, value);
            file.setDataSet(dataSet);
            fileStorageRepository.save(file);
            return new ResponseEntity<>("Successfully modified cell value.", HttpStatus.OK);
        }

    @PostMapping("/editHeader")
    public ResponseEntity<String> editHeader(@RequestParam("filename") String filename,
        @RequestParam("value") String value,
        @RequestBody int headerIndex,
        @CurrentSecurityContext(expression = "authentication?.name") String username) {
            FileStorageEntity file = fileStorageRepository.findByUsernameAndFilename(username, filename);
            if (file == null) { return new ResponseEntity<>(Messages.couldNotFindRequestedFile, HttpStatus.BAD_REQUEST); }
            DataSet dataSet = file.getDataSet();
            dataSet.setHeader(headerIndex, value);
            file.setDataSet(dataSet);
            fileStorageRepository.save(file);
            return new ResponseEntity<>("Successfully modified header value.", HttpStatus.OK);
        }

    @PostMapping("/overwriteData")
    public ResponseEntity<String> overwriteData(@RequestParam("filename") String filename,
    @RequestBody DataSet newDataSet,
    @CurrentSecurityContext(expression = "authentication?.name") String username) {
        FileStorageEntity file = fileStorageRepository.findByUsernameAndFilename(username, filename);
        if (file == null) { return new ResponseEntity<>(Messages.couldNotFindRequestedFile, HttpStatus.BAD_REQUEST); }
        file.setDataSet(newDataSet);
        fileStorageRepository.save(file);
        return new ResponseEntity<>("Succesffully overwrote file.", HttpStatus.OK);
    }
}
