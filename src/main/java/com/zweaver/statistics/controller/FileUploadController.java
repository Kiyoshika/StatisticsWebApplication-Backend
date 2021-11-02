package com.zweaver.statistics.controller;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.zweaver.statistics.utility.ListFilterPredicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
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

    public FileUploadController() {}

    @PostMapping("/uploadFile")
    public void uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(file.getOriginalFilename());
            Files.write(path, bytes);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/viewFile/{filename}")
    @ResponseBody
    public void viewFile(@PathVariable String filename) {
        try {
            Path path = Paths.get(filename);
            List<List<String>> fileContents = new ArrayList<List<String>>();
            List<String> fileLines = Files.readAllLines(path);
            for (String currentLine : fileLines) {
                fileContents.add(Arrays.asList(currentLine.split("\\s*,\\s*")));
            }

            System.out.println(fileContents.get(0).get(0));

            List<Integer> indices = new ArrayList<Integer>(){{ add(0); add(2); }};
            List<String> values = new ArrayList<String>(){{ add("a"); add("f"); }};
            List<String> conds = new ArrayList<String>(){{ add("or"); }};

            ListFilterPredicate compositePredicate = new ListFilterPredicate(indices.get(0), values.get(0));
            Predicate<List<String>> compPredicate = compositePredicate;
            if (conds.size() > 0) {
                for (int i = 0; i < conds.size(); ++i) {
                    if (conds.get(i).equals("and")) {
                        compPredicate = compPredicate.and(new ListFilterPredicate(indices.get(i+1), values.get(i+1)));
                    }
                    else if (conds.get(i).equals("or")) {
                        compPredicate = compPredicate.or(new ListFilterPredicate(indices.get(i+1), values.get(i+1)));
                    }
                }
            }

            List<List<String>> resultSet = fileContents.stream()
                .filter(compPredicate)
                .collect(Collectors.toList());

            for (List<String> rows : resultSet) {
                for (String col : rows) {
                    System.out.print(col + " ");
                }
                System.out.println("");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
