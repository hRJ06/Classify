package com.Hindol.Classroom.Service.Implementation;

import com.Hindol.Classroom.Entity.Assignment;
import com.Hindol.Classroom.Entity.File;
import com.Hindol.Classroom.Payload.FileAddRemoveDTO;
import com.Hindol.Classroom.Repository.AssignmentRepository;
import com.Hindol.Classroom.Repository.FileRepository;
import com.Hindol.Classroom.Service.FileService;
import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
public class FileServiceImplementation implements FileService {
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private AssignmentRepository assignmentRepository;
    @Autowired
    private Cloudinary cloudinary;
    @Override
    public FileAddRemoveDTO addFile(Integer assignmentId, String role, String email, List<MultipartFile> files) {
        try {
            if(role.equals("STUDENT")) {
                return new FileAddRemoveDTO("You must be an INSTRUCTOR in order to edit Assignment",false);
            }
            Assignment assignment = this.assignmentRepository.findById(assignmentId).orElseThrow(() -> new RuntimeException("Unable To Find Assignment with ID " + assignmentId));
            for(MultipartFile file : files) {
                String fileName = file.getOriginalFilename();
                Map data = this.cloudinary.uploader().upload(file.getBytes(),Map.of());
                String uploadedLink = (String) data.get("secure_url");
                File assignmentFile = new File();
                assignmentFile.setFileName(fileName);
                assignmentFile.setFilePath(uploadedLink);
                File savedFile = this.fileRepository.save(assignmentFile);
                assignment.getFile().add(savedFile);
            }
            this.assignmentRepository.save(assignment);
            return new FileAddRemoveDTO("Successfully Added File(s) To Assignment",true);
        }
        catch (Exception e) {
            return new FileAddRemoveDTO(e.getMessage(),false);
        }
    }

    @Override
    public FileAddRemoveDTO removeFile(Integer fileId, String role) {
        try {
            if(role.equals("STUDENT")) {
                return new FileAddRemoveDTO("You Need To Be An Instructor",true);
            }
            File file = this.fileRepository.findById(fileId).orElseThrow(() -> new RuntimeException("Unable to Fetch File With ID " + fileId));
            this.fileRepository.delete(file);
            return new FileAddRemoveDTO("Successfully Removed File",true);
        }
        catch (Exception e) {
            return new FileAddRemoveDTO(e.getMessage(),false);
        }

    }
}
