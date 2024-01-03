package com.Hindol.Classroom.Service;

import com.Hindol.Classroom.Payload.FileAddRemoveDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {
    FileAddRemoveDTO removeFile(Integer fileId, String role);
    FileAddRemoveDTO addFile(Integer assignmentId, String role, String email, List<MultipartFile> files);
}
