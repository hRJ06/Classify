package com.Hindol.Classroom.Service;

import com.Hindol.Classroom.Payload.FileRemoveDTO;

public interface FileService {
    FileRemoveDTO removeFile(Integer fileId,String role);
}
