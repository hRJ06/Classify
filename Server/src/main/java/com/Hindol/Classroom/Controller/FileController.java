package com.Hindol.Classroom.Controller;

import com.Hindol.Classroom.Payload.FileRemoveDTO;
import com.Hindol.Classroom.Service.FileService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/file")
public class FileController {
    @Autowired
    private FileService fileService;
    @DeleteMapping("/remove/{fileId}")
    public ResponseEntity<?> removeFile(@PathVariable Integer fileId, HttpServletRequest request) {
        String role = (String) request.getAttribute("Role");
        FileRemoveDTO fileRemoveDTO = this.fileService.removeFile(fileId,role);
        if(fileRemoveDTO.getSuccess()) {
            return new ResponseEntity<FileRemoveDTO>(fileRemoveDTO, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<FileRemoveDTO>(fileRemoveDTO,HttpStatus.BAD_REQUEST);
        }
    }
}
