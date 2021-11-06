package com.zweaver.statistics.repository;

import java.util.List;

import javax.transaction.Transactional;

import com.zweaver.statistics.entity.FileStorageEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface FileStorageRepository extends JpaRepository<FileStorageEntity, Long> {
    FileStorageEntity findByUsernameAndFilename(String username, String filename);
}
