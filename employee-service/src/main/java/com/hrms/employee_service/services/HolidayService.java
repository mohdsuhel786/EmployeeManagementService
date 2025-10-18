package com.hrms.employee_service.services;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Set;

@Service
public class HolidayService {
    private final Set<LocalDate> holidays = Set.of(
            LocalDate.of(2025, 1, 1),
            LocalDate.of(2025, 8, 15),
            LocalDate.of(2025, 12, 25)
    );

    public boolean isHoliday(LocalDate date) {
        return holidays.contains(date);
    }
}
