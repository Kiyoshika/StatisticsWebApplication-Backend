package com.zweaver.statistics.entity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.zweaver.statistics.lib.DataSet;

import org.hibernate.Hibernate;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

@Entity
@EnableAutoConfiguration
@Table(name = "file_storage")
public class FileStorageEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "filename", unique = true, nullable = false)
    private String filename;

    @Column(name = "file_content", nullable = false)
    @Lob
    private byte[] fileContent;

    public FileStorageEntity() {}

    public FileStorageEntity(String username, String filename, DataSet fileContent) {
        this.username = username;
        this.filename = filename;
        
        // convert data set object into blob
        try {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream objStream = new ObjectOutputStream(baos);
            objStream.writeObject(fileContent);
            objStream.flush();
            objStream.close();
            baos.close();

            this.fileContent = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String getUsername() {
        return this.username;
    }

    public String getFilename() {
        return this.filename;
    }

    public DataSet getDataSet() {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(this.fileContent);
            ObjectInputStream ois = new ObjectInputStream(bis);
            return (DataSet)ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException cnf) {
            cnf.printStackTrace();
        }

        return null;
    }

    public void setDataSet(DataSet dataSet) {
        // convert data set object into blob
        try {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream objStream = new ObjectOutputStream(baos);
            objStream.writeObject(dataSet);
            objStream.flush();
            objStream.close();
            baos.close();

            this.fileContent = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
