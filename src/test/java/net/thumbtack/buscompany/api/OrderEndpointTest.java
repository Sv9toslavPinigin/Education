package net.thumbtack.buscompany.api;

import com.fasterxml.jackson.core.type.TypeReference;
import net.thumbtack.buscompany.dto.request.*;
import net.thumbtack.buscompany.dto.response.*;
import net.thumbtack.buscompany.exception.BuscompanyErrorCode;
import net.thumbtack.buscompany.model.Client;
import net.thumbtack.buscompany.model.Role;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Transactional
public class OrderEndpointTest extends RestControllerTest {


    @Before
    public void setUp() {
        super.setUp();
    }

    @Test
    public void testAddOrder_orderAdded() throws Exception {
        AdminTripResponseDto trip = addTestData().stream().filter(AdminTripResponseDto::isApproved).findFirst().get();
        List<PassengerDto> passengerList = List.of(
                PassengerDto.builder().firstName("Имя").lastName("Фамилия").passport("123").build(),
                PassengerDto.builder().firstName("Имя").lastName("Фамилия").passport("321").build()
        );
        OrderRequestDto requestDto = OrderRequestDto.builder()
                .tripId(Integer.parseInt(trip.getTripId()))
                .date(trip.getDates().stream().findFirst().get())
                .passengers(passengerList)
                .build();


        final MvcResult mvcResult = mvc.perform(post("/api/orders")
                        .with(user("Client").authorities(Role.ROLE_CLIENT))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(requestDto)))
                .andExpect(status().isOk())
                .andReturn();

        OrderResponseDto responseDto =
                mapFromJson(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
                        OrderResponseDto.class);

