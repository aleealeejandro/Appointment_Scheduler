package controller;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Locale;

public class TimeController {
    public static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
    public static DateTimeFormatter timestampFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    public static ZoneId estZoneID = ZoneId.of("America/New_York");

    public static ZoneId systemZoneID = ZoneId.systemDefault();
    public static ZoneId utcZoneID = ZoneId.of("UTC");

    public static ZonedDateTime estTimeObject = ZonedDateTime.now(estZoneID);
    public static ZonedDateTime utcTimeObject = ZonedDateTime.now(utcZoneID);
    public static ZonedDateTime systemTimeObject = ZonedDateTime.now();
    public static ZoneOffset estOffSet = estTimeObject.getOffset();
    public static ZoneOffset utcOffSet = utcTimeObject.getOffset();
    public static ZoneOffset systemOffSet = systemTimeObject.getOffset();

    public static int offsetSecondsTotal = estOffSet.compareTo(systemOffSet);
    public static LocalDateTime openTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(8,0)).plusSeconds(offsetSecondsTotal);
    public static LocalDateTime closeTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(22,0)).plusSeconds(offsetSecondsTotal);
    public static LocalDateTime lastAppointmentSlotTimeToday = LocalDateTime.of(LocalDate.now(), LocalTime.of(21,45)).plusSeconds(offsetSecondsTotal);

    public static LocalDate startDate = openTime.toLocalDate();
    public static LocalDate endDate = closeTime.toLocalDate();

    //for month
    public static YearMonth monthOfYear = YearMonth.of(startDate.getYear(), startDate.getMonth());
    public static LocalDate firstDateOfMonth = monthOfYear.atDay( 1 );
    public static LocalDate lastDateOfMonth = monthOfYear.atEndOfMonth();
    public static LocalDateTime firstDateTimeOfMonth = LocalDateTime.of(firstDateOfMonth, openTime.toLocalTime());
    public static LocalDateTime lastDateTimeOfMonth = LocalDateTime.of(lastDateOfMonth, closeTime.toLocalTime());
    //for month
    public static LocalDateTime getFirstOfMonthDateTime(LocalDateTime dateTimeChosen) {
        YearMonth monthOfYear = YearMonth.of(dateTimeChosen.getYear(), dateTimeChosen.getMonth());
        LocalDate firstDateOfMonth = monthOfYear.atDay( 1 );
        LocalDateTime firstDateTimeOfMonth = LocalDateTime.of(firstDateOfMonth, openTime.toLocalTime());

        return firstDateTimeOfMonth;
    }

    public static LocalDateTime getLastOfMonthDateTime(LocalDateTime dateTimeChosen) {
        YearMonth monthOfYear = YearMonth.of(dateTimeChosen.getYear(), dateTimeChosen.getMonth());
        LocalDate lastDateOfMonth = monthOfYear.atEndOfMonth();
        LocalDateTime lastDateTimeOfMonth = LocalDateTime.of(lastDateOfMonth, closeTime.toLocalTime());

        return lastDateTimeOfMonth;
    }


    //    for week
    public static TemporalField weekOfYear = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
    public static int weekNumber = startDate.get(weekOfYear);

    public static LocalDateTime getStartOfWeekDateTime(LocalDateTime dateTimeChosen) {
        TemporalField wkOfYear = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
        int weekNum = dateTimeChosen.get(wkOfYear);

        LocalDate startOfWkDate = dateTimeChosen.toLocalDate()
                .with(wkOfYear, weekNum)
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        LocalDateTime startOfWkDateTime = LocalDateTime.of(startOfWkDate, openTime.toLocalTime());

        return startOfWkDateTime;
    }

    public static LocalDateTime getEndOfWeekDateTime(LocalDateTime dateTimeChosen) {
        TemporalField wkOfYear = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
        int weekNum = dateTimeChosen.get(wkOfYear);

        LocalDate endOfWeekDate = dateTimeChosen.toLocalDate()
                .with(wkOfYear, weekNum)
                .with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY));

        LocalDateTime endOfWeekDateTime = LocalDateTime.of(endOfWeekDate, closeTime.toLocalTime());

        return endOfWeekDateTime;
    }

    public static LocalDate nextDateAvailable() {
        LocalDateTime startDatetime = openTime;
        LocalDate startDate = startDatetime.toLocalDate();
        LocalDateTime timeNow = LocalDateTime.now();
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        while(true) {
            if(timeNow.isAfter(lastAppointmentSlotTimeToday) && startDate.isBefore(tomorrow)) {
                startDate = startDate.plusDays(1);
                continue;
            }

            if(startDate.getDayOfWeek() != DayOfWeek.SATURDAY && startDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
                break;
            }

            startDate = startDate.plusDays(1);
        }

        return startDate;
    }
    public static LocalDate startOfWeekDate = openTime.toLocalDate()
//            .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, weekNumber)
            .with(weekOfYear, weekNumber)
            .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

    public static LocalDateTime startOfWeekDateTime = LocalDateTime.of(startOfWeekDate, openTime.toLocalTime());

    public static LocalDate endOfWeekDate = endDate
            .with(weekOfYear, weekNumber)
            .with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY));

    public static LocalDateTime endOfWeekDateTime = LocalDateTime.of(endOfWeekDate, closeTime.toLocalTime());
//    for week



    public static LocalDateTime dateTimeNow = LocalDateTime.now();
    public static LocalDate dateToday = LocalDate.now();
    public static LocalTime timeNow = LocalTime.now();

    public static LocalDateTime getUtcDatetime(LocalDateTime time) {
        OffsetDateTime sys =  time.atOffset(systemOffSet);
        ZonedDateTime zoned = sys.atZoneSameInstant(utcZoneID);
        LocalDateTime utc = zoned.toLocalDateTime();

        return utc;
    }

    public static LocalDateTime getSystemDatetime(LocalDateTime time) {
        OffsetDateTime utc =  time.atOffset(utcOffSet);
        ZonedDateTime zoned = utc.atZoneSameInstant(systemZoneID);
        LocalDateTime sys = zoned.toLocalDateTime();

        return sys;
    }

}
