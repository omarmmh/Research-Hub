package com.webapp.researchhub.repository;

import com.webapp.researchhub.domain.MyUser;
import com.webapp.researchhub.domain.UserFile;
import org.springframework.data.repository.CrudRepository;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public interface FileRepository extends CrudRepository<UserFile, Long> {
    List<UserFile> findTop6ByDateUploadedAfterOrderByDateUploadedAsc(Date date);
    ArrayList<UserFile> findTop3ByUserAndDateUploadedBeforeOrderByDateUploadedDesc(MyUser user, Date date);
    ArrayList<UserFile> findAllByTitleContains(String name);

}
