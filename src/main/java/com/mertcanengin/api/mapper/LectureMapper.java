package com.mertcanengin.api.mapper;

import com.mertcanengin.api.dto.LectureRequest;
import com.mertcanengin.api.dto.LectureResponse;
import com.mertcanengin.api.entity.Lecture;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LectureMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "enrollments", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Lecture toEntity(LectureRequest request);

    @AfterMapping
    default void mapTeacherId(LectureRequest request, @MappingTarget Lecture lecture) {
        lecture.setTeacherId(request.teacherId());
    }

    @Mapping(target = "teacherId", expression = "java(lecture.getTeacherId())")
    LectureResponse toResponse(Lecture lecture);

    List<LectureResponse> toResponseList(List<Lecture> lectures);
}
