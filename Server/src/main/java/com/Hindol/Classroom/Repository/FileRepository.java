package com.Hindol.Classroom.Repository;

import com.Hindol.Classroom.Entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File,Integer> {
}
