package mh.cyb.root.DpiBatchMeetBackend.modules.community.service;

import mh.cyb.root.DpiBatchMeetBackend.modules.community.dto.BirthdayAlertDto;
import java.util.List;

public interface BirthdayService {
    List<BirthdayAlertDto> getTodayBirthdays();

    List<BirthdayAlertDto> getUpcomingBirthdays(int daysLimit);
}
