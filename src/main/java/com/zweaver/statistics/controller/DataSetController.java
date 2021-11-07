package com.zweaver.statistics.controller;

import com.zweaver.statistics.entity.DataSetEntity;
import com.zweaver.statistics.entity.FileStorageEntity;
import com.zweaver.statistics.lib.DataSet;
import com.zweaver.statistics.repository.FileStorageRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api/v1/")
public class DataSetController {

    public DataSetController() {}

    @Autowired
    private FileStorageRepository fileStorageRepository;

    @GetMapping("/filterData")
    public @ResponseBody Object filterData(@RequestParam("filename") String filename,
            @CurrentSecurityContext(expression = "authentication?.name") String username,
            @RequestBody DataSetEntity dataFilterEntity) {
        // get data set from repository
        FileStorageEntity file = fileStorageRepository.findByUsernameAndFilename(username, filename);
        if (file == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        DataSet dataSet = file.getDataSet();
        return new DataSet(dataSet.getHeaders(), dataSet.filter(dataFilterEntity));
    }
}
