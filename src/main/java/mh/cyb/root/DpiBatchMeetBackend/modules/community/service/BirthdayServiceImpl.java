package mh.cyb.root.DpiBatchMeetBackend.modules.community.service;

import mh.cyb.root.DpiBatchMeetBackend.modules.community.dto.BirthdayAlertDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.profile.domain.Profile;
import mh.cyb.root.DpiBatchMeetBackend.modules.profile.repository.ProfileRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BirthdayServiceImpl implements BirthdayService {

    private final ProfileRepository profileRepository;

    public BirthdayServiceImpl(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public List<BirthdayAlertDto> getTodayBirthdays() {
        LocalDate today = LocalDate.now();
        return profileRepository.findByDateOfBirthMonthAndDay(today.getMonthValue(), today.getDayOfMonth())
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BirthdayAlertDto> getUpcomingBirthdays(int daysLimit) {
        List<BirthdayAlertDto> upcoming = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = 1; i <= daysLimit; i++) {
            LocalDate date = today.plusDays(i);
            List<Profile> profiles = profileRepository.findByDateOfBirthMonthAndDay(date.getMonthValue(),
                    date.getDayOfMonth());
            upcoming.addAll(profiles.stream().map(this::mapToDto).collect(Collectors.toList()));
        }

        return upcoming;
    }

    private BirthdayAlertDto mapToDto(Profile profile) {
        int age = Period.between(profile.getDateOfBirth(), LocalDate.now()).getYears();
        return BirthdayAlertDto.builder()
                .userId(profile.getUser().getId())
                .fullName(profile.getUser().getFullName())
                .age(age)
                .build();
    }
}
