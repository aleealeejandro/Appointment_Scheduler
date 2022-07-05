package controller;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Locale;

/**
 *
 * @author Alexander Padilla
 */
public class TimeController {
    public static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
    public static DateTimeFormatter timestampFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    public static DateTimeFormatter monthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy");
    private static final ZoneId estZoneID = ZoneId.of("America/New_York");

    public static ZoneId systemZoneID = ZoneId.systemDefault();
    public static ZoneId utcZoneID = ZoneId.of("UTC");

    public static ZonedDateTime estTimeObject = ZonedDateTime.now(estZoneID);
    public static ZonedDateTime utcTimeObject = ZonedDateTime.now(utcZoneID);
    public static ZonedDateTime systemTimeObject = ZonedDateTime.now();
    public static ZoneOffset estOffSet = estTimeObject.getOffset();
    public static ZoneOffset utcOffSet = utcTimeObject.getOffset();
    public static ZoneOffset systemOffSet = systemTimeObject.getOffset();
    public static int offsetSecondsTotal = estOffSet.compareTo(systemOffSet);
    public static int amountOfHoursOfficeIsOpen = 14;
    public static int amountOfHoursOfficeIsClosed = 10;
    public static int minimumTimeDurationMinutes = 15;
    public static LocalDateTime openTime = LocalDateTime.of(estTimeObject.toLocalDate(), LocalTime.of(8,0)).plusSeconds(offsetSecondsTotal);

    /**
     * gets the first datetime of the month
     *
     * @param dateTimeChosen the datetime chosen by user
     * @return first datetime of the month
     */
    public static LocalDateTime getFirstOfMonthDateTime(LocalDateTime dateTimeChosen) {
        YearMonth monthOfYear = YearMonth.of(dateTimeChosen.getYear(), dateTimeChosen.getMonth());
        LocalDate firstDateOfMonth = monthOfYear.atDay( 1 );
        LocalDateTime firstOfMonthDateTime = LocalDateTime.of(firstDateOfMonth, LocalTime.now());
        firstOfMonthDateTime = getOpenOrCloseTime(firstOfMonthDateTime, true);

        return firstOfMonthDateTime;
    }

    /**
     * gets the last datetime of the month
     *
     * @param dateTimeChosen the datetime chosen by user
     * @return last datetime of the month
     */
    public static LocalDateTime getLastOfMonthDateTime(LocalDateTime dateTimeChosen) {
        YearMonth monthOfYear = YearMonth.of(dateTimeChosen.getYear(), dateTimeChosen.getMonth());
        LocalDate lastDateOfMonth = monthOfYear.atEndOfMonth();
        LocalDateTime lastOfMonthDateTime = LocalDateTime.of(lastDateOfMonth, LocalTime.now());
        lastOfMonthDateTime = getOpenOrCloseTime(lastOfMonthDateTime, false);

        return lastOfMonthDateTime;
    }

    /**
     * gets the start datetime of the week
     *
     * @param dateTimeChosen the datetime chosen by user
     * @return start datetime of the week
     */
    public static LocalDateTime getStartOfWeekDateTime(LocalDateTime dateTimeChosen) {
        TemporalField wkOfYear = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
        int weekNum = dateTimeChosen.get(wkOfYear);
        LocalDate startOfWkDate;
        int offsetHours = offsetSecondsTotal/3600;
        LocalDateTime startOfWeekDateTime;

        if(offsetHours >= 16) {
            startOfWkDate = dateTimeChosen.toLocalDate()
                    .with(wkOfYear, weekNum)
                    .with(TemporalAdjusters.previousOrSame(DayOfWeek.TUESDAY));
        } else {
            startOfWkDate = dateTimeChosen.toLocalDate()
                    .with(wkOfYear, weekNum)
                    .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        }

        startOfWeekDateTime = LocalDateTime.of(startOfWkDate, LocalTime.now());
        startOfWeekDateTime = getOpenOrCloseTime(startOfWeekDateTime, true);

        return startOfWeekDateTime;
    }

