package com.example.repository;

import com.example.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

    @Modifying
    @Query(value = "DELETE FROM file_user WHERE file_id = ?1", nativeQuery = true)
    void deleteFileUsers(Long fileId);

    @Modifying
    @Query(value = "DELETE FROM files WHERE file_id = ?1", nativeQuery = true)
    void deleteFile(Long fileId);
}