        Assertions.assertAll(
                () -> Assertions.assertEquals(requestDto.getTripId(), responseDto.getTripId()),
                () -> Assertions.assertEquals(requestDto.getDate(), responseDto.getDate()),
                () -> Assertions.assertEquals("BAW", responseDto.getBusName()),
                () -> Assertions.assertEquals("200", responseDto.getPrice()),
                () -> Assertions.assertEquals("400", responseDto.getTotalPrice()),
                () -> Assertions.assertEquals("1", responseDto.getFromStation()),
                () -> Assertions.assertEquals("2", responseDto.getToStation()),
                () -> Assertions.assertEquals("12:00", responseDto.getStart()),
                () -> Assertions.assertEquals("01:00", responseDto.getDuration()),
                () -> Assertions.assertEquals(requestDto.getPassengers().get(0).getPassport(),
                        responseDto.getPassengers().get(0).getPassport()),
                () -> Assertions.assertEquals(requestDto.getPassengers().get(1).getPassport(),
                        responseDto.getPassengers().get(1).getPassport()));
    }

    @Test
    public void testAddOrder_orderNotAddedTripNotApproved_returnErrorDto() throws Exception {
        AdminTripResponseDto notApprovedTrip = addTestData().stream().filter(x -> !x.isApproved()).findFirst().get();
        List<PassengerDto> passengerList = List.of(
                PassengerDto.builder().firstName("Имя").lastName("Фамилия").passport("123").build(),
                PassengerDto.builder().firstName("Имя").lastName("Фамилия").passport("321").build()
        );
        OrderRequestDto requestDto = OrderRequestDto.builder()
                .tripId(Integer.parseInt(notApprovedTrip.getTripId()))
                .date(notApprovedTrip.getDates().stream().findFirst().get())
                .passengers(passengerList)
                .build();


        final MvcResult mvcResult = mvc.perform(post("/api/orders")
                        .with(user("client").authorities(Role.ROLE_CLIENT))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(requestDto)))
                .andExpect(status().is(400))
                .andReturn();

        ErrorDto responseDto =
                mapFromJson(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
                        ErrorDto.class);

        assertTrue(responseDto.getErrors().contains(MyError.builder().errorCode("TRIP_NOT_APPROVED")
                .field("tripId").message("Trip with this id is not approved").build()));

    }

    @Test
    public void testGetOrdersWithParamsByAdmin_returnAllOrders() throws Exception {
        List<AdminTripResponseDto> trips = addTestData();
        AdminTripResponseDto tripBusFoton = trips.stream()
                .filter(AdminTripResponseDto::isApproved)
                .filter(x -> x.getBus().getBusName().equals("BAW"))
                .findFirst().get();
        AdminTripResponseDto tripBusBaw = trips.stream()
                .filter(AdminTripResponseDto::isApproved)
                .filter(x -> x.getBus().getBusName().equals("FOTON"))
                .findFirst().get();

        Client client1 = Client.builder()
                .id(1)
                .role(Role.ROLE_CLIENT)
                .username("Client1")
                .phone("")
                .build();
        Client client2 = Client.builder()
                .id(2)
                .role(Role.ROLE_CLIENT)
                .username("Client2")
                .phone("").build();
        List<PassengerDto> passengerList = List.of(
                PassengerDto.builder().firstName("Имя").lastName("Фамилия").passport("123").build(),
                PassengerDto.builder().firstName("Имя").lastName("Фамилия").passport("321").build()
        );
        OrderRequestDto requestDto = OrderRequestDto.builder()
                .tripId(Integer.parseInt(tripBusFoton.getTripId()))
                .date(tripBusFoton.getDates().stream().findFirst().get())
                .passengers(passengerList)
                .build();


        mvc.perform(post("/api/orders")
                        .with(user(client1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(requestDto)))
                .andExpect(status().isOk())
                .andReturn();

        requestDto = OrderRequestDto.builder()
                .tripId(Integer.parseInt(tripBusBaw.getTripId()))
                .date(tripBusBaw.getDates().stream().findFirst().get())
                .passengers(passengerList)
                .build();

        mvc.perform(post("/api/orders")
                        .with(user(client2))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(requestDto)))
                .andExpect(status().isOk())
                .andReturn();

        MvcResult mvcResult = mvc.perform(get("/api/orders")
                        .with(user("admin").authorities(Role.ROLE_ADMIN))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<OrderResponseDto> responseDto =
                mapFromJson(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
                });

        assertEquals(2, responseDto.size());


        mvcResult = mvc.perform(get("/api/orders")
                        .with(user("admin").authorities(Role.ROLE_ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("busName", "Baw"))
                .andExpect(status().isOk())
                .andReturn();

        responseDto =
                mapFromJson(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
                });

        assertEquals(1, responseDto.size());

        mvcResult = mvc.perform(get("/api/orders")
                        .with(user("admin").authorities(Role.ROLE_ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("busName", "Foton"))
                .andExpect(status().isOk())
                .andReturn();

        responseDto =
                mapFromJson(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
                });

        assertEquals(1, responseDto.size());

        mvcResult = mvc.perform(get("/api/orders")
                        .with(user("admin").authorities(Role.ROLE_ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("fromDate", "2022-01-02"))
                .andExpect(status().isOk())
                .andReturn();

        responseDto =
                mapFromJson(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
                });

        assertEquals(1, responseDto.size());

        mvcResult = mvc.perform(get("/api/orders")
                        .with(user("admin").authorities(Role.ROLE_ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("toDate", "2022-03-03"))
                .andExpect(status().isOk())
                .andReturn();

        responseDto =
                mapFromJson(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
                });

        assertEquals(1, responseDto.size());


        mvcResult = mvc.perform(get("/api/orders")
                        .with(user("admin").authorities(Role.ROLE_ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("fromStation", "1"))
                .andExpect(status().isOk())
                .andReturn();

        responseDto =
                mapFromJson(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
                });

        assertEquals(2, responseDto.size());

        mvcResult = mvc.perform(get("/api/orders")
                        .with(user("admin").authorities(Role.ROLE_ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("fromStation", "1")
                        .param("toStation", "3")
                        .param("busName", "FOTON")
                        .param("fromDate", "2022-01-03")
                        .param("toDate", "2022-03-05"))
                .andExpect(status().isOk())
                .andReturn();

        responseDto =
                mapFromJson(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
                });

        assertEquals(1, responseDto.size());
    }

    @Test
    public void testGetOrderWithParamByClient_returnClientOrders() throws Exception {
        ClientRegisterRequestDto requestDto = ClientRegisterRequestDto.builder()
                .firstName("Имя")
                .lastName("Фамилия")
                .patronymic("")
                .email("example@mail.ru")
                .phone("89876543211")
                .login("Client1")
                .password("Password").build();

        mvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(requestDto)))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andReturn();

        requestDto = ClientRegisterRequestDto.builder()
                .firstName("Имя")
                .lastName("Фамилия")
                .patronymic("")
                .email("example@mail.ru")
                .phone("89876543211")
                .login("Client2")
                .password("Password").build();

        mvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(requestDto)))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andReturn();

        List<AdminTripResponseDto> trips = addTestData();
        AdminTripResponseDto tripBusFoton = trips.stream()
                .filter(AdminTripResponseDto::isApproved)
                .filter(x -> x.getBus().getBusName().equals("FOTON"))
                .findFirst().get();
        AdminTripResponseDto tripBusBaw = trips.stream()
                .filter(AdminTripResponseDto::isApproved)
                .filter(x -> x.getBus().getBusName().equals("BAW"))
                .findFirst().get();


        List<PassengerDto> passengerList = List.of(
                PassengerDto.builder().firstName("Имя").lastName("Фамилия").passport("123").build(),
                PassengerDto.builder().firstName("Имя").lastName("Фамилия").passport("321").build()
        );

        OrderRequestDto orderRequestDto = OrderRequestDto.builder()
                .tripId(Integer.parseInt(tripBusFoton.getTripId()))
                .date(tripBusFoton.getDates().stream().findFirst().get())
                .passengers(passengerList)
                .build();


        mvc.perform(post("/api/orders")
                        .with(user("client1"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(orderRequestDto)))
                .andExpect(status().isOk())
                .andReturn();

        orderRequestDto = OrderRequestDto.builder()
                .tripId(Integer.parseInt(tripBusBaw.getTripId()))
                .date(tripBusBaw.getDates().stream().findFirst().get())
                .passengers(passengerList)
                .build();

        mvc.perform(post("/api/orders")
                        .with(user("client2"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(orderRequestDto)))
                .andExpect(status().isOk())
                .andReturn();
        MvcResult mvcResult = mvc.perform(get("/api/orders")
                        .with(user("client1").authorities(Role.ROLE_CLIENT))
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("fromStation", "1")
                        .param("toStation", "3")
                        .param("busName", "FOTON")
                        .param("fromDate", "2022-01-03")
                        .param("toDate", "2022-03-05"))
                .andExpect(status().isOk())
                .andReturn();

        List<OrderResponseDto> responseDto =
                mapFromJson(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
                });

        assertEquals(1, responseDto.size());

        mvcResult = mvc.perform(get("/api/orders")
                        .with(user("client2").authorities(Role.ROLE_CLIENT))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        responseDto =
                mapFromJson(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
                });

        assertEquals(1, responseDto.size());


    }

    @Test
    public void testGetFreePlaces_returnFreePlaces() throws Exception {
        ClientRegisterRequestDto requestDto = ClientRegisterRequestDto.builder()
                .firstName("Имя")
                .lastName("Фамилия")
                .patronymic("")
                .email("example@mail.ru")
                .phone("89876543211")
                .login("client1")
                .password("Password").build();

        mvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(requestDto)))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andReturn();

        List<AdminTripResponseDto> trips = addTestData();

        AdminTripResponseDto trip = trips.stream()
                .filter(AdminTripResponseDto::isApproved)
                .findFirst().get();
        List<PassengerDto> passengerList = List.of(
                PassengerDto.builder().firstName("Имя").lastName("Фамилия").passport("123").build(),
                PassengerDto.builder().firstName("Имя").lastName("Фамилия").passport("321").build()
        );


        OrderRequestDto orderRequestDto = OrderRequestDto.builder()
                .tripId(Integer.parseInt(trip.getTripId()))
                .date(trip.getDates().stream().findFirst().get())
                .passengers(passengerList)
                .build();

        MvcResult mvcResult = mvc.perform(post("/api/orders")
                        .with(user("client1"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(orderRequestDto)))
                .andExpect(status().isOk())
                .andReturn();

        OrderResponseDto orderResponseDto =
                mapFromJson(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
                        OrderResponseDto.class);

        int orderId = orderResponseDto.getOrderId();

        mvcResult = mvc.perform(get("/api/places/{orderId}", orderId)
                        .with(user("client1"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<String> freePlaces = mapFromJson(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
                new TypeReference<>() {
                });

        List<String> expectedFreePlacesList = IntStream.range(1, trip.getBus().getPlaceCount() + 1)
                .boxed()
                .map(x -> "Место" + x)
                .collect(Collectors.toList());
        assertEquals(expectedFreePlacesList.toString(), freePlaces.toString());
    }

    @Test
    public void testSelectPlace_placeSelected() throws Exception {
        ClientRegisterRequestDto requestDto = ClientRegisterRequestDto.builder()
                .firstName("Имя")
                .lastName("Фамилия")
                .patronymic("")
                .email("example@mail.ru")
                .phone("89876543211")
                .login("client1")
                .password("Password").build();

        mvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(requestDto)))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andReturn();

        List<AdminTripResponseDto> trips = addTestData();

        AdminTripResponseDto trip = trips.stream()
                .filter(AdminTripResponseDto::isApproved)
                .filter(x -> x.getBus().getBusName().equals("BAW"))
                .findFirst().get();
        List<PassengerDto> passengerList = List.of(
                PassengerDto.builder().firstName("Имя").lastName("Фамилия").passport("123").build(),
                PassengerDto.builder().firstName("Имя").lastName("Фамилия").passport("321").build()
        );


        OrderRequestDto orderRequestDto = OrderRequestDto.builder()
                .tripId(Integer.parseInt(trip.getTripId()))
                .date(trip.getDates().stream().findFirst().get())
                .passengers(passengerList)
                .build();

        MvcResult mvcResult = mvc.perform(post("/api/orders")
                        .with(user("client1"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(orderRequestDto)))
                .andExpect(status().isOk())
                .andReturn();

        OrderResponseDto orderResponseDto =
                mapFromJson(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
                        OrderResponseDto.class);

        int orderId = orderResponseDto.getOrderId();
        int place = 5;

        PlaceSelectRequestDto placeSelectRequestDto = PlaceSelectRequestDto.builder()
                .orderId(orderId)
                .firstName("Имя")
                .lastName("Фамилия")
                .passport("123")
                .place(place)
                .build();

        mvcResult = mvc.perform(post("/api/places")
                        .with(user("client1"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(placeSelectRequestDto)))
                .andExpect(status().isOk())
                .andReturn();

        PlaceSelectResponseDto responseDto =
                mapFromJson(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
                        PlaceSelectResponseDto.class);

        String expectedTicket = "Билет " + orderId + "_" + placeSelectRequestDto.getPlace();

        Assertions.assertAll(
                () -> assertEquals(placeSelectRequestDto.getFirstName(), responseDto.getFirstName()),
                () -> assertEquals(placeSelectRequestDto.getLastName(), responseDto.getLastName()),
                () -> assertEquals(placeSelectRequestDto.getPassport(), responseDto.getPassport()),
                () -> assertEquals(placeSelectRequestDto.getPlace(), responseDto.getPlace()),
                () -> assertEquals(expectedTicket, responseDto.getTicket())
        );


        mvcResult = mvc.perform(get("/api/places/{orderId}", orderId)
                        .with(user("client1"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<String> freePlaces = mapFromJson(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
                new TypeReference<>() {
                });

        List<String> expectedFreePlacesList = IntStream.range(1, trip.getBus().getPlaceCount() + 1)
                .boxed()
                .filter(x -> x != place)
                .map(x -> "Место" + x)
                .collect(Collectors.toList());
        assertEquals(expectedFreePlacesList.toString(), freePlaces.toString());
    }

    @Test
    public void testSelectPlace_placeAlreadySelect_returnErrorDto() throws Exception {
        ClientRegisterRequestDto requestDto = ClientRegisterRequestDto.builder()
                .firstName("Имя")
                .lastName("Фамилия")
                .patronymic("")
                .email("example@mail.ru")
                .phone("89876543211")
                .login("client1")
                .password("Password").build();

        mvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(requestDto)))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andReturn();

        List<AdminTripResponseDto> trips = addTestData();

        AdminTripResponseDto tripBusBaw = trips.stream()
                .filter(AdminTripResponseDto::isApproved)
                .filter(x -> x.getBus().getBusName().equals("BAW"))
                .findFirst().get();
        List<PassengerDto> passengerList = List.of(
                PassengerDto.builder().firstName("Имя").lastName("Фамилия").passport("123").build(),
                PassengerDto.builder().firstName("Имя").lastName("Фамилия").passport("321").build()
        );


        OrderRequestDto orderRequestDto = OrderRequestDto.builder()
                .tripId(Integer.parseInt(tripBusBaw.getTripId()))
                .date(tripBusBaw.getDates().stream().findFirst().get())
                .passengers(passengerList)
                .build();

        MvcResult mvcResult = mvc.perform(post("/api/orders")
                        .with(user("client1"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(orderRequestDto)))
                .andExpect(status().isOk())
                .andReturn();

        OrderResponseDto orderResponseDto =
                mapFromJson(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
                        OrderResponseDto.class);

        int orderId = orderResponseDto.getOrderId();

        PlaceSelectRequestDto placeSelectRequestDto = PlaceSelectRequestDto.builder()
                .orderId(orderId)
                .firstName("Имя")
                .lastName("Фамилия")
                .passport("123")
                .place(5)
                .build();

        mvcResult = mvc.perform(post("/api/places")
                        .with(user("client1"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(placeSelectRequestDto)))
                .andExpect(status().isOk())
                .andReturn();

        mvcResult = mvc.perform(post("/api/places")
                        .with(user("client1"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(placeSelectRequestDto)))
                .andExpect(status().is(400))
                .andReturn();

        ErrorDto errorDto =
                mapFromJson(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8), new TypeReference<>() {
                });

        Assertions.assertAll(
                () -> assertEquals(errorDto.getErrors().get(0).getField(), "place"),
                () -> assertEquals(errorDto.getErrors().get(0).getErrorCode(), BuscompanyErrorCode.PLACE_ALREADY_SELECT.toString()),
                () -> assertEquals(errorDto.getErrors().get(0).getMessage(), BuscompanyErrorCode.PLACE_ALREADY_SELECT.getErrorString())
        );
    }

    @Test
    public void testRemoveOrder_orderRemoved() throws Exception {
        ClientRegisterRequestDto requestDto = ClientRegisterRequestDto.builder()
                .firstName("Имя")
                .lastName("Фамилия")
                .patronymic("")
                .email("example@mail.ru")
                .phone("89876543211")
                .login("client1")
                .password("Password").build();

        mvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(requestDto)))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andReturn();

        List<AdminTripResponseDto> trips = addTestData();

        AdminTripResponseDto trip = trips.stream()
                .filter(AdminTripResponseDto::isApproved)
                .findFirst().get();
        List<PassengerDto> passengerList = List.of(
                PassengerDto.builder().firstName("Имя").lastName("Фамилия").passport("123").build(),
                PassengerDto.builder().firstName("Имя").lastName("Фамилия").passport("321").build()
        );


        OrderRequestDto orderRequestDto = OrderRequestDto.builder()
                .tripId(Integer.parseInt(trip.getTripId()))
                .date(trip.getDates().stream().findFirst().get())
                .passengers(passengerList)
                .build();

        MvcResult mvcResult = mvc.perform(post("/api/orders")
                        .with(user("client1"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(orderRequestDto)))
                .andExpect(status().isOk())
                .andReturn();

        OrderResponseDto orderResponseDto =
                mapFromJson(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
                        OrderResponseDto.class);

        int orderId = orderResponseDto.getOrderId();

        mvc.perform(delete("/api/orders/{orderId}", orderId)
                        .with(user("client1"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mvcResult = mvc.perform(delete("/api/orders/{orderId}", orderId)
                        .with(user("client1"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andReturn();

        ErrorDto errorDto = mapFromJson(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        Assertions.assertAll(
                () -> assertEquals(errorDto.getErrors().get(0).getField(), "orderId"),
                () -> assertEquals(errorDto.getErrors().get(0).getErrorCode(), BuscompanyErrorCode.ORDER_NOT_FOUND.toString()),
                () -> assertEquals(errorDto.getErrors().get(0).getMessage(), BuscompanyErrorCode.ORDER_NOT_FOUND.getErrorString())
        );
    }

    private List<AdminTripResponseDto> addTestData() throws Exception {
        String busBaw = "Baw";
        String busFoton = "Foton";

        List<AdminTripResponseDto> tripList = new ArrayList<>();
        TripRequestDto.TripRequestDtoBuilder builder = TripRequestDto.builder();
        builder
                .busName(busBaw)
                .fromStation("1")
                .toStation("2")
                .start("12:00")
                .duration("01:00")
                .price("200")
                .dates(List.of(("2022-01-02"),
                        ("2022-02-03"),
                        ("2022-03-03")));

        tripList.add(addTripAndApprove(builder.build()));

        builder
                .busName(busBaw)
                .fromStation("1")
                .toStation("2")
                .start("12:00")
                .duration("01:00")
                .price("200")
                .dates(List.of(("2022-01-02"),
                        ("2022-02-03"),
                        ("2022-03-04")));
        tripList.add(addTrip(builder.build()));

        builder
                .busName(busBaw)
                .fromStation("1")
                .toStation("3")
                .start("12:00")
                .duration("01:00")
                .price("200")
                .dates(List.of(("2022-01-03"),
                        ("2022-02-04"),
                        ("2022-03-05")));
        tripList.add(addTrip(builder.build()));

        builder
                .busName(busBaw)
                .fromStation("2")
                .toStation("3")
                .start("12:00")
                .duration("01:00")
                .price("200")
                .dates(List.of(("2022-01-03"),
                        ("2022-02-04"),
                        ("2022-03-06")));
        tripList.add(addTrip(builder.build()));

        builder
                .busName(busFoton)
                .fromStation("1")
                .toStation("3")
                .start("12:00")
                .duration("01:00")
                .price("200")
                .dates(List.of(("2022-01-03"),
                        ("2022-02-04"),
                        ("2022-03-05")));
        tripList.add(addTripAndApprove(builder.build()));

        return tripList;
    }


    private AdminTripResponseDto addTripAndApprove(TripRequestDto requestDto) throws Exception {
        MvcResult mvcAddResult = mvc.perform(post("/api/trip")
                        .with(user("Admin").authorities(Role.ROLE_ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(requestDto)))
                .andExpect(status().isOk())
                .andReturn();

        AdminTripResponseDto responseDto =
                mapFromJson(mvcAddResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
                        AdminTripResponseDto.class);

        String tripId = responseDto.getTripId();

        MvcResult mvcResult = mvc.perform(put("/api/trips/{tripId}/approve", tripId)
                        .with(user("Admin").authorities(Role.ROLE_ADMIN)))
                .andExpect(status().isOk())
                .andReturn();

        return mapFromJson(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8), AdminTripResponseDto.class);
    }

    private AdminTripResponseDto addTrip(TripRequestDto requestDto) throws Exception {
        MvcResult mvcResult = mvc.perform(post("/api/trip")
                        .with(user("Admin").authorities(Role.ROLE_ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(requestDto)))
                .andExpect(status().isOk())
                .andReturn();

        return mapFromJson(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8), AdminTripResponseDto.class);
    }
}
