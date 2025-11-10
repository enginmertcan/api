package com.mertcanengin.api.domain.user.policy;

import com.mertcanengin.api.common.GeneralException;
import com.mertcanengin.api.entity.User;
import com.mertcanengin.api.repository.IEnrollmentRepository;
import com.mertcanengin.api.repository.ILectureRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DefaultUserDeletionGuard implements UserDeletionGuard {

    private final ILectureRepository lectureRepository;
    private final IEnrollmentRepository enrollmentRepository;

    public DefaultUserDeletionGuard(ILectureRepository lectureRepository,
                                    IEnrollmentRepository enrollmentRepository) {
        this.lectureRepository = lectureRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Override
    public void ensureCanDelete(User user) {
        boolean hasLectures = lectureRepository.existsByTeacher_Id(user.getId());
        boolean hasEnrollments = enrollmentRepository.existsByStudent_Id(user.getId());

        if (hasLectures || hasEnrollments) {
            List<String> reasons = new ArrayList<>();
            if (hasLectures) {
                reasons.add("they are assigned as a teacher to existing lectures");
            }
            if (hasEnrollments) {
                reasons.add("they are enrolled in existing lectures");
            }
            throw new GeneralException("User cannot be deleted because " +
                    String.join(" and ", reasons) +
                    ". Remove the related records first.");
        }
    }
}
