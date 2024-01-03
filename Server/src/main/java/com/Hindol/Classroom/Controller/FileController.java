package com.Hindol.Classroom.Controller;

import com.Hindol.Classroom.Payload.FileAddRemoveDTO;
import com.Hindol.Classroom.Service.FileService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/file")
public class FileController {
    @Autowired
    private FileService fileService;
    @DeleteMapping("/remove/{fileId}")
    public ResponseEntity<?> removeFile(@PathVariable Integer fileId, HttpServletRequest request) {
        String role = (String) request.getAttribute("Role");
        FileAddRemoveDTO fileRemoveDTO = this.fileService.removeFile(fileId,role);
        if(fileRemoveDTO.getSuccess()) {
            return new ResponseEntity<FileAddRemoveDTO>(fileRemoveDTO, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<FileAddRemoveDTO>(fileRemoveDTO,HttpStatus.BAD_REQUEST);
        }
    }
    @PutMapping("/add/assignment/{assignmentId}")
    public ResponseEntity<?> addFileToAssignment(@RequestParam("files") List<MultipartFile> files, @PathVariable Integer assignmentId, HttpServletRequest request) {
        String email = (String) request.getAttribute("email");
        String role = (String) request.getAttribute(("Role"));
        FileAddRemoveDTO fileAddRemoveDTO = this.fileService.addFile(assignmentId,role,email,files);
        if(fileAddRemoveDTO.getResult() != null) {
            return new ResponseEntity<FileAddRemoveDTO>(fileAddRemoveDTO,HttpStatus.OK);
        }
        else {
            return new ResponseEntity<FileAddRemoveDTO>(fileAddRemoveDTO,HttpStatus.BAD_REQUEST);
        }
    }
}
