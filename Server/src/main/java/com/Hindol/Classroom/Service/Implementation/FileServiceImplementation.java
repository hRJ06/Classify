package com.Hindol.Classroom.Service.Implementation;

import com.Hindol.Classroom.Entity.File;
import com.Hindol.Classroom.Payload.FileRemoveDTO;
import com.Hindol.Classroom.Repository.FileRepository;
import com.Hindol.Classroom.Service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileServiceImplementation implements FileService {
    @Autowired
    private FileRepository fileRepository;
    @Override
    public FileRemoveDTO removeFile(Integer fileId,String role) {
        try {
            if(role.equals("STUDENT")) {
                return new FileRemoveDTO("You Need To Be An Instructor",true);
            }
            File file = this.fileRepository.findById(fileId).orElseThrow(() -> new RuntimeException("Unable to Fetch File With ID " + fileId));
            this.fileRepository.delete(file);
            return new FileRemoveDTO("Successfully Removed File",true);
        }
        catch (Exception e) {
            return new FileRemoveDTO(e.getMessage(),false);
        }

    }
}