    /**
     * gets the end datetime of the week
     *
     * @param dateTimeChosen the datetime chosen by user
     * @return end datetime of the week
     */
    public static LocalDateTime getEndOfWeekDateTime(LocalDateTime dateTimeChosen) {
        TemporalField wkOfYear = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
        int weekNum = dateTimeChosen.get(wkOfYear);

        LocalDate endOfWeekDate;
        LocalDateTime endOfWeekDateTime;
        int offsetHours = offsetSecondsTotal/3600;
        LocalDateTime closingTime;

        if(offsetHours >= 3) {
            endOfWeekDate = dateTimeChosen.toLocalDate()
                    .with(wkOfYear, weekNum)
                    .with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));
        } else {
            endOfWeekDate = dateTimeChosen.toLocalDate()
                    .with(wkOfYear, weekNum)
                    .with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY));
        }

        endOfWeekDateTime = LocalDateTime.of(endOfWeekDate, LocalTime.now());
        closingTime = getOpenOrCloseTime(endOfWeekDateTime, false);

        return closingTime;
    }

    /**
     * gets the open or close datetime based on the day of the week and timezone
     *
     * @param dateTimeChosen the datetime chosen by user
     * @param gettingOpenTime boolean value that determines if user is getting the open or close time
     * @return open or close datetime
     */
    public static LocalDateTime getOpenOrCloseTime(LocalDateTime dateTimeChosen, boolean gettingOpenTime) {
        int offsetHours = offsetSecondsTotal/3600;
        LocalDate dateChosen = dateTimeChosen.toLocalDate();
        LocalDateTime secondShiftBeginning = LocalDateTime.of(dateChosen, openTime.toLocalTime());
        LocalDateTime secondShiftEnding = LocalDateTime.of(dateChosen.plusDays(1), LocalTime.of(0, 0));

        LocalDateTime breakBeginning = secondShiftBeginning.minusHours(amountOfHoursOfficeIsClosed);
        LocalDateTime breakEnding = secondShiftBeginning;

        LocalDateTime firstShiftBeginning = LocalDateTime.of(dateChosen, LocalTime.of(0, 0));
        LocalDateTime firstShiftEnding = breakBeginning;

        LocalDateTime startOfDay;
        LocalDateTime endOfDay;

        if(offsetHours >= 3 && offsetHours <= 15) {
            if (dateChosen.getDayOfWeek().equals(DayOfWeek.MONDAY)) {
                startOfDay = secondShiftBeginning;
                endOfDay = secondShiftEnding;
            } else if (dateChosen.getDayOfWeek().equals(DayOfWeek.SATURDAY)) {
                startOfDay = firstShiftBeginning;
                endOfDay = firstShiftEnding;
            } else {
                startOfDay = firstShiftBeginning;
                endOfDay = secondShiftEnding;
            }
        } else {
            startOfDay = LocalDateTime.of(dateTimeChosen.toLocalDate(), openTime.toLocalTime());
            endOfDay = startOfDay.plusHours(amountOfHoursOfficeIsOpen);
        }

        if(gettingOpenTime) {
            return startOfDay;
        }
        return endOfDay;
    }

    /**
     * finds the next date available
     *
     * @return the next date available
     */
    public static LocalDate nextDateAvailable() {
        LocalDateTime timeNow = LocalDateTime.now();
        LocalDateTime startDatetime = getOpenOrCloseTime(timeNow, true);
        LocalDate startDate = startDatetime.toLocalDate();
        LocalDateTime lastAppointmentSlotTimeToday = getOpenOrCloseTime(timeNow, false).minusMinutes(minimumTimeDurationMinutes);
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalDate oneYearFromNow = timeNow.plusYears(1).toLocalDate();

        while(startDate.isBefore(oneYearFromNow)) {
            if(startDate.isBefore(tomorrow)) {
                startDate = startDate.plusDays(1);
                continue;
            }

            if(timeNow.isAfter(lastAppointmentSlotTimeToday) && startDate.isBefore(tomorrow)) {
                startDate = startDate.plusDays(1);
                continue;
            }

            int offsetHours = TimeController.offsetSecondsTotal/3600;

            if(offsetHours >= 16) {
                if(startDate.getDayOfWeek() != DayOfWeek.SUNDAY && startDate.getDayOfWeek() != DayOfWeek.MONDAY) {
                    break;
                }
            } else if(offsetHours >= 3) {
                if(startDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
                    break;
                }
            } else {
                if(startDate.getDayOfWeek() != DayOfWeek.SATURDAY && startDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
                    break;
                }
            }

            startDate = startDate.plusDays(1);
        }

        return startDate;
    }

    /**
     * converts given time to UTC time
     *
     * @param time the datetime being converted to UTC
     * @return datetime in UTC
     */
    public static LocalDateTime getUtcDatetime(LocalDateTime time) {
        OffsetDateTime sys =  time.atOffset(systemOffSet);
        ZonedDateTime zoned = sys.atZoneSameInstant(utcZoneID);

        return zoned.toLocalDateTime();
    }

    /**
     * converts given time to system's time
     *
     * @param time the datetime being converted to UTC
     * @return datetime in system's time
     */
    public static LocalDateTime getSystemDatetime(LocalDateTime time) {
        OffsetDateTime utc =  time.atOffset(utcOffSet);
        ZonedDateTime zoned = utc.atZoneSameInstant(systemZoneID);

        return zoned.toLocalDateTime();
    }

}
