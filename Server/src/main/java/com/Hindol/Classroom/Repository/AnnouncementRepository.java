package com.Hindol.Classroom.Repository;

import com.Hindol.Classroom.Entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnouncementRepository extends JpaRepository<Announcement,Integer> {
}
