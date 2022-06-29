package controller;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Locale;

public class TimeController {
    public static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
    public static DateTimeFormatter timestampFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    public static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("hh:mm a MM/dd/yy");
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
    public static int amountOfHoursOfficeIsOpen = 14;
    public static int amountOfHoursOfficeIsClosed = 10;
    public static int minimumTimeDurationMinutes = 15;
//    public static LocalDateTime openTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(8,0)).plusSeconds(offsetSecondsTotal);
    public static LocalDateTime openTime = LocalDateTime.of(estTimeObject.toLocalDate(), LocalTime.of(8,0)).plusSeconds(offsetSecondsTotal);
    public static LocalDateTime closeTime = openTime.plusHours(amountOfHoursOfficeIsOpen).plusSeconds(offsetSecondsTotal);
    public static LocalDateTime lastAppointmentSlotTimeToday = closeTime.minusMinutes(15).plusSeconds(offsetSecondsTotal);

    public static LocalDateTime getFirstOfMonthDateTime(LocalDateTime dateTimeChosen) {
        YearMonth monthOfYear = YearMonth.of(dateTimeChosen.getYear(), dateTimeChosen.getMonth());
        LocalDate firstDateOfMonth = monthOfYear.atDay( 1 );

        return LocalDateTime.of(firstDateOfMonth, openTime.toLocalTime());
    }

    public static LocalDateTime getLastOfMonthDateTime(LocalDateTime dateTimeChosen) {
        YearMonth monthOfYear = YearMonth.of(dateTimeChosen.getYear(), dateTimeChosen.getMonth());
        LocalDate lastDateOfMonth = monthOfYear.atEndOfMonth();

        return LocalDateTime.of(lastDateOfMonth, closeTime.toLocalTime());
    }

    public static LocalDateTime getStartOfWeekDateTime(LocalDateTime dateTimeChosen) {
        TemporalField wkOfYear = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
        int weekNum = dateTimeChosen.get(wkOfYear);

        LocalDate startOfWkDate = dateTimeChosen.toLocalDate()
                .with(wkOfYear, weekNum)
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        return LocalDateTime.of(startOfWkDate, openTime.toLocalTime());
    }

    public static LocalDateTime getEndOfWeekDateTime(LocalDateTime dateTimeChosen) {
        TemporalField wkOfYear = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
        int weekNum = dateTimeChosen.get(wkOfYear);

        LocalDate endOfWeekDate = dateTimeChosen.toLocalDate()
                .with(wkOfYear, weekNum)
                .with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY));

        return LocalDateTime.of(endOfWeekDate, closeTime.toLocalTime());
    }

    public static LocalDateTime getStartOfDayDateTime(LocalDateTime dateTimeChosen) {
        return LocalDateTime.of(dateTimeChosen.toLocalDate(), openTime.toLocalTime());
    }

//    public static LocalDateTime getEndOfDayDateTime(LocalDateTime dateTimeChosen) {
//        LocalDateTime endOfDayDateTime = LocalDateTime.of(dateTimeChosen.toLocalDate(), openTime.toLocalTime());
//        return endOfDayDateTime;
//    }

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

    public static LocalTime timeNow = LocalTime.now();

    public static LocalDateTime getUtcDatetime(LocalDateTime time) {
        OffsetDateTime sys =  time.atOffset(systemOffSet);
        ZonedDateTime zoned = sys.atZoneSameInstant(utcZoneID);
        System.out.println("\ntime in getUtcDatetime() TimeController: " + time);
        System.out.println("sys in getUtcDatetime() TimeController: " + sys);
        System.out.println("zoned in getUtcDatetime() TimeController: " + zoned);
        System.out.println("zoned.toLocalDateTime() in getUtcDatetime() TimeController: " + zoned.toLocalDateTime() + "\n");

        return zoned.toLocalDateTime();
    }

    public static LocalDateTime getSystemDatetime(LocalDateTime time) {
        OffsetDateTime utc =  time.atOffset(utcOffSet);
        ZonedDateTime zoned = utc.atZoneSameInstant(systemZoneID);

        return zoned.toLocalDateTime();
    }

}
