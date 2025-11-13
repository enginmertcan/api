package com.mertcanengin.api.bootstrap;

import com.mertcanengin.api.dto.bootstrap.SampleAccount;
import com.mertcanengin.api.dto.bootstrap.SampleDataStatusResponse;
import com.mertcanengin.api.entity.Classroom;
import com.mertcanengin.api.entity.Enrollment;
import com.mertcanengin.api.entity.GradeComponent;
import com.mertcanengin.api.entity.Lecture;
import com.mertcanengin.api.entity.LectureSchedule;
import com.mertcanengin.api.entity.ScheduleSlot;
import com.mertcanengin.api.entity.User;
import com.mertcanengin.api.entity.enums.EnrollmentStatus;
import com.mertcanengin.api.entity.enums.Gender;
import com.mertcanengin.api.entity.enums.Role;
import com.mertcanengin.api.repository.IClassroomRepository;
import com.mertcanengin.api.repository.IEnrollmentGradeRepository;
import com.mertcanengin.api.repository.IEnrollmentRepository;
import com.mertcanengin.api.repository.IGradeComponentRepository;
import com.mertcanengin.api.repository.ILectureRepository;
import com.mertcanengin.api.repository.ILectureScheduleRepository;
import com.mertcanengin.api.repository.IRefreshTokenRepository;
import com.mertcanengin.api.repository.IScheduleSlotRepository;
import com.mertcanengin.api.repository.IUserRepository;
import com.mertcanengin.api.service.IEnrollmentGradeService;
import com.mertcanengin.api.service.IEnrollmentService;
import com.mertcanengin.api.service.IGradeComponentService;
import com.mertcanengin.api.service.IClassroomService;
import com.mertcanengin.api.service.ILectureScheduleService;
import com.mertcanengin.api.service.ILectureService;
import com.mertcanengin.api.service.IScheduleSlotService;
import com.mertcanengin.api.service.IUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SampleDataService {

    private static final String DEMO_PASSWORD = "Trend123!";

    private static final UserSeed ADMIN_SEED = new UserSeed("90000000001", "Deniz", "Kaya", Gender.MALE);

    private static final List<UserSeed> TEACHER_SEEDS = List.of(
            new UserSeed("80000000001", "Ayse", "Kaplan", Gender.FEMALE),
            new UserSeed("80000000002", "Mehmet", "Arslan", Gender.MALE),
            new UserSeed("80000000003", "Selin", "Demir", Gender.FEMALE),
            new UserSeed("80000000004", "Tolga", "Sener", Gender.MALE),
            new UserSeed("80000000005", "Banu", "Ergul", Gender.FEMALE),
            new UserSeed("80000000006", "Hakan", "Kurt", Gender.MALE)
    );

    private static final List<UserSeed> STUDENT_SEEDS = List.of(
            new UserSeed("70000000001", "Efe", "Yildiz", Gender.MALE),
            new UserSeed("70000000002", "Zeynep", "Korkmaz", Gender.FEMALE),
            new UserSeed("70000000003", "Kerem", "Aksoy", Gender.MALE),
            new UserSeed("70000000004", "Mert", "Celik", Gender.MALE),
            new UserSeed("70000000005", "Derya", "Baran", Gender.FEMALE),
            new UserSeed("70000000006", "Burak", "Alp", Gender.MALE),
            new UserSeed("70000000007", "Elif", "Uzun", Gender.FEMALE),
            new UserSeed("70000000008", "Arda", "Kaya", Gender.MALE),
            new UserSeed("70000000009", "Eda", "Can", Gender.FEMALE),
            new UserSeed("70000000010", "Umut", "Ergin", Gender.MALE),
            new UserSeed("70000000011", "Nil", "Sahin", Gender.FEMALE),
            new UserSeed("70000000012", "Baris", "Guler", Gender.MALE),
            new UserSeed("70000000013", "Sena", "Oz", Gender.FEMALE),
            new UserSeed("70000000014", "Can", "Bora", Gender.MALE),
            new UserSeed("70000000015", "Ipek", "Esen", Gender.FEMALE),
            new UserSeed("70000000016", "Tuna", "Demirel", Gender.MALE),
            new UserSeed("70000000017", "Melis", "Aslan", Gender.FEMALE),
            new UserSeed("70000000018", "Gokhan", "Kar", Gender.MALE),
            new UserSeed("70000000019", "Azra", "Bilgin", Gender.FEMALE),
            new UserSeed("70000000020", "Kaan", "Tamer", Gender.MALE)
    );

    private static final List<ClassroomSeed> CLASSROOM_SEEDS = List.of(
            new ClassroomSeed("Orion Lab", "B1 Katı", 34),
            new ClassroomSeed("Atlas Hub", "A Blok 2. Kat", 28),
            new ClassroomSeed("Nova Studio", "C Blok", 24),
            new ClassroomSeed("Vega Hall", "Konferans Alanı", 60),
            new ClassroomSeed("Helix Studio", "D Blok", 26),
            new ClassroomSeed("Vertex Lab", "Innovation Center", 32)
    );

    private static final List<SlotSeed> SLOT_SEEDS = List.of(
            new SlotSeed(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(11, 0)),
            new SlotSeed(DayOfWeek.TUESDAY, LocalTime.of(11, 0), LocalTime.of(13, 0)),
            new SlotSeed(DayOfWeek.WEDNESDAY, LocalTime.of(13, 0), LocalTime.of(15, 0)),
            new SlotSeed(DayOfWeek.THURSDAY, LocalTime.of(10, 0), LocalTime.of(12, 0)),
            new SlotSeed(DayOfWeek.FRIDAY, LocalTime.of(14, 0), LocalTime.of(16, 0)),
            new SlotSeed(DayOfWeek.SATURDAY, LocalTime.of(9, 0), LocalTime.of(12, 0)),
            new SlotSeed(DayOfWeek.MONDAY, LocalTime.of(16, 0), LocalTime.of(18, 0)),
            new SlotSeed(DayOfWeek.TUESDAY, LocalTime.of(14, 0), LocalTime.of(16, 0))
    );

    private static final List<LectureSeed> LECTURE_SEEDS = List.of(
            new LectureSeed(
                    "Distributed Systems",
                    "Event-driven mikroservisler ve mesaj kuyruklarıyla ölçeklenebilir sistem tasarımı.",
                    4,
                    "80000000001",
                    List.of(
                            new GradeComponentSeed("Project", 40, 100),
                            new GradeComponentSeed("Midterm", 25, 100),
                            new GradeComponentSeed("Final", 35, 100)
                    )
            ),
            new LectureSeed(
                    "Data Mining",
                    "Öznitelik mühendisliği, kümeleme ve ensemble yöntemleriyle uygulamalı veri madenciliği.",
                    3,
                    "80000000002",
                    List.of(
                            new GradeComponentSeed("Assignments", 30, 100),
                            new GradeComponentSeed("Midterm", 30, 100),
                            new GradeComponentSeed("Final", 40, 100)
                    )
            ),
            new LectureSeed(
                    "Cloud Architecture",
                    "Kubernetes, IaC ve gözlemlenebilirlik araçları ile bulut yerlisi mimari prensipleri.",
                    5,
                    "80000000003",
                    List.of(
                            new GradeComponentSeed("Labs", 35, 100),
                            new GradeComponentSeed("Midterm", 25, 100),
                            new GradeComponentSeed("Final", 40, 100)
                    )
            ),
            new LectureSeed(
                    "Mobile Development",
                    "Flutter ile çapraz platform mimariler, state management ve performans optimizasyonu.",
                    4,
                    "80000000001",
                    List.of(
                            new GradeComponentSeed("Project", 45, 100),
                            new GradeComponentSeed("Midterm", 20, 100),
                            new GradeComponentSeed("Final", 35, 100)
                    )
            ),
            new LectureSeed(
                    "DevOps Automation",
                    "CI/CD orkestrasyonu, pipeline güvenliği ve altyapı otomasyonu pratikleri.",
                    3,
                    "80000000004",
                    List.of(
                            new GradeComponentSeed("Labs", 30, 100),
                            new GradeComponentSeed("Midterm", 30, 100),
                            new GradeComponentSeed("Final", 40, 100)
                    )
            ),
            new LectureSeed(
                    "UI/UX Research",
                    "Kullanıcı araştırması, kullanılabilirlik testleri ve Figma üzerinden prototipleme.",
                    4,
                    "80000000005",
                    List.of(
                            new GradeComponentSeed("Case Study", 35, 100),
                            new GradeComponentSeed("Midterm", 25, 100),
                            new GradeComponentSeed("Final", 40, 100)
                    )
            ),
            new LectureSeed(
                    "Cyber Security",
                    "Sızma testleri, OWASP Top 10 ve Zero Trust prensipleriyle savunma stratejileri.",
                    5,
                    "80000000006",
                    List.of(
                            new GradeComponentSeed("Lab Exercises", 30, 100),
                            new GradeComponentSeed("Midterm", 30, 100),
                            new GradeComponentSeed("Final", 40, 100)
                    )
            ),
            new LectureSeed(
                    "Data Visualization",
                    "D3.js ve Apache Superset ile veri hikayeleştirme ve dashboard tasarımı.",
                    4,
                    "80000000003",
                    List.of(
                            new GradeComponentSeed("Project", 40, 100),
                            new GradeComponentSeed("Midterm", 20, 100),
                            new GradeComponentSeed("Final", 40, 100)
                    )
            )
    );

    private static final List<LectureScheduleSeed> LECTURE_SCHEDULE_SEEDS = List.of(
            new LectureScheduleSeed("Distributed Systems", "Orion Lab", 0, -14, 70),
            new LectureScheduleSeed("Distributed Systems", "Orion Lab", 3, -14, 70),
            new LectureScheduleSeed("Data Mining", "Atlas Hub", 1, -7, 60),
            new LectureScheduleSeed("Cloud Architecture", "Vega Hall", 5, -21, 84),
            new LectureScheduleSeed("Cloud Architecture", "Helix Studio", 2, -21, 84),
            new LectureScheduleSeed("Mobile Development", "Nova Studio", 4, -10, 65),
            new LectureScheduleSeed("DevOps Automation", "Helix Studio", 6, -12, 80),
            new LectureScheduleSeed("DevOps Automation", "Vertex Lab", 7, -12, 80),
            new LectureScheduleSeed("UI/UX Research", "Nova Studio", 3, -8, 75),
            new LectureScheduleSeed("UI/UX Research", "Helix Studio", 1, -8, 75),
            new LectureScheduleSeed("Cyber Security", "Vertex Lab", 2, -18, 90),
            new LectureScheduleSeed("Cyber Security", "Helix Studio", 0, -18, 90),
            new LectureScheduleSeed("Data Visualization", "Orion Lab", 7, -6, 60),
            new LectureScheduleSeed("Data Visualization", "Vertex Lab", 6, -6, 60)
    );

    private static final List<SampleAccount> SAMPLE_ACCOUNTS = List.of(
            new SampleAccount("ADMIN", ADMIN_SEED.identityNo(), DEMO_PASSWORD, "Tüm panellere erişim"),
            new SampleAccount("TEACHER", TEACHER_SEEDS.get(0).identityNo(), DEMO_PASSWORD, "Ders/grade yönetimi"),
            new SampleAccount("TEACHER", TEACHER_SEEDS.get(1).identityNo(), DEMO_PASSWORD, "Analytics ve kayıt onayı"),
            new SampleAccount("TEACHER", TEACHER_SEEDS.get(3).identityNo(), DEMO_PASSWORD, "DevOps & altyapı dersleri"),
            new SampleAccount("STUDENT", STUDENT_SEEDS.get(0).identityNo(), DEMO_PASSWORD, "Kayıt ve transcript testleri"),
            new SampleAccount("STUDENT", STUDENT_SEEDS.get(1).identityNo(), DEMO_PASSWORD, "Waitlist senaryosu"),
            new SampleAccount("STUDENT", STUDENT_SEEDS.get(4).identityNo(), DEMO_PASSWORD, "Notlandırma senaryosu"),
            new SampleAccount("STUDENT", STUDENT_SEEDS.get(10).identityNo(), DEMO_PASSWORD, "Kalabalık sınıf testi")
    );

    private final IUserService userService;
    private final ILectureService lectureService;
    private final IClassroomService classroomService;
    private final IScheduleSlotService scheduleSlotService;
    private final ILectureScheduleService lectureScheduleService;
    private final IGradeComponentService gradeComponentService;
    private final IEnrollmentService enrollmentService;
    private final IEnrollmentGradeService enrollmentGradeService;

    private final IUserRepository userRepository;
    private final ILectureRepository lectureRepository;
    private final IClassroomRepository classroomRepository;
    private final IScheduleSlotRepository scheduleSlotRepository;
    private final ILectureScheduleRepository lectureScheduleRepository;
    private final IGradeComponentRepository gradeComponentRepository;
    private final IEnrollmentRepository enrollmentRepository;
    private final IEnrollmentGradeRepository enrollmentGradeRepository;
    private final IRefreshTokenRepository refreshTokenRepository;

    public SampleDataService(IUserService userService,
                             ILectureService lectureService,
                             IClassroomService classroomService,
                             IScheduleSlotService scheduleSlotService,
                             ILectureScheduleService lectureScheduleService,
                             IGradeComponentService gradeComponentService,
                             IEnrollmentService enrollmentService,
                             IEnrollmentGradeService enrollmentGradeService,
                             IUserRepository userRepository,
                             ILectureRepository lectureRepository,
                             IClassroomRepository classroomRepository,
                             IScheduleSlotRepository scheduleSlotRepository,
                             ILectureScheduleRepository lectureScheduleRepository,
                             IGradeComponentRepository gradeComponentRepository,
                             IEnrollmentRepository enrollmentRepository,
                             IEnrollmentGradeRepository enrollmentGradeRepository,
                             IRefreshTokenRepository refreshTokenRepository) {
        this.userService = userService;
        this.lectureService = lectureService;
        this.classroomService = classroomService;
        this.scheduleSlotService = scheduleSlotService;
        this.lectureScheduleService = lectureScheduleService;
        this.gradeComponentService = gradeComponentService;
        this.enrollmentService = enrollmentService;
        this.enrollmentGradeService = enrollmentGradeService;
        this.userRepository = userRepository;
        this.lectureRepository = lectureRepository;
        this.classroomRepository = classroomRepository;
        this.scheduleSlotRepository = scheduleSlotRepository;
        this.lectureScheduleRepository = lectureScheduleRepository;
        this.gradeComponentRepository = gradeComponentRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.enrollmentGradeRepository = enrollmentGradeRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public SampleDataStatusResponse bootstrap(boolean force) {
        long lectureCount = lectureRepository.count();
        long userCount = userRepository.count();
        if (!force && (lectureCount > 0 || userCount > 0)) {
            return snapshot("Veri mevcut olduğundan bootstrap atlandı. Force=true ile sıfırlayabilirsiniz.");
        }
        purgeAll();
        populate();
        return snapshot(force ? "Örnek veri seti force=true ile yeniden oluşturuldu." : "Örnek veri seti oluşturuldu.");
    }

    public SampleDataStatusResponse status() {
        return snapshot("Anlık durum");
    }

    private SampleDataStatusResponse snapshot(String note) {
        long adminCount = userRepository.countByRole(Role.ADMIN);
        long teacherCount = userRepository.countByRole(Role.TEACHER);
        long studentCount = userRepository.countByRole(Role.STUDENT);
        long lectureCount = lectureRepository.count();
        long classroomCount = classroomRepository.count();
        long slotCount = scheduleSlotRepository.count();
        long scheduleCount = lectureScheduleRepository.count();
        long componentCount = gradeComponentRepository.count();
        long enrollmentCount = enrollmentRepository.count();
        long pending = enrollmentRepository.countByStatus(EnrollmentStatus.PENDING_APPROVAL);
        long active = enrollmentRepository.countByStatus(EnrollmentStatus.ACTIVE);
        long waiting = enrollmentRepository.countByStatus(EnrollmentStatus.WAITING);
        long completed = enrollmentRepository.countByStatus(EnrollmentStatus.COMPLETED);

        return new SampleDataStatusResponse(
                lectureCount > 0,
                note,
                adminCount,
                teacherCount,
                studentCount,
                lectureCount,
                classroomCount,
                slotCount,
                scheduleCount,
                componentCount,
                enrollmentCount,
                pending,
                active,
                waiting,
                completed,
                SAMPLE_ACCOUNTS
        );
    }

    private void populate() {
        seedAdmin();
        Map<String, User> teachers = seedTeachers();
        Map<String, User> students = seedStudents();
        List<Classroom> classrooms = seedClassrooms();
        List<ScheduleSlot> slots = seedSlots();
        Map<String, Lecture> lectures = seedLectures(teachers);
        Map<Integer, List<GradeComponent>> components = seedGradeComponents(lectures);
        seedSchedules(lectures, classrooms, slots);
        seedEnrollments(lectures, students, components);
    }

    private void seedAdmin() {
        createUser(ADMIN_SEED, Role.ADMIN);
    }

    private Map<String, User> seedTeachers() {
        Map<String, User> teachers = new LinkedHashMap<>();
        for (UserSeed seed : TEACHER_SEEDS) {
            User saved = createUser(seed, Role.TEACHER);
            teachers.put(seed.identityNo(), saved);
        }
        return teachers;
    }

    private Map<String, User> seedStudents() {
        Map<String, User> students = new LinkedHashMap<>();
        for (UserSeed seed : STUDENT_SEEDS) {
            User saved = createUser(seed, Role.STUDENT);
            students.put(seed.identityNo(), saved);
        }
        return students;
    }

    private User createUser(UserSeed seed, Role role) {
        User user = new User();
        user.setIdentityNo(seed.identityNo());
        user.setName(seed.name());
        user.setSurname(seed.surname());
        user.setEmail(seed.identityNo() + "@demo.lecture-portal.local");
        user.setGender(seed.gender());
        user.setRole(role);
        user.setPassword(DEMO_PASSWORD);
        user.setEmailVerified(true);
        return userService.save(user);
    }

    private List<Classroom> seedClassrooms() {
        List<Classroom> classrooms = new ArrayList<>();
        for (ClassroomSeed seed : CLASSROOM_SEEDS) {
            Classroom classroom = new Classroom();
            classroom.setName(seed.name());
            classroom.setLocation(seed.location());
            classroom.setCapacity(seed.capacity());
            classrooms.add(classroomService.save(classroom));
        }
        return classrooms;
    }

    private List<ScheduleSlot> seedSlots() {
        List<ScheduleSlot> slots = new ArrayList<>();
        for (SlotSeed seed : SLOT_SEEDS) {
            ScheduleSlot slot = new ScheduleSlot();
            slot.setDayOfWeek(seed.day());
            slot.setStartTime(seed.start());
            slot.setEndTime(seed.end());
            slots.add(scheduleSlotService.save(slot));
        }
        return slots;
    }

    private Map<String, Lecture> seedLectures(Map<String, User> teachers) {
        Map<String, Lecture> lectures = new LinkedHashMap<>();
        for (LectureSeed seed : LECTURE_SEEDS) {
            User teacher = teachers.get(seed.teacherIdentity());
            if (teacher == null) {
                continue;
            }
            Lecture lecture = new Lecture();
            lecture.setName(seed.name());
            lecture.setDescription(seed.description());
            lecture.setCapacity(seed.capacity());
            lecture.setTeacherId(teacher.getId());
            Lecture saved = lectureService.save(lecture);
            lectures.put(seed.name(), saved);
        }
        return lectures;
    }

    private Map<Integer, List<GradeComponent>> seedGradeComponents(Map<String, Lecture> lectures) {
        Map<Integer, List<GradeComponent>> result = new LinkedHashMap<>();
        for (LectureSeed seed : LECTURE_SEEDS) {
            Lecture lecture = lectures.get(seed.name());
            if (lecture == null) {
                continue;
            }
            List<GradeComponent> components = new ArrayList<>();
            for (GradeComponentSeed componentSeed : seed.components()) {
                GradeComponent component = new GradeComponent();
                component.setLecture(lecture);
                component.setName(componentSeed.name());
                component.setWeight(BigDecimal.valueOf(componentSeed.weight()).setScale(2, RoundingMode.HALF_UP));
                component.setMaxScore(BigDecimal.valueOf(componentSeed.maxScore()).setScale(2, RoundingMode.HALF_UP));
                components.add(gradeComponentService.save(component));
            }
            result.put(lecture.getId(), components);
        }
        return result;
    }

    private void seedSchedules(Map<String, Lecture> lectures,
                               List<Classroom> classrooms,
                               List<ScheduleSlot> slots) {
        Map<String, Classroom> classroomMap = classrooms.stream()
                .collect(Collectors.toMap(
                        classroom -> classroom.getName().toLowerCase(Locale.ROOT),
                        Function.identity()));
        for (LectureScheduleSeed seed : LECTURE_SCHEDULE_SEEDS) {
            Lecture lecture = lectures.get(seed.lectureName());
            Classroom classroom = classroomMap.get(seed.classroomName().toLowerCase(Locale.ROOT));
            ScheduleSlot slot = (seed.slotIndex() >= 0 && seed.slotIndex() < slots.size())
                    ? slots.get(seed.slotIndex())
                    : null;
            if (lecture == null || classroom == null || slot == null) {
                continue;
            }
            LectureSchedule schedule = new LectureSchedule();
            schedule.setLecture(lecture);
            schedule.setClassroom(classroom);
            schedule.setScheduleSlot(slot);
            schedule.setStartDate(LocalDate.now().plusDays(seed.startOffsetDays()));
            schedule.setEndDate(LocalDate.now().plusDays(seed.endOffsetDays()));
            lectureScheduleService.save(schedule);
        }
    }

    private void seedEnrollments(Map<String, Lecture> lectures,
                                 Map<String, User> students,
                                 Map<Integer, List<GradeComponent>> components) {
        if (students.isEmpty() || lectures.isEmpty()) {
            return;
        }
        List<User> studentPool = new ArrayList<>(students.values());
        studentPool.sort(Comparator.comparing(User::getIdentityNo));
        int offset = 0;
        for (LectureSeed seed : LECTURE_SEEDS) {
            Lecture lecture = lectures.get(seed.name());
            if (lecture == null) {
                continue;
            }
            int totalEnrollments = Math.min(studentPool.size(), lecture.getCapacity() + 3);
            List<Enrollment> created = new ArrayList<>();
            for (int i = 0; i < totalEnrollments; i++) {
                User student = studentPool.get((offset + i) % studentPool.size());
                created.add(enrollmentService.enroll(lecture.getId(), student.getId()));
            }
            offset = (offset + 2) % studentPool.size();

            int approvals = Math.min(created.size() - 1, lecture.getCapacity() + 2);
            for (int i = 0; i < approvals; i++) {
                Enrollment updated = enrollmentService.approve(created.get(i).getId());
                created.set(i, updated);
            }

            List<Enrollment> actives = created.stream()
                    .filter(enrollment -> enrollment.getStatus() == EnrollmentStatus.ACTIVE)
                    .toList();
            int completionCount = Math.min(2, actives.size());
            for (int i = 0; i < completionCount; i++) {
                Enrollment target = actives.get(i);
                applyGrades(target, components.getOrDefault(lecture.getId(), List.of()));
                enrollmentService.complete(target.getId(), null);
            }
        }
    }

    private void applyGrades(Enrollment enrollment, List<GradeComponent> gradeComponents) {
        if (gradeComponents == null || gradeComponents.isEmpty()) {
            return;
        }
        double base = 0.75;
        for (int i = 0; i < gradeComponents.size(); i++) {
            GradeComponent component = gradeComponents.get(i);
            double ratio = Math.min(0.95, base + (i * 0.08));
            double score = component.getMaxScore().doubleValue() * ratio;
            enrollmentGradeService.recordGrade(enrollment.getId(), component.getId(), score);
        }
    }

    private void purgeAll() {
        enrollmentGradeRepository.deleteAllInBatch();
        enrollmentRepository.deleteAllInBatch();
        lectureScheduleRepository.deleteAllInBatch();
        gradeComponentRepository.deleteAllInBatch();
        lectureRepository.deleteAllInBatch();
        classroomRepository.deleteAllInBatch();
        scheduleSlotRepository.deleteAllInBatch();
        refreshTokenRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    private record UserSeed(String identityNo, String name, String surname, Gender gender) {
    }

    private record ClassroomSeed(String name, String location, int capacity) {
    }

    private record SlotSeed(DayOfWeek day, LocalTime start, LocalTime end) {
    }

    private record GradeComponentSeed(String name, double weight, double maxScore) {
    }

    private record LectureSeed(String name,
                               String description,
                               int capacity,
                               String teacherIdentity,
                               List<GradeComponentSeed> components) {
    }

    private record LectureScheduleSeed(String lectureName,
                                       String classroomName,
                                       int slotIndex,
                                       int startOffsetDays,
                                       int endOffsetDays) {
    }
}
