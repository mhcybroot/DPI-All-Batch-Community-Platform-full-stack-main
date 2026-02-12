package mh.cyb.root.DpiBatchMeetBackend.modules.community.service;

import mh.cyb.root.DpiBatchMeetBackend.modules.community.dto.BirthdayAlertDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.profile.domain.Profile;
import mh.cyb.root.DpiBatchMeetBackend.modules.profile.repository.ProfileRepository;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BirthdayServiceImplTest {

    @Mock
    private ProfileRepository profileRepository;

    @InjectMocks
    private BirthdayServiceImpl birthdayService;

    private Profile profile;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setFullName("John Doe");
        profile = new Profile();
        profile.setUser(user);
        profile.setDateOfBirth(LocalDate.of(1990, LocalDate.now().getMonthValue(), LocalDate.now().getDayOfMonth()));
    }

    @Test
    void getTodayBirthdays_ShouldReturnList() {
        when(profileRepository.findByDateOfBirthMonthAndDay(anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(profile));

        List<BirthdayAlertDto> result = birthdayService.getTodayBirthdays();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFullName()).isEqualTo("John Doe");
    }

    @Test
    void getUpcomingBirthdays_ShouldReturnList() {
        when(profileRepository.findByDateOfBirthMonthAndDay(anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(profile));

        List<BirthdayAlertDto> result = birthdayService.getUpcomingBirthdays(3);

        assertThat(result).hasSize(3); // 1 per day for 3 days
        verify(profileRepository, times(3)).findByDateOfBirthMonthAndDay(anyInt(), anyInt());
    }
}
