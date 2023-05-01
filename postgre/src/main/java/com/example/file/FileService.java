package com.example.file;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.entity.File;
import com.example.entity.User;
import com.example.repository.FileRepository;
import com.example.repository.UserRepository;

@Service
public class FileService {
    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public FileResponse createFile(FileRequest fileRequest) {
        String fileName = fileRequest.getFilename();
        Long createBy = fileRequest.getCreateBy();

        List<Long> sharedList = fileRequest.getSharedList();

        File file = new File();
        file.setFileName(fileName);
        file.setCreateBy(createBy);
        file.setCreateAt(LocalDateTime.now());
        file.setModifyAt(LocalDateTime.now());
        fileRepository.save(file);

        for (Long shared: sharedList){
            User user = userRepository.getReferenceById(shared);
            user.getFiles().add(file);
            file.getUsers().add(user);
        }

        FileResponse fileResponse = new FileResponse(HttpStatus.CREATED.value(),
                "Create files successfully", file.getId());

        return fileResponse;
    }

    @Transactional
    public FileResponse deleteFile(Long id) {


        fileRepository.deleteFileUsers(id);
        fileRepository.deleteFile(id);

        return new FileResponse(HttpStatus.CREATED.value(), "delete files successfully");

    }
//
    @Transactional
    public FileResponse updateFile(FileRequest fileRequest) {


        Long fileId = fileRequest.getFileId();

        File file = fileRepository.findById(fileId).orElse(null);

        if (file == null){
            return new FileResponse(HttpStatus.NOT_FOUND.value(), "Files not found");
        }

        file.setModifyAt(LocalDateTime.now());


        fileRepository.save(file);

        return new FileResponse(HttpStatus.CREATED.value(), "update files successfully");


    }



}
