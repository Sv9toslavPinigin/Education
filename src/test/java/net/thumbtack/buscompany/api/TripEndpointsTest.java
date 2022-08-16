package net.thumbtack.buscompany.api;

import com.fasterxml.jackson.core.type.TypeReference;
import net.thumbtack.buscompany.dto.request.ScheduleDto;
import net.thumbtack.buscompany.dto.request.TripRequestDto;
import net.thumbtack.buscompany.dto.response.AdminTripResponseDto;
import net.thumbtack.buscompany.dto.response.ErrorDto;
import net.thumbtack.buscompany.dto.response.MyError;
import net.thumbtack.buscompany.dto.response.TripResponseDto;
import net.thumbtack.buscompany.exception.BuscompanyErrorCode;
import net.thumbtack.buscompany.model.Bus;
import net.thumbtack.buscompany.model.Role;
import net.thumbtack.buscompany.model.Trip;
import net.thumbtack.buscompany.repository.iface.BusRepository;
import net.thumbtack.buscompany.repository.iface.TripRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Transactional
public class TripEndpointsTest extends RestControllerTest {
    @Autowired
    private TripRepository tripRepository;
    @Autowired
    private BusRepository busRepository;

    @Before
    public void setUp() {
        super.setUp();
    }

    @Test
    public void testAddTripDates() throws Exception {
        TripRequestDto requestDto = TripRequestDto.builder()
                .busName("BAW")
                .fromStation("1")
                .toStation("2")
                .start("12:00")
                .duration("01:00")
                .price("200")
                .dates(List.of("2022-01-02", "2022-02-03", "2022-03-03"))
                .build();

        final MvcResult mvcResult = mvc.perform(post("/api/trip")
                        .with(user("Admin").authorities(Role.ROLE_ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(requestDto)))
                .andExpect(status().isOk())
                .andReturn();

        AdminTripResponseDto responseDto =
                mapFromJson(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
                        AdminTripResponseDto.class);

        Assertions.assertAll(
                () -> Assertions.assertEquals(requestDto.getBusName(), responseDto.getBus().getBusName()),
                () -> Assertions.assertEquals(requestDto.getFromStation(), responseDto.getFromStation()),
                () -> Assertions.assertEquals(requestDto.getToStation(), responseDto.getToStation()),
                () -> Assertions.assertEquals(requestDto.getDuration(), responseDto.getDuration()),
                () -> Assertions.assertEquals(requestDto.getStart(), responseDto.getStart()),
                () -> Assertions.assertEquals(requestDto.getPrice(), responseDto.getPrice()),
                () -> Assertions.assertEquals(requestDto.getDates(), responseDto.getDates()),
                () -> Assertions.assertFalse(responseDto.isApproved())
        );
    }

    @Test
    public void testAddTripScheduleDaily() throws Exception {
        TripRequestDto requestDto = TripRequestDto.builder()
                .busName("BAW")
                .fromStation("1")
                .toStation("2")
                .start("12:00")
                .duration("01:00")
                .price("200")
                .schedule(ScheduleDto.builder()
                        .fromDate("2022-01-01")
                        .toDate("2022-01-08")
                        .period("daily")
                        .build())
                .build();

        List<String> listOfDates = List.of(
                "2022-01-01",
                "2022-01-02",
                "2022-01-03",
                "2022-01-04",
                "2022-01-05",
                "2022-01-06",
                "2022-01-07",
                "2022-01-08"
        );

        final MvcResult mvcResult = mvc.perform(post("/api/trip")
                        .with(user("Admin").authorities(Role.ROLE_ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(requestDto)))
                .andExpect(status().isOk())
                .andReturn();

        AdminTripResponseDto responseDto =
                mapFromJson(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
                        AdminTripResponseDto.class);

        Assertions.assertAll(
                () -> Assertions.assertEquals(requestDto.getBusName(), responseDto.getBus().getBusName()),
                () -> Assertions.assertEquals(requestDto.getFromStation(), responseDto.getFromStation()),
                () -> Assertions.assertEquals(requestDto.getToStation(), responseDto.getToStation()),
                () -> Assertions.assertEquals(requestDto.getDuration(), responseDto.getDuration()),
                () -> Assertions.assertEquals(requestDto.getStart(), responseDto.getStart()),
                () -> Assertions.assertEquals(requestDto.getPrice(), responseDto.getPrice()),
                () -> Assertions.assertEquals(listOfDates, responseDto.getDates()),
                () -> Assertions.assertFalse(responseDto.isApproved())
        );
    }

    @Test
    public void testAddTripScheduleOdd() throws Exception {
        TripRequestDto requestDto = TripRequestDto.builder()
                .busName("BAW")
                .fromStation("1")
                .toStation("2")
                .start("12:00")
                .duration("01:00")
                .price("200")
                .schedule(ScheduleDto.builder()
                        .fromDate("2022-01-01")
                        .toDate("2022-01-09")
                        .period("odd")
                        .build())
                .build();

        List<String> listOfOddDates = List.of(
                "2022-01-01",
                "2022-01-03",
                "2022-01-05",
                "2022-01-07",
                "2022-01-09"
        );

        final MvcResult mvcResult = mvc.perform(post("/api/trip")
                        .with(user("Admin").authorities(Role.ROLE_ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(requestDto)))
                .andExpect(status().isOk())
                .andReturn();

        AdminTripResponseDto responseDto =
                mapFromJson(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
                        AdminTripResponseDto.class);

        Assertions.assertAll(
                () -> Assertions.assertEquals(requestDto.getBusName(), responseDto.getBus().getBusName()),
                () -> Assertions.assertEquals(requestDto.getFromStation(), responseDto.getFromStation()),
                () -> Assertions.assertEquals(requestDto.getToStation(), responseDto.getToStation()),
                () -> Assertions.assertEquals(requestDto.getDuration(), responseDto.getDuration()),
                () -> Assertions.assertEquals(requestDto.getStart(), responseDto.getStart()),
                () -> Assertions.assertEquals(requestDto.getPrice(), responseDto.getPrice()),
                () -> Assertions.assertEquals(listOfOddDates, responseDto.getDates()),
                () -> Assertions.assertFalse(responseDto.isApproved())
        );
    }

    @Test
    public void testAddTripScheduleEven() throws Exception {
        TripRequestDto requestDto = TripRequestDto.builder()
                .busName("BAW")
                .fromStation("1")
                .toStation("2")
                .start("12:00")
                .duration("01:00")
                .price("200")
                .schedule(ScheduleDto.builder()
                        .fromDate("2022-01-01")
                        .toDate("2022-01-09")
                        .period("even")
                        .build())
                .build();

        List<String> listOfEvenDates = List.of(
                "2022-01-02",
                "2022-01-04",
                "2022-01-06",
                "2022-01-08"
        );

        final MvcResult mvcResult = mvc.perform(post("/api/trip")
                        .with(user("Admin").authorities(Role.ROLE_ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(requestDto)))
                .andExpect(status().isOk())
                .andReturn();

        AdminTripResponseDto responseDto =
                mapFromJson(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
                        AdminTripResponseDto.class);

        Assertions.assertAll(
                () -> Assertions.assertEquals(requestDto.getBusName(), responseDto.getBus().getBusName()),
                () -> Assertions.assertEquals(requestDto.getFromStation(), responseDto.getFromStation()),
                () -> Assertions.assertEquals(requestDto.getToStation(), responseDto.getToStation()),
                () -> Assertions.assertEquals(requestDto.getDuration(), responseDto.getDuration()),
                () -> Assertions.assertEquals(requestDto.getStart(), responseDto.getStart()),
                () -> Assertions.assertEquals(requestDto.getPrice(), responseDto.getPrice()),
                () -> Assertions.assertEquals(listOfEvenDates, responseDto.getDates()),
                () -> Assertions.assertFalse(responseDto.isApproved())
        );
    }

    @Test
    public void testAddTripScheduleDayOfWeek() throws Exception {
        TripRequestDto requestDto = TripRequestDto.builder()
                .busName("BAW")
                .fromStation("1")
                .toStation("2")
                .start("12:00")
                .duration("01:00")
                .price("200")
                .schedule(ScheduleDto.builder()
                        .fromDate("2022-07-01")
                        .toDate("2022-07-31")
                        .period("Mon,Wed")
                        .build())
                .build();

        List<String> listOfMonAndWed = List.of(
                "2022-07-04",
                "2022-07-06",
                "2022-07-11",
                "2022-07-13",
                "2022-07-18",
                "2022-07-20",
                "2022-07-25",
                "2022-07-27"
        );

        final MvcResult mvcResult = mvc.perform(post("/api/trip")
                        .with(user("Admin").authorities(Role.ROLE_ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(requestDto)))
                .andExpect(status().isOk())
                .andReturn();

        AdminTripResponseDto responseDto =
                mapFromJson(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
                        AdminTripResponseDto.class);

        Assertions.assertAll(
                () -> Assertions.assertEquals(requestDto.getBusName(), responseDto.getBus().getBusName()),
                () -> Assertions.assertEquals(requestDto.getFromStation(), responseDto.getFromStation()),
                () -> Assertions.assertEquals(requestDto.getToStation(), responseDto.getToStation()),
                () -> Assertions.assertEquals(requestDto.getDuration(), responseDto.getDuration()),
                () -> Assertions.assertEquals(requestDto.getStart(), responseDto.getStart()),
                () -> Assertions.assertEquals(requestDto.getPrice(), responseDto.getPrice()),
                () -> Assertions.assertEquals(listOfMonAndWed, responseDto.getDates()),
                () -> Assertions.assertFalse(responseDto.isApproved())
        );
    }

    @Test
    public void testAddTripScheduleDayOfMonth() throws Exception {
        TripRequestDto requestDto = TripRequestDto.builder()
                .busName("BAW")
                .fromStation("1")
                .toStation("2")
                .start("12:00")
                .duration("01:00")
                .price("200")
                .schedule(ScheduleDto.builder()
                        .fromDate("2022-01-01")
                        .toDate("2022-03-31")
                        .period("28,31")
                        .build())
                .build();

        List<String> listOfMonAndWed = List.of(
                "2022-01-28",
                "2022-01-31",
                "2022-02-28",
                "2022-03-28",
                "2022-03-31"
        );

        final MvcResult mvcResult = mvc.perform(post("/api/trip")
                        .with(user("Admin").authorities(Role.ROLE_ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(requestDto)))
                .andExpect(status().isOk())
                .andReturn();

        AdminTripResponseDto responseDto =
                mapFromJson(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
                        AdminTripResponseDto.class);

        Assertions.assertAll(
                () -> Assertions.assertEquals(requestDto.getBusName(), responseDto.getBus().getBusName()),
                () -> Assertions.assertEquals(requestDto.getFromStation(), responseDto.getFromStation()),
                () -> Assertions.assertEquals(requestDto.getToStation(), responseDto.getToStation()),
                () -> Assertions.assertEquals(requestDto.getDuration(), responseDto.getDuration()),
                () -> Assertions.assertEquals(requestDto.getStart(), responseDto.getStart()),
                () -> Assertions.assertEquals(requestDto.getPrice(), responseDto.getPrice()),
                () -> Assertions.assertEquals(listOfMonAndWed, responseDto.getDates()),
                () -> Assertions.assertFalse(responseDto.isApproved())
        );
    }

    @Test
    public void addTrip_validationFail_returnErrorDto() throws Exception {
        TripRequestDto requestDto = TripRequestDto.builder()
                .busName("")
                .fromStation("")
                .toStation("")
                .start("12-00")
                .duration("01-00")
                .price("200")
                .schedule(ScheduleDto.builder()
                        .fromDate("2022-01-01")
                        .toDate("2022-01-08")
                        .period("asd")
                        .build())
                .build();


        final MvcResult mvcResult = mvc.perform(post("/api/trip")
                        .with(user("Admin").authorities(Role.ROLE_ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(requestDto)))
                .andExpect(status().is(400))
                .andReturn();

        ErrorDto errorDto =
                mapFromJson(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
                        new TypeReference<>() {
                        });

        Assertions.assertAll(
                () -> Assertions.assertTrue(errorDto.getErrors().contains(new MyError("VALIDATION_FAILED",
                        "busName",
                        "must not be empty"))),
                () -> Assertions.assertTrue(errorDto.getErrors().contains(new MyError("VALIDATION_FAILED",
                        "fromStation",
                        "must not be empty"))),
                () -> Assertions.assertTrue(errorDto.getErrors().contains(new MyError("VALIDATION_FAILED",
                        "toStation",
                        "must not be empty"))),
                () -> Assertions.assertTrue(errorDto.getErrors().contains(new MyError("VALIDATION_FAILED",
                        "start",
                        "Must be time format HH:MM"))),
                () -> Assertions.assertTrue(errorDto.getErrors().contains(new MyError("VALIDATION_FAILED",
                        "duration",
                        "Must be time format HH:MM"))),
                () -> Assertions.assertTrue(errorDto.getErrors().contains(new MyError("VALIDATION_FAILED",
                        "Schedule.period",
                        "Schedule.period format must be: daily, odd, even, days of week (Sun, Mon, Thu... etc) or number of day")))
        );
    }

    @Test
    public void testEditTrip() throws Exception {
        TripRequestDto requestDto = TripRequestDto.builder()
                .busName("BAW")
                .fromStation("1")
                .toStation("2")
                .start("12:00")
                .duration("01:00")
                .price("200")
                .schedule(ScheduleDto.builder()
                        .fromDate("2022-01-01")
                        .toDate("2022-01-09")
                        .period("odd")
                        .build())
                .build();

        final MvcResult mvcAddResult = mvc.perform(post("/api/trip")
                        .with(user("Admin").authorities(Role.ROLE_ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(requestDto)))
                .andExpect(status().isOk())
                .andReturn();

        AdminTripResponseDto responseDto =
                mapFromJson(mvcAddResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
                        AdminTripResponseDto.class);

        String tripId = responseDto.getTripId();

        TripRequestDto editRequestDto = TripRequestDto.builder()
                .busName("BAW")
                .fromStation("2")
                .toStation("1")
                .start("13:00")
                .duration("02:00")
                .price("500")
                .schedule(ScheduleDto.builder()
                        .fromDate("2022-01-01")
                        .toDate("2022-01-09")
                        .period("even")
                        .build())
                .build();

        List<String> listOfEvenDates = List.of(
                "2022-01-02",
                "2022-01-04",
                "2022-01-06",
                "2022-01-08"
        );

        final MvcResult mvcEditResult = mvc.perform(put("/api/trips/{tripId}", tripId)
                        .with(user("Admin").authorities(Role.ROLE_ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(editRequestDto)))
                .andExpect(status().isOk())
                .andReturn();


        AdminTripResponseDto editResponseDto =
                mapFromJson(mvcEditResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
                        AdminTripResponseDto.class);

        Assertions.assertAll(
                () -> Assertions.assertEquals(editRequestDto.getBusName(), editResponseDto.getBus().getBusName()),
                () -> Assertions.assertEquals(editRequestDto.getFromStation(), editResponseDto.getFromStation()),
                () -> Assertions.assertEquals(editRequestDto.getToStation(), editResponseDto.getToStation()),
                () -> Assertions.assertEquals(editRequestDto.getDuration(), editResponseDto.getDuration()),
                () -> Assertions.assertEquals(editRequestDto.getStart(), editResponseDto.getStart()),
                () -> Assertions.assertEquals(editRequestDto.getPrice(), editResponseDto.getPrice()),
                () -> Assertions.assertEquals(listOfEvenDates, editResponseDto.getDates()),
                () -> Assertions.assertFalse(editResponseDto.isApproved())
        );

    }

    @Test
    public void testEditTrip_editApprovedTrip_returnErrorDto() throws Exception {
        TripRequestDto requestDto = TripRequestDto.builder()
                .busName("BAW")
                .fromStation("1")
                .toStation("2")
                .start("12:00")
                .duration("01:00")
                .price("200")
                .schedule(ScheduleDto.builder()
                        .fromDate("2022-01-01")
                        .toDate("2022-01-09")
                        .period("even")
                        .build())
                .build();

        final MvcResult mvcAddResult = mvc.perform(post("/api/trip")
                        .with(user("Admin").authorities(Role.ROLE_ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(requestDto)))
                .andExpect(status().isOk())
                .andReturn();

        AdminTripResponseDto responseDto =
                mapFromJson(mvcAddResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
                        AdminTripResponseDto.class);

        String tripId = responseDto.getTripId();

        mvc.perform(put("/api/trips/{tripId}/approve", tripId)
                        .with(user("Admin").authorities(Role.ROLE_ADMIN)))
                .andExpect(status().isOk())
                .andReturn();

        final MvcResult mvcEditResult = mvc.perform(put("/api/trips/{tripId}", tripId)
                        .with(user("Admin").authorities(Role.ROLE_ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(requestDto)))
                .andExpect(status().is(400))
                .andReturn();

        ErrorDto errorDto =
                mapFromJson(mvcEditResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
                        new TypeReference<>() {
                        });

        assertTrue(errorDto.getErrors().contains(new MyError(BuscompanyErrorCode.TRIP_EDIT_FORBIDDEN.toString(),
                "tripId", BuscompanyErrorCode.TRIP_EDIT_FORBIDDEN.getErrorString())));
    }

    @Test
    public void testApproveTrip_tripApproved() throws Exception {

        TripRequestDto requestDto = TripRequestDto.builder()
                .busName("BAW")
                .fromStation("1")
                .toStation("2")
                .start("12:00")
                .duration("01:00")
                .price("200")
                .schedule(ScheduleDto.builder()
                        .fromDate("2022-01-01")
                        .toDate("2022-01-09")
                        .period("even")
                        .build())
                .build();

        List<String> listOfEvenDates = List.of(
                "2022-01-02",
                "2022-01-04",
                "2022-01-06",
                "2022-01-08"
        );

        final MvcResult mvcAddResult = mvc.perform(post("/api/trip")
                        .with(user("Admin").authorities(Role.ROLE_ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(requestDto)))
                .andExpect(status().isOk())
                .andReturn();

        AdminTripResponseDto responseDto =
                mapFromJson(mvcAddResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
                        AdminTripResponseDto.class);

        String tripId = responseDto.getTripId();

        final MvcResult mvcApproveResult = mvc.perform(put("/api/trips/{tripId}/approve", tripId)
                        .with(user("Admin").authorities(Role.ROLE_ADMIN)))
                .andExpect(status().isOk())
                .andReturn();

        AdminTripResponseDto approveResponseDto =
                mapFromJson(mvcApproveResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
                        AdminTripResponseDto.class);

        Assertions.assertAll(
                () -> Assertions.assertEquals(requestDto.getBusName(), approveResponseDto.getBus().getBusName()),
                () -> Assertions.assertEquals(requestDto.getFromStation(), approveResponseDto.getFromStation()),
                () -> Assertions.assertEquals(requestDto.getToStation(), approveResponseDto.getToStation()),
                () -> Assertions.assertEquals(requestDto.getDuration(), approveResponseDto.getDuration()),
                () -> Assertions.assertEquals(requestDto.getStart(), approveResponseDto.getStart()),
                () -> Assertions.assertEquals(requestDto.getPrice(), approveResponseDto.getPrice()),
                () -> Assertions.assertEquals(listOfEvenDates, approveResponseDto.getDates()),
                () -> Assertions.assertTrue(approveResponseDto.isApproved())
        );
    }

    @Test
    public void testApproveTrip_tripNotExists_returnErrorDto() throws Exception {

        final MvcResult mvcResult = mvc.perform(put("/api/trips/{tripId}/approve", Integer.MAX_VALUE)
                        .with(user("Admin").authorities(Role.ROLE_ADMIN)))
                .andExpect(status().is(400))
                .andReturn();

        ErrorDto errorDto =
                mapFromJson(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
                        new TypeReference<>() {
                        });

        assertTrue(errorDto.getErrors().contains(new MyError(BuscompanyErrorCode.TRIP_NOT_FOUND.toString(),
                "tripId", BuscompanyErrorCode.TRIP_NOT_FOUND.getErrorString())));
    }

    @Test
    public void testGetTrip_returnTrip() throws Exception {
        TripRequestDto requestDto = TripRequestDto.builder()
                .busName("BAW")
                .fromStation("1")
                .toStation("2")
                .start("12:00")
                .duration("01:00")
                .price("200")
                .schedule(ScheduleDto.builder()
                        .fromDate("2022-01-01")
                        .toDate("2022-01-09")
                        .period("even")
                        .build())
                .build();

        List<String> listOfEvenDates = List.of(
                "2022-01-02",
                "2022-01-04",
                "2022-01-06",
                "2022-01-08"
        );

        final MvcResult mvcAddResult = mvc.perform(post("/api/trip")
                        .with(user("Admin").authorities(Role.ROLE_ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(requestDto)))
                .andExpect(status().isOk())
                .andReturn();

        AdminTripResponseDto responseDto =
                mapFromJson(mvcAddResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
                        AdminTripResponseDto.class);

        String tripId = responseDto.getTripId();

        final MvcResult mvcGetResult = mvc.perform(get("/api/trips/{tripId}", tripId)
                        .with(user("Admin").authorities(Role.ROLE_ADMIN)))
                .andExpect(status().isOk())
                .andReturn();

        AdminTripResponseDto approveResponseDto =
                mapFromJson(mvcGetResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
                        AdminTripResponseDto.class);

        Assertions.assertAll(
                () -> Assertions.assertEquals(requestDto.getBusName(), approveResponseDto.getBus().getBusName()),
                () -> Assertions.assertEquals(requestDto.getFromStation(), approveResponseDto.getFromStation()),
                () -> Assertions.assertEquals(requestDto.getToStation(), approveResponseDto.getToStation()),
                () -> Assertions.assertEquals(requestDto.getDuration(), approveResponseDto.getDuration()),
                () -> Assertions.assertEquals(requestDto.getStart(), approveResponseDto.getStart()),
                () -> Assertions.assertEquals(requestDto.getPrice(), approveResponseDto.getPrice()),
                () -> Assertions.assertEquals(listOfEvenDates, approveResponseDto.getDates()),
                () -> Assertions.assertFalse(approveResponseDto.isApproved())
        );
    }

    @Test
    public void testDeleteTrip_tripDeleted() throws Exception {
        TripRequestDto requestDto = TripRequestDto.builder()
                .busName("BAW")
                .fromStation("1")
                .toStation("2")
                .start("12:00")
                .duration("01:00")
                .price("200")
                .schedule(ScheduleDto.builder()
                        .fromDate("2022-01-01")
                        .toDate("2022-01-09")
                        .period("even")
                        .build())
                .build();

        final MvcResult mvcAddResult = mvc.perform(post("/api/trip")
                        .with(user("Admin").authorities(Role.ROLE_ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(requestDto)))
                .andExpect(status().isOk())
                .andReturn();

        AdminTripResponseDto responseDto =
                mapFromJson(mvcAddResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
                        AdminTripResponseDto.class);

        String tripId = responseDto.getTripId();

        mvc.perform(delete("/api/trips/{tripId}", tripId)
                        .with(user("Admin").authorities(Role.ROLE_ADMIN)))
                .andExpect(status().isOk())
                .andReturn();

        final MvcResult mvcDeleteResult = mvc.perform(delete("/api/trips/{tripId}", tripId)
                        .with(user("Admin").authorities(Role.ROLE_ADMIN)))
                .andExpect(status().is(400))
                .andReturn();

        ErrorDto errorDto = mapFromJson(mvcDeleteResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
                ErrorDto.class);

        Assertions.assertTrue(errorDto.getErrors().contains(new MyError(
                BuscompanyErrorCode.TRIP_NOT_FOUND.toString(),
                "tripId",
                BuscompanyErrorCode.TRIP_NOT_FOUND.getErrorString())));

    }

    @Test
    public void testGetTripWithParams_returnTrips() throws Exception {
        addTestData();

        MvcResult mvcResult = mvc.perform(get("/api/trips")
                        .with(user("Admin").authorities(Role.ROLE_ADMIN))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<TripResponseDto> allTripList =
                mapFromJson(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
                        new TypeReference<>() {
                        });

        Assertions.assertEquals(5, allTripList.size());

        mvcResult = mvc.perform(get("/api/trips")
                        .with(user("Admin").authorities(Role.ROLE_ADMIN))
                        .param("busName", "Baw")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<TripResponseDto> tripList =
                mapFromJson(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
                        new TypeReference<>() {
                        });

        Assertions.assertEquals(4, tripList.size());

        mvcResult = mvc.perform(get("/api/trips")
                        .with(user("Admin").authorities(Role.ROLE_ADMIN))
                        .param("busName", "Baw")
                        .param("fromDate", "2022-01-03")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        tripList = mapFromJson(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
                new TypeReference<>() {
                });

        Assertions.assertEquals(2, tripList.size());

        mvcResult = mvc.perform(get("/api/trips")
                        .with(user("Admin").authorities(Role.ROLE_ADMIN))
                        .param("busName", "Baw")
                        .param("fromStation", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        tripList = mapFromJson(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
                new TypeReference<>() {
                });

        Assertions.assertEquals(3, tripList.size());

        mvcResult = mvc.perform(get("/api/trips")
                        .with(user("Admin").authorities(Role.ROLE_ADMIN))
                        .param("busName", "Foton")
                        .param("fromStation", "2")
                        .param("toStation", "3")
                        .param("fromDate", "2022-01-03")
                        .param("toDate", "2022-03-05")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        tripList = mapFromJson(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
                new TypeReference<>() {
                });

        Assertions.assertEquals(1, tripList.size());

        mvcResult = mvc.perform(get("/api/trips")
                        .with(user("Admin").authorities(Role.ROLE_ADMIN))
                        .param("busName", "Ankai")
                        .param("fromStation", "2")
                        .param("toStation", "3")
                        .param("fromDate", "2022-01-03")
                        .param("toDate", "2022-03-05")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        tripList = mapFromJson(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
                new TypeReference<>() {
                });

        Assertions.assertEquals(0, tripList.size());
    }

    private void addTestData() {
        String busBaw = "Baw";
        String busFoton = "Foton";

        List<TripRequestDto> tripList = new ArrayList<>();
        tripList.add(TripRequestDto.builder()
                .busName(busBaw)
                .fromStation("1")
                .toStation("2")
                .start("12:00")
                .duration("01:00")
                .price("200")
                .dates(List.of(("2022-01-02"),
                        ("2022-02-03"),
                        ("2022-03-03")))
                .build());

        tripList.add(TripRequestDto.builder()
                .busName(busBaw)
                .fromStation("1")
                .toStation("2")
                .start("12:00")
                .duration("01:00")
                .price("200")
                .dates(List.of(("2022-01-02"),
                        ("2022-02-03"),
                        ("2022-03-04")))
                .build());

        tripList.add(TripRequestDto.builder()
                .busName(busBaw)
                .fromStation("1")
                .toStation("3")
                .start("12:00")
                .duration("01:00")
                .price("200")
                .dates(List.of(("2022-01-03"),
                        ("2022-02-04"),
                        ("2022-03-05")))
                .build());

        tripList.add(TripRequestDto.builder()
                .busName(busBaw)
                .fromStation("2")
                .toStation("3")
                .start("12:00")
                .duration("01:00")
                .price("200")
                .dates(List.of(("2022-01-03"),
                        ("2022-02-04"),
                        ("2022-03-06")))
                .build());

        tripList.add(TripRequestDto.builder()
                .busName(busFoton)
                .fromStation("2")
                .toStation("3")
                .start("12:00")
                .duration("01:00")
                .price("200")
                .dates(List.of(("2022-01-03"),
                        ("2022-02-04"),
                        ("2022-03-05")))
                .build());

        tripList.forEach(x-> {
            try {
                mvc.perform(post("/api/trip")
                                .with(user("Admin").authorities(Role.ROLE_ADMIN))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapToJson(x)))
                        .andExpect(status().isOk());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
