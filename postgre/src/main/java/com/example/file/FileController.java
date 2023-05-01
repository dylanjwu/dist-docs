package com.example.file;

import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.api.RemoteFileServer;
import com.example.entity.File;
import com.example.entity.User;
import com.example.repository.FileRepository;
import com.example.repository.UserRepository;
import com.example.user.UserService;

@RestController
@CrossOrigin("*")
public class FileController {


    @Value("${node.server.host}")
    private String nodeHost;

    @Autowired
    public FileRepository fileRepository;
    @Autowired
    public FileService fileService;

    @Autowired
    public UserService userService;

    @Autowired
    public UserRepository userRepository;


    @Autowired
    public RmiClient rmiClient;

    @PostMapping("/file/createFile")
    public ResponseEntity<String> createFile(HttpServletRequest request, @RequestBody FileRequest fileRequest) throws UnknownHostException {
//        String fileName = fileRequest.getFilename();
//        Integer[] userId = fileRequest.getUserid();
        RemoteFileServer remoteFileServer = (RemoteFileServer) request.getAttribute("remoteFileServer");


        // update the metadata database
        try {
            if(!rmiClient.createFile(remoteFileServer , fileRequest)){
                return ResponseEntity.status(500).body("Failed when creating file in File System");
            }
        } catch (RemoteException e) {
            return ResponseEntity.status(500).body("Failed when creating file in File System");
        }

        FileResponse fileResponse = fileService.createFile(fileRequest);


        String fileName = fileRequest.getFilename();
        Long createBy = fileRequest.getCreateBy();
        List<Long> sharedList = fileRequest.getSharedList();
        Notification.notify(fileName, createBy, sharedList, "create");

        return ResponseEntity.status(HttpStatus.CREATED).body(fileResponse.toString());
    }

    @DeleteMapping("/file/deleteFile")
    public ResponseEntity<FileResponse> deleteFile(HttpServletRequest request, @RequestParam Long id) {

        File file = fileRepository.findById(id).orElse(null);

        RemoteFileServer remoteFileServer = (RemoteFileServer) request.getAttribute("remoteFileServer");

        if (file == null){
            return ResponseEntity.status(500).body(new FileResponse(HttpStatus.NOT_FOUND.value(), "File not found"));
        }

        List<Long> userIds = new ArrayList<>();
        for (User user: file.getUsers()) {
            userIds.add(user.getId());
        }

        try {
            if (!rmiClient.deleteFile(remoteFileServer, file.getFileName(), userIds)) {
                return ResponseEntity.status(500).body(new FileResponse(500, "deleting failed"));
            }
        } catch (RemoteException e) {
            return ResponseEntity.status(500).body(new FileResponse(500, "deleting failed"));
        }

        FileResponse fileResponse = fileService.deleteFile(id);


        String filename = file.getFileName();
        List<Long> sharedList = file.getUsers()
            .stream().map((User user) -> user.getId()).collect(Collectors.toList());
        Long userId = file.getCreateBy();

        Notification.notify(filename, userId, sharedList, "delete");

        return ResponseEntity.status(HttpStatus.CREATED).body(fileResponse);
    }

    @GetMapping("/file/readFile")
    public ResponseEntity<String> getFileById(HttpServletRequest request, @RequestParam Long id) throws RemoteException {
        File file = fileRepository.findById(id).orElse(null);

        RemoteFileServer remoteFileServer = (RemoteFileServer) request.getAttribute("remoteFileServer");

        if (file == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<User> users = file.getUsers().stream().toList();

        byte[] contents = this.rmiClient.readFile(remoteFileServer, file.getFileName(), users.get(0).getId());

        return new ResponseEntity<>(new String(contents, StandardCharsets.UTF_8), HttpStatus.OK);
    }



    @GetMapping("/file/getAllFilesInfo")
    public ResponseEntity<List<FileInfo>> getFileAndContents(HttpServletRequest request, @RequestParam Long userId) throws RemoteException {
        User user = userRepository.getReferenceById(userId);
//
//        Set<File> files = user.getFiles();
//
//        List<File> fileList = files.stream().toList();

        RemoteFileServer remoteFileServer = (RemoteFileServer) request.getAttribute("remoteFileServer");
        Map<String, String> files = user.getSharedFiles();

        // System.out.println("FILES": )

        List<FileInfo> result = new ArrayList<>();
        try {
            for (String id : files.keySet() ) {
                String fileName = files.get(id);
                String contents = new String(rmiClient.readFile(remoteFileServer, fileName, userId), StandardCharsets.UTF_8);
                Long fileId = Long.parseLong(id);
                result.add(new FileInfo(fileId, fileName, contents));
            }
        }catch (Exception e){
            System.out.println(e.getMessage()); // TEMP FIX SO I CAN WORK ON FE
            return new ResponseEntity<>(result, HttpStatus.OK);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PutMapping("/file/updateFile")
    public ResponseEntity<FileResponse> updateFile(HttpServletRequest request, @RequestBody FileRequest fileRequest) {

        String contents = fileRequest.getContents();
        RemoteFileServer remoteFileServer = (RemoteFileServer) request.getAttribute("remoteFileServer");

        File file = this.fileRepository.findById(fileRequest.getFileId()).orElse(null);

        if (file == null){
            return ResponseEntity.status(HttpStatus.CREATED).body(new FileResponse(HttpStatus.NOT_FOUND.value(), "" +
                    "File not found"));
        }
        try {
            if (!this.rmiClient.updateFile(remoteFileServer, file, contents)){
                return ResponseEntity.status(HttpStatus.CREATED).body(new FileResponse(500, "Update failed"));
            }
        } catch (RemoteException e) {
            return ResponseEntity.status(HttpStatus.CREATED).body(new FileResponse(500, "Update failed"));
        }
        FileResponse fileResponse = fileService.updateFile(fileRequest);


        String filename = file.getFileName();
        List<Long> sharedList = file.getUsers().stream()
            .map((User user) -> user.getId()).collect(Collectors.toList());
        Long userId = file.getCreateBy();

        Notification.notify(filename, userId, sharedList, "update");

        return ResponseEntity.status(HttpStatus.CREATED).body(fileResponse);
    }
}
