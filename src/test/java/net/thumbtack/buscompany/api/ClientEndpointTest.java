package net.thumbtack.buscompany.api;

import com.fasterxml.jackson.core.type.TypeReference;
import net.thumbtack.buscompany.dto.request.ClientProfileEditRequestDto;
import net.thumbtack.buscompany.dto.request.ClientRegisterRequestDto;
import net.thumbtack.buscompany.dto.response.ClientProfileEditResponseDto;
import net.thumbtack.buscompany.dto.response.ClientResponseDto;
import net.thumbtack.buscompany.dto.response.ErrorDto;
import net.thumbtack.buscompany.dto.response.MyError;
import net.thumbtack.buscompany.exception.BuscompanyErrorCode;
import net.thumbtack.buscompany.model.Client;
import net.thumbtack.buscompany.model.Role;
import net.thumbtack.buscompany.repository.iface.ClientRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;


import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Transactional
public class ClientEndpointTest extends RestControllerTest {


    @Before
    public void setUp() {
        super.setUp();
    }

    @Test
    public void testRegisterClient_clientRegistered() throws Exception {
        ClientRegisterRequestDto requestDto = ClientRegisterRequestDto.builder()
                .firstName("Имя")
                .lastName("Фамилия")
                .patronymic("")
                .email("example@mail.ru")
                .phone("89876543211")
                .login("testClient")
                .password("Password").build();

        final MvcResult mvcResult = mvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(requestDto)))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andReturn();

        ClientResponseDto responseDto =
                mapFromJson(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
                        ClientResponseDto.class);

        Assertions.assertAll(
                () -> Assertions.assertEquals(requestDto.getFirstName(), responseDto.getFirstName()),
                () -> Assertions.assertEquals(requestDto.getLastName(), responseDto.getLastName()),
                () -> Assertions.assertEquals(requestDto.getPatronymic(), responseDto.getPatronymic()),
                () -> Assertions.assertEquals(requestDto.getEmail(), responseDto.getEmail()),
                () -> Assertions.assertEquals(requestDto.getPhone(), responseDto.getPhone()),
                () -> Assertions.assertEquals("client", responseDto.getUserType()));
    }

    @Test
    public void testRegisterClient_clientNotRegister_returnErrorDto() throws Exception {
        ClientRegisterRequestDto requestDto = ClientRegisterRequestDto.builder()
                .firstName("Имя")
                .lastName("Фамилия")
                .patronymic("")
                .email("example@mail.ru")
                .phone("89876543211")
                .login("testClient")
                .password("Password").build();

        mvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(requestDto)))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andReturn();

        final MvcResult mvcResult = mvc.perform(post("/api/clients")
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
    public void testEditClientProfile_profileEdited() throws Exception {
        ClientRegisterRequestDto registerRequestDto = ClientRegisterRequestDto.builder()
                .firstName("Имя")
                .lastName("Фамилия")
                .patronymic("")
                .email("example@mail.ru")
                .phone("89876543211")
                .login("ClientForEditTest")
                .password("OldPassword").build();

        mvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(registerRequestDto)))
                .andExpect(authenticated().withUsername("ClientForEditTest"))
                .andExpect(status().isOk());

        ClientProfileEditRequestDto requestDto = ClientProfileEditRequestDto.builder()
                .firstName("НовоеИмя")
                .lastName("НоваяФамилия")
                .patronymic("НовоеОтчество")
                .mail("newmail@example.com")
                .phone("89998765432")
                .oldPassword("OldPassword")
                .newPassword("newPassword")
                .build();

        final MvcResult mvcEditResult = mvc.perform(put("/api/clients")
                        .with(user("ClientForEditTest").authorities(Role.ROLE_CLIENT).password("OldPassword"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(requestDto)))
                .andExpect(status().is(200))
                .andReturn();

        mvc.perform(post("/api/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "\"login\": \"ClientForEditTest\",\n" +
                                "\"password\": \"newPassword\"\n" +
                                "}"))
                .andExpect(authenticated())
                .andReturn();

        ClientProfileEditResponseDto responseDto =
                mapFromJson(mvcEditResult.getResponse().getContentAsString(StandardCharsets.UTF_8),
                        ClientProfileEditResponseDto.class);

        Assertions.assertAll(
                () -> Assertions.assertEquals(requestDto.getFirstName(), responseDto.getFirstName()),
                () -> Assertions.assertEquals(requestDto.getLastName(), responseDto.getLastName()),
                () -> Assertions.assertEquals(requestDto.getPatronymic(), responseDto.getPatronymic()),
                () -> Assertions.assertEquals(requestDto.getPhone(), responseDto.getPhone()),
                () -> Assertions.assertEquals(requestDto.getMail(), responseDto.getEmail()),
                () -> Assertions.assertEquals("client", responseDto.getUserType()));

    }

    @Test
    public void testClientRegister_validationFail_returnErrorDto() throws Exception {
        ClientRegisterRequestDto requestDto = new ClientRegisterRequestDto(
                "123",
                null,
                "!",
                "notmail",
                "55-55-55",
                "Qwe 12",
                "pass"
        );

        final MvcResult mvcResult = mvc.perform(post(
                        "/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(requestDto)))
                .andExpect(status().is(400))
                .andReturn();

        ErrorDto responseDto = mapFromJson(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8), ErrorDto.class);

        assertTrue(responseDto.getErrors().contains((new MyError("VALIDATION_FAILED", "firstName",
                "First name can only contain Russian letters , spaces and \"-\""))));
        assertTrue(responseDto.getErrors().contains(new MyError("VALIDATION_FAILED", "lastName",
                "must not be empty")));
        assertTrue(responseDto.getErrors().contains(new MyError("VALIDATION_FAILED", "patronymic",
                "Patronymic can only contain Russian letters , spaces and \"-\"")));
        assertTrue(responseDto.getErrors().contains(new MyError("VALIDATION_FAILED", "login",
                "Login can contain only Latin and Russian letters and numbers")));
        assertTrue(responseDto.getErrors().contains(new MyError("VALIDATION_FAILED", "password",
                "Length should be more " + minPasswordLength)));
        assertTrue(responseDto.getErrors().contains(new MyError("VALIDATION_FAILED", "email",
                "must be a well-formed email address")));
        assertTrue(responseDto.getErrors().contains(new MyError("VALIDATION_FAILED", "phone",
                "Should be mobile number of Russian operators")));
    }

    @Test
    public void testClientProfileEdit_profileNotEdit_returnErrorDto() throws Exception {
        ClientRegisterRequestDto registerRequestDto = ClientRegisterRequestDto.builder()
                .firstName("Имя")
                .lastName("Фамилия")
                .patronymic("")
                .email("example@mail.ru")
                .phone("89876543211")
                .login("ClientForEditTest")
                .password("Password").build();

        mvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(registerRequestDto)))
                .andExpect(authenticated().withUsername("ClientForEditTest"))
                .andExpect(status().isOk());

        ClientProfileEditRequestDto requestDto = ClientProfileEditRequestDto.builder()
                .firstName("НовоеИмя")
                .lastName("НоваяФамилия")
                .patronymic("НовоеОтчество")
                .mail("newmail@example.com")
                .phone("89998765432")
                .oldPassword("invalidPassword")
                .newPassword("newPassword")
                .build();

        final MvcResult mvcEditResult = mvc.perform(put("/api/clients")
                        .with(user("ClientForEditTest").authorities(Role.ROLE_CLIENT).password("OldPassword"))
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

}
