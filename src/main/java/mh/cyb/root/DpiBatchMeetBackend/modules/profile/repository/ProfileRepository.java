package mh.cyb.root.DpiBatchMeetBackend.modules.profile.repository;

import mh.cyb.root.DpiBatchMeetBackend.modules.profile.domain.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByUserId(Long userId);

    @Query("SELECT p FROM Profile p WHERE MONTH(p.dateOfBirth) = :month AND DAY(p.dateOfBirth) = :day")
    List<Profile> findByDateOfBirthMonthAndDay(@Param("month") int month, @Param("day") int day);
}
