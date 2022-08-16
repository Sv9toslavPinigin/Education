package net.thumbtack.buscompany.api;

import com.fasterxml.jackson.core.type.TypeReference;
import net.thumbtack.buscompany.dto.request.AdminProfileEditRequestDto;
import net.thumbtack.buscompany.dto.request.AdminRegisterRequestDto;
import net.thumbtack.buscompany.dto.request.ClientRegisterRequestDto;
import net.thumbtack.buscompany.dto.response.*;
import net.thumbtack.buscompany.exception.BuscompanyErrorCode;
import net.thumbtack.buscompany.model.Role;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Transactional
public class AdminEndpointsTest extends RestControllerTest {


    @Before()
    public void setUp() {
        super.setUp();
    }

    @Test
    public void testAdminRegister_adminRegistered() throws Exception {
        AdminRegisterRequestDto requestDto = new AdminRegisterRequestDto("имя",
                "фамилия",
                "",
                "Admin",
                "testAdmin",
                "Qwe12356"
        );

        final MvcResult mvcResult = mvc.perform(post(
                        "/api/admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(requestDto)))
                .andExpect(authenticated().withUsername("testAdmin"))
                .andExpect(status().isOk())
                .andReturn();

        AdminResponseDto responseDto =
                mapFromJson(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
                        AdminResponseDto.class);

        Assertions.assertAll(
                () -> Assertions.assertEquals(requestDto.getFirstName(), responseDto.getFirstName()),
                () -> Assertions.assertEquals(requestDto.getLastName(), responseDto.getLastName()),
                () -> Assertions.assertEquals(requestDto.getPatronymic(), responseDto.getPatronymic()),
                () -> Assertions.assertEquals(requestDto.getPosition(), responseDto.getPosition()),
                () -> assertEquals("admin", responseDto.getUserType()));
    }

    @Test
    public void testRegisterAdmin_adminNotRegister_returnErrorDto() throws Exception {
        AdminRegisterRequestDto requestDto = new AdminRegisterRequestDto("имя",
                "фамилия",
                "",
                "Admin",
                "testAdmin",
                "Qwe12356"
        );

        mvc.perform(post("/api/admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(requestDto)))
                .andExpect(authenticated().withUsername("testAdmin"))
                .andExpect(status().isOk());

        final MvcResult mvcResult = mvc.perform(post("/api/admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(requestDto)))
                .andExpect(status().is(400))
                .andReturn();

        ErrorDto errorDto =
                mapFromJson(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
                        new TypeReference<>() {
                        });

        Assert.assertTrue(errorDto.getErrors().contains(new MyError(BuscompanyErrorCode.LOGIN_ALREADY_EXISTS.toString(),
                "login", BuscompanyErrorCode.LOGIN_ALREADY_EXISTS.getErrorString())));
    }

    @Test
    public void testAdminGetClients() throws Exception {
        ClientRegisterRequestDto requestDto = ClientRegisterRequestDto.builder()
                .firstName("Имя")
                .lastName("Фамилия")
                .patronymic("")
                .email("example@mail.ru")
                .phone("89876543211")
                .login("User")
                .password("Password").build();
        mvc.perform(post("/api/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapToJson(requestDto)));

        final MvcResult mvcResult = mvc.perform(get("/api/clients")
                        .with(user("admin").password("pass").authorities(Role.ROLE_ADMIN))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<ClientResponseDto> resultList =
                mapFromJson(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
                        new TypeReference<List<ClientResponseDto>>() {
                        });

        ClientResponseDto responseDto = resultList.get(0);
        Assertions.assertAll(
                () -> Assertions.assertEquals(requestDto.getFirstName(), responseDto.getFirstName()),
                () -> Assertions.assertEquals(requestDto.getLastName(), responseDto.getLastName()),
                () -> Assertions.assertEquals(requestDto.getPatronymic(), responseDto.getPatronymic()),
                () -> Assertions.assertEquals(requestDto.getEmail(), responseDto.getEmail()),
                () -> Assertions.assertEquals(requestDto.getPhone(), responseDto.getPhone())
        );
    }

    @Test
    public void testAdminEditProfile_profileEdited() throws Exception {
        AdminRegisterRequestDto registerRequestDto = new AdminRegisterRequestDto("имя",
                "фамилия",
                "",
                "Admin",
                "AdminForEditTest",
                "OldPassword"
        );

        mvc.perform(post("/api/admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(registerRequestDto)))
                .andExpect(authenticated().withUsername("AdminForEditTest"))
                .andExpect(status().isOk());


        AdminProfileEditRequestDto requestDto = AdminProfileEditRequestDto.builder()
                .firstName("НовоеИмя")
                .lastName("НоваяФамилия")
                .patronymic("НовоеОтчество")
                .oldPassword("OldPassword")
                .newPassword("newPassword")
                .position("NewPosition").build();

        final MvcResult mvcEditResult = mvc.perform(put("/api/admins")
                        .with(user("AdminForEditTest").authorities(Role.ROLE_ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(requestDto)))
                .andReturn();

        mvc.perform(post("/api/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "\"login\": \"AdminForEditTest\",\n" +
                                "\"password\": \"newPassword\"\n" +
                                "}"))
                .andExpect(authenticated())
                .andReturn();

        mvc.perform(post("/api/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "\"login\": \"AdminForEditTest\",\n" +
                                "\"password\": \"newPassword\"\n" +
                                "}"))
                .andExpect(authenticated())
                .andReturn();

        AdminProfileEditResponseDto responseDto =
                mapFromJson(mvcEditResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
                        AdminProfileEditResponseDto.class);

        Assertions.assertAll(
                () -> Assertions.assertEquals(requestDto.getFirstName(), responseDto.getFirstName()),
                () -> Assertions.assertEquals(requestDto.getLastName(), responseDto.getLastName()),
                () -> Assertions.assertEquals(requestDto.getPatronymic(), responseDto.getPatronymic()),
                () -> Assertions.assertEquals(requestDto.getPosition(), responseDto.getPosition()));
    }

    @Test
    public void testGetBusInfo() throws Exception {
        mvc.perform(get("/api/buses")
                        .with(user("admin").password("pass").authorities(Role.ROLE_ADMIN))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void testAdminRegister_validationFail_returnErrorDto() throws Exception {
        AdminRegisterRequestDto requestDto = new AdminRegisterRequestDto(
                "123",
                "Lastname",
                "!",
                "",
                "test Admin",
                "Qwe12"
        );

        final MvcResult mvcResult = mvc.perform(post(
                        "/api/admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(requestDto)))
                .andExpect(status().is(400))
                .andReturn();

        ErrorDto responseDto = mapFromJson(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorDto.class);

        assertTrue(responseDto.getErrors().contains((new MyError("VALIDATION_FAILED", "firstName",
                "First name can only contain Russian letters , spaces and \"-\""))));
        assertTrue(responseDto.getErrors().contains(new MyError("VALIDATION_FAILED", "lastName",
                "Last name can only contain Russian letters , spaces and \"-\"")));
        assertTrue(responseDto.getErrors().contains(new MyError("VALIDATION_FAILED", "patronymic",
                "Patronymic can only contain Russian letters , spaces and \"-\"")));
        assertTrue(responseDto.getErrors().contains(new MyError("VALIDATION_FAILED", "login",
                "Login can contain only Latin and Russian letters and numbers")));
        assertTrue(responseDto.getErrors().contains(new MyError("VALIDATION_FAILED", "password",
                "Length should be more " + minPasswordLength)));
    }

    @Test
    public void testLogin_invalidCredentials_returnErrorDto() throws Exception {
        final MvcResult mvcLoginResult = mvc.perform(post("/api/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "\"login\": \"someLogin\",\n" +
                                "\"password\": \"password\"\n" +
                                "}"))
                .andExpect(status().is(400))
                .andReturn();

        ErrorDto errorDto =
                mapFromJson(mvcLoginResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
                        ErrorDto.class);

        assertEquals(errorDto.getErrors().get(0).getErrorCode(), "Unauthorized");
        assertEquals(errorDto.getErrors().get(0).getField(), "login");
        assertEquals(errorDto.getErrors().get(0).getMessage(), "Invalid login or password");
    }

    @Test
    public void testAdminProfileEdit_invalidOldPassword_returnErrorDto() throws Exception {
        AdminRegisterRequestDto registerRequestDto = new AdminRegisterRequestDto("имя",
                "фамилия",
                "",
                "Admin",
                "AdminForEditTest",
                "SomePassword"
        );

        mvc.perform(post("/api/admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(registerRequestDto)))
                .andExpect(authenticated().withUsername("AdminForEditTest"))
                .andExpect(status().isOk());

        AdminProfileEditRequestDto requestDto = AdminProfileEditRequestDto.builder()
                .firstName("НовоеИмя")
                .lastName("НоваяФамилия")
                .patronymic("НовоеОтчество")
                .oldPassword("invalidPassword")
                .newPassword("newPassword")
                .position("NewPosition").build();

        final MvcResult mvcEditResult = mvc.perform(put("/api/admins")
                        .with(user("AdminForEditTest").password("OldPassword").authorities(Role.ROLE_ADMIN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(requestDto)))
                .andExpect(status().is(400))
                .andReturn();

        ErrorDto responseDto =
                mapFromJson(mvcEditResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
                        ErrorDto.class);

        assertTrue(responseDto.getErrors().contains(new MyError(BuscompanyErrorCode.INVALID_OLD_PASSWORD.toString(),
                "oldPassword",
                BuscompanyErrorCode.INVALID_OLD_PASSWORD.getErrorString())));
    }

    @Test
    public void testAdminLogout() throws Exception {
        mvc.perform(delete("/api/sessions")
                        .with(user("testAdmin")))
                .andExpect(status().is(200));
    }

    @Test
    public void testAdminLeft_adminLeft() throws Exception {
        AdminRegisterRequestDto.AdminRegisterRequestDtoBuilder registerRequestDtoBuilder
                = AdminRegisterRequestDto.builder();
        registerRequestDtoBuilder
                .firstName("имя")
                .lastName("фамилия")
                .patronymic("")
                .position("Admin")
                .login("testAdmin1")
                .password("Qwe12356");

        mvc.perform(post("/api/admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(registerRequestDtoBuilder.build())))
                .andExpect(authenticated().withUsername("testAdmin1"))
                .andExpect(status().isOk())
                .andReturn();

        registerRequestDtoBuilder.login("testAdmin2");

        mvc.perform(post("/api/admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(registerRequestDtoBuilder.build())))
                .andExpect(authenticated().withUsername("testAdmin2"))
                .andExpect(status().isOk())
                .andReturn();

        mvc.perform(delete("/api/accounts")
                        .with(user("testAdmin2").authorities(Role.ROLE_ADMIN)))
                .andExpect(status().is(200));

        mvc.perform(post("/api/admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(registerRequestDtoBuilder.build())))
                .andExpect(status().is(400))
                .andReturn();

        mvc.perform(post("/api/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "\"login\": \"testAdmin2\",\n" +
                                "\"password\": \"Qwe12356\"\n" +
                                "}"))
                .andExpect(status().is(400))
                .andReturn();
    }

    @Test
    public void testAdminLeft_lastAdminNotLeft_returnErrorDto() throws Exception {
        AdminRegisterRequestDto.AdminRegisterRequestDtoBuilder registerRequestDtoBuilder
                = AdminRegisterRequestDto.builder();
        registerRequestDtoBuilder
                .firstName("имя")
                .lastName("фамилия")
                .patronymic("")
                .position("Admin")
                .login("testAdmin")
                .password("Qwe12356");

        mvc.perform(post("/api/admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(registerRequestDtoBuilder.build())))
                .andExpect(authenticated().withUsername("testAdmin"))
                .andExpect(status().isOk())
                .andReturn();

        MvcResult mvcResult = mvc.perform(delete("/api/accounts")
                        .with(user("testAdmin").authorities(Role.ROLE_ADMIN)))
                .andExpect(status().is(400))
                .andReturn();

        ErrorDto responseDto =
                mapFromJson(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
                        ErrorDto.class);

        assertTrue(responseDto.getErrors().contains(new MyError(BuscompanyErrorCode.LAST_ADMIN.toString(),
                "login",
                BuscompanyErrorCode.LAST_ADMIN.getErrorString())));

    }

}
