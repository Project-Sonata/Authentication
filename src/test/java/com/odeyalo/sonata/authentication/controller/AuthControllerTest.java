package com.odeyalo.sonata.authentication.controller;

import com.odeyalo.sonata.authentication.JsonTestUtils;
import com.odeyalo.sonata.authentication.common.ExtendedErrorDetails;
import com.odeyalo.sonata.authentication.common.LoginCredentials;
import com.odeyalo.sonata.authentication.common.AuthenticationResult;
import com.odeyalo.sonata.authentication.dto.ExtendedUserInfo;
import com.odeyalo.sonata.authentication.dto.request.AdvancedUserRegistrationInfo;
import com.odeyalo.sonata.authentication.dto.response.ExtendedAuthenticationResultResponse;
import com.odeyalo.sonata.authentication.dto.response.EmailConfirmationStatusResponseDto;
import com.odeyalo.sonata.authentication.entity.ConfirmationCode;
import com.odeyalo.sonata.authentication.entity.User;
import com.odeyalo.sonata.authentication.repository.*;
import com.odeyalo.sonata.authentication.support.kafka.KafkaMessageSender;
import com.odeyalo.sonata.authentication.testing.factory.UserEntityTestingFactory;
import com.odeyalo.sonata.authentication.testing.faker.ConfirmationCodeFaker;
import com.odeyalo.sonata.authentication.testing.faker.UserFaker;
import com.odeyalo.sonata.common.authentication.dto.UserInfo;
import com.odeyalo.sonata.common.authentication.dto.request.ConfirmationCodeData;
import com.odeyalo.sonata.common.authentication.dto.response.AuthenticationResultResponse;
import com.odeyalo.sonata.common.authentication.dto.response.UserRegistrationConfirmationResponseDto;
import com.odeyalo.sonata.common.shared.ApiErrorDetailsInfo;
import com.odeyalo.sonata.suite.servlet.client.UserServiceClient;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * The class contains the tests for the {@link AuthController}
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthControllerTest {

    @MockBean
    private KafkaMessageSender sender;

    @MockBean
    private UserServiceClient userServiceClient;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JpaSupportUserRepository userRepository;
    @Autowired
    private JpaConfirmationCodeRepository confirmationCodeRepository;

    @Autowired
    private AdvancedUserRegistrationInfoStore infoStore;

    @Autowired
    PasswordEncoder passwordEncoder;

    public static final String SIGNUP_ENDPOINT_NAME = "/auth/signup";
    public static final String EMAIL_CONFIRMATION_ENDPOINT_NAME = "/auth/confirm/email";
    public static final String LOGIN_ENDPOINT_NAME = "/auth/login";

    @BeforeAll
    void beforeAll() {
        Mockito.when(userServiceClient.createUser(any()))
                .thenReturn(ResponseEntity.created(URI.create("https://testing.com/user/123")).build());
    }

    @Nested
    @TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
    class RegistrationAuthControllerTests {
        public static final String ALREADY_TAKEN_EMAIL = "alreadytaken@gmail.com";
        public static final String CONFIRMATION_URL_RELATION_NAME = "confirmation_url";
        public static final String SELF_RELATION_NAME = "self";
        public static final String INVALID_PASSWORD = "invalid";

        @BeforeAll
        void setup() {
            User user = UserEntityTestingFactory.createAndModify((modifier) -> modifier.setEmail(ALREADY_TAKEN_EMAIL));
            ((UserRepository) userRepository).save(user);
        }

        /**
         * The test case tests the '/auth/signup endpoint', the test purpose is to register the user with valid JSON body
         * and expect valid response from controller.
         */
        @Test
        @DisplayName("Should register the user in the system and return HTTP 200 OK with JSON body")
        void shouldRegisterUser_andExceptHttp200() throws Exception {
            // given
            AdvancedUserRegistrationInfo registrationInfo = getValidUserRegistrationInfo();

            String requestBody = JsonTestUtils.convertToJson(registrationInfo);

            // when
            MvcResult mvcResult = mockMvc.perform(post(SIGNUP_ENDPOINT_NAME)
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andDo(print())
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andReturn();

            //then
            UserRegistrationConfirmationResponseDto responseBody = JsonTestUtils.convertToPojo(mvcResult, UserRegistrationConfirmationResponseDto.class);

            String message = responseBody.getMessage();
            assertNotNull(message, "The response must contain message for user!");

            List<Link> selfRelationsLinks = responseBody.getLinks(SELF_RELATION_NAME);
            List<Link> confirmationUrls = responseBody.getLinks(CONFIRMATION_URL_RELATION_NAME);

            assertNotNull(selfRelationsLinks, "The response must contain the 'self' relation");
            assertNotNull(confirmationUrls, "The response must contain the links relations!");

            assertNotEquals(0, selfRelationsLinks.size(), "The 'self' relation must contain at least 1 element");
            assertNotEquals(0, confirmationUrls.size(), "The relation must contain at least 1 element!");

            String email = registrationInfo.getEmail();
            User user = userRepository.findUserByEmail(email);

            assertNotNull(user, "User must be saved to DB after registration");
            assertEquals(email, user.getEmail(), "Emails must be equal!");
            assertNotNull(user.getPassword(), "Password must be encoded and saved!");
            assertNotEquals(registrationInfo.getPassword(), user.getPassword(), "Password must be encoded and MUST NOT be saved in plain text");
        }


        @Test
        @DisplayName("Register the user with invalid registration info that contains invalid email and expect HTTP 400 with INVALID_EMAIL error")
        void registerUserWithInvalidEmail_andExceptHttp400WithInvalidEmailError() throws Exception {
            // given
            AdvancedUserRegistrationInfo info = getValidUserRegistrationInfo();
            info.setEmail("invalidemail");

            long beforeRequestCount = userRepository.count();

            String requestBody = JsonTestUtils.convertToJson(info);

            // when
            MvcResult mvcResult = mockMvc.perform(post(SIGNUP_ENDPOINT_NAME)
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andDo(print())
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andReturn();
            // then
            long afterRequestCount = userRepository.count();
            ApiErrorDetailsInfo errorInfo = JsonTestUtils.convertToPojo(mvcResult, ApiErrorDetailsInfo.class);
            HttpStatus httpStatus = errorInfo.toHttpStatus();
            assertEquals(beforeRequestCount, afterRequestCount, "Count must be same if user was not saved");
            assertEquals(HttpStatus.BAD_REQUEST, httpStatus, "If the user entered the wrong registration info, then HTTP 400 must be returned");
            assertNotNull(errorInfo.getErrorDetails(), "Error details must be not null and contain the detailed info about error");
            assertEquals(errorInfo.getErrorDetails(), ExtendedErrorDetails.INVALID_EMAIL, "If email is incorrect, then invalid_email error message must be returned");

        }

        @Test
        @DisplayName("Register the user with invalid registration info that contains already used email and expect HTTP 400 with EMAIL_ALREADY_TAKEN error")
        void registerUserWithTakenEmail_andExceptHttp400WithEmailAlreadyTakenError() throws Exception {
            // given
            AdvancedUserRegistrationInfo info = getValidUserRegistrationInfo();
            info.setEmail(ALREADY_TAKEN_EMAIL);
            long beforeRequestCount = userRepository.count();

            String requestBody = JsonTestUtils.convertToJson(info);

            // when
            MvcResult mvcResult = mockMvc.perform(post(SIGNUP_ENDPOINT_NAME)
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andDo(print())
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andReturn();

            // then
            long afterRequestCount = userRepository.count();
            ApiErrorDetailsInfo errorInfo = JsonTestUtils.convertToPojo(mvcResult, ApiErrorDetailsInfo.class);
            HttpStatus httpStatus = errorInfo.toHttpStatus();

            assertEquals(beforeRequestCount, afterRequestCount, "Count must be same if user was not saved");
            assertEquals(HttpStatus.BAD_REQUEST, httpStatus, "If the user entered the wrong registration info, then HTTP 400 must be returned");
            assertNotNull(errorInfo.getErrorDetails(), "Error details must be not null and contain the detailed info about error");
            assertEquals(errorInfo.getErrorDetails(), ExtendedErrorDetails.EMAIL_ALREADY_TAKEN, "If email is already taken by other user, then email_already_taken error message must be returned");
        }

        @Test
        @DisplayName("Register the user with invalid registration info that contains invalid email and expect HTTP 400 with INVALID_PASSWORD error")
        void registerUserWithInvalidPassword_andExceptHttp400WithInvalidPasswordError() throws Exception {
            // given
            AdvancedUserRegistrationInfo info = getValidUserRegistrationInfo();
            info.setPassword(INVALID_PASSWORD);

            long beforeRequestCount = userRepository.count();

            String requestBody = JsonTestUtils.convertToJson(info);

            // when
            MvcResult mvcResult = mockMvc.perform(post(SIGNUP_ENDPOINT_NAME)
                            .content(requestBody)
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andDo(print())
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andReturn();

            // then
            long afterRequestCount = userRepository.count();
            ApiErrorDetailsInfo errorInfo = JsonTestUtils.convertToPojo(mvcResult, ApiErrorDetailsInfo.class);
            HttpStatus httpStatus = errorInfo.toHttpStatus();

            assertEquals(beforeRequestCount, afterRequestCount, "Count must be same if user was not saved");
            assertEquals(HttpStatus.BAD_REQUEST, httpStatus, "If the user entered the wrong registration info, then HTTP 400 must be returned");
            assertNotNull(errorInfo.getErrorDetails(), "Error details must be not null and contain the detailed info about error");
            assertEquals(ExtendedErrorDetails.INVALID_PASSWORD, errorInfo.getErrorDetails(), "If the password is incorrect, then invalid_password error must be returned");
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class EmailConfirmationEndpointsAuthControllerTests {

        @BeforeAll
        void beforeAll() {
            Mockito.when(userServiceClient.createUser(any()))
                    .thenReturn(ResponseEntity.created(URI.create("https://testing.com/user/123")).build());
        }

        @Test
        @DisplayName("Send valid email confirmation code and expect HTTP 200 OK with JSON body that contains info about user and authentication")
        void sendValidCode_andExpectOKWithBody() throws Exception {
            // given
            String validCodeValue = "123456";
            User user = ((UserRepository) userRepository).save(UserFaker.create().overrideId(null).makeInactive().get());

            ConfirmationCode confirmationCode = ConfirmationCodeFaker.withBody(validCodeValue).user(user).get();
            ((ConfirmationCodeRepository) confirmationCodeRepository).save(confirmationCode);
            infoStore.save(
                    AdvancedUserRegistrationInfo.builder()
                            .email(user.getEmail())
                            .birthdate(LocalDate.of(2000, 2, 3))
                            .gender("MALE")
                            .password("password")
                            .notificationEnabled(false)
                            .countryCode("JP")
                            .build());
            ExtendedUserInfo expectedUserInfo = ExtendedUserInfo.from(user);

            ConfirmationCodeData data = new ConfirmationCodeData(validCodeValue);
            String json = JsonTestUtils.convertToJson(data);
            // when
            MvcResult mvcResult = mockMvc.perform(
                            post(EMAIL_CONFIRMATION_ENDPOINT_NAME)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(json))
                    .andDo(print())
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                    .andReturn();
            // then
            Optional<User> optional = ((UserRepository) userRepository).findById(user.getId());
            EmailConfirmationStatusResponseDto responseDto = JsonTestUtils.convertToPojo(mvcResult, EmailConfirmationStatusResponseDto.class);
            ExtendedUserInfo userInfo = responseDto.getUserInfo();

            assertTrue(optional.isPresent(), "User must not be deleted after request!");
            User actualUser = optional.get();
            assertTrue(actualUser.isActive(), "The user must be activated if code was valid!");
            assertTrue(responseDto.isConfirmed(), "If the code is valid, then true must be returned in is_confirmed field");
            assertEquals(expectedUserInfo, userInfo, "The user info must be same to the info from ConfirmationCode");
            assertNotNull(responseDto.getMessage(), "Message should not be null");
        }

        @Test
        @DisplayName("Send invalid email confirmation code and expect HTTP 400 with JSON body")
        void sendInValidCode_andExpectBadRequest() throws Exception {
            // given
            String invalidCodeValue = "123456";
            ConfirmationCodeData data = new ConfirmationCodeData(invalidCodeValue);
            String json = JsonTestUtils.convertToJson(data);
            // when
            MvcResult mvcResult = mockMvc.perform(
                            post(EMAIL_CONFIRMATION_ENDPOINT_NAME)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(json))
                    .andDo(print())
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                    .andReturn();
            // then
            EmailConfirmationStatusResponseDto responseDto = JsonTestUtils.convertToPojo(mvcResult, EmailConfirmationStatusResponseDto.class);
            ExtendedUserInfo userInfo = responseDto.getUserInfo();

            assertFalse(responseDto.isConfirmed(), "If the code is invalid, then false must be returned in is_confirmed field");
            assertNotNull(responseDto.getMessage(), "Message should not be null");
            assertNull(userInfo, "User info must be null if the confirmation code is invalid");
        }
    }


    @Nested
    class LoginEndpointAuthControllerTests {


        @Test
        @DisplayName("Test login endpoint with correct credentials and expect 200 OK and JSON body as result")
        void loginEndpointWithCorrectCredentials_andExpectOkAndJson() throws Exception {
            // given
            String email = "odeyalo";
            String rawPassword = "The world has not changed the people still suffer123";
            String encodedPassword = passwordEncoder.encode(rawPassword);
            User user = UserFaker.withEmail(email).overrideId(null).makeActive().overridePassword(encodedPassword).get();

            User savedUser = ((UserRepository) userRepository).save(user);

            UserInfo expectedUserInfo = UserInfo.of(String.valueOf(savedUser.getId()), savedUser.getEmail());

            LoginCredentials credentials = LoginCredentials.of(email, rawPassword);
            String content = JsonTestUtils.convertToJson(credentials);
            // when
            MvcResult mvcResult = mockMvc.perform(post(LOGIN_ENDPOINT_NAME)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(content))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                    .andReturn();
            // then
            AuthenticationResultResponse result = JsonTestUtils.convertToPojo(mvcResult, AuthenticationResultResponse.class);
            assertTrue(result.isSuccess(), "If credentials are correct, then 'true' should be returned as result!");
            assertEquals(expectedUserInfo, result.getUserInfo(), "If user has been authenticated, then user info should be returned!");
            assertNull(result.getErrorDetails(), "If authentication is success, then null should be returned as error details");
        }

        @Test
        @DisplayName("Test login endpoint with incorrect credentials and expect 400 BAD_REQUEST and JSON body as result")
        void loginEndpointWithIncorrectCredentials_andExpectBadRequestAndJson() throws Exception {
            // given
            String email = "odeyalo";
            String rawPassword = "MikuIsTheB3st";
            LoginCredentials credentials = LoginCredentials.of(email, rawPassword);
            String content = JsonTestUtils.convertToJson(credentials);
            // when
            MvcResult mvcResult = mockMvc.perform(post(LOGIN_ENDPOINT_NAME)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(content))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                    .andReturn();
            // then
            ExtendedAuthenticationResultResponse result = JsonTestUtils.convertToPojo(mvcResult, ExtendedAuthenticationResultResponse.class);

            assertFalse(result.isSuccess(), "If credentials are incorrect, then 'false' should be returned as result!");
            assertNull(result.getUserInfo(), "If credentials are incorrect, then null should be returned as user info");
            assertEquals(AuthenticationResult.PossibleErrors.INVALID_CREDENTIALS, result.getErrorDetails(), "If the invalid credentials were provided, then AuthenticationResult.PossibleErrors.INVALID_CREDENTIALS error should be returned");
        }


        @Test
        @DisplayName("Test login endpoint with correct username but wrong password and expect 400 BAD_REQUEST and JSON body as result")
        void loginEndpointWithCorrectUsernameButWrongPassword_andExpectBadRequestAndJson() throws Exception {
            // given
            String rawCorrectPassword = "MikuIsTheB3stGirl";
            String incorrectPassword = "IdL0v3ToBeANewKurtCobain";

            String encodedPassword = passwordEncoder.encode(rawCorrectPassword);
            User user = UserFaker.create().overridePassword(encodedPassword).makeActive().get();

            LoginCredentials credentials = LoginCredentials.of(user.getEmail(), incorrectPassword);

            ((UserRepository) userRepository).save(user);

            String content = JsonTestUtils.convertToJson(credentials);
            // when
            MvcResult mvcResult = mockMvc.perform(post(LOGIN_ENDPOINT_NAME)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(content))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                    .andReturn();
            // then
            ExtendedAuthenticationResultResponse result = JsonTestUtils.convertToPojo(mvcResult, ExtendedAuthenticationResultResponse.class);

            assertFalse(result.isSuccess(), "If credentials are incorrect, then 'false' should be returned as result!");
            assertNull(result.getUserInfo(), "If credentials are incorrect, then null should be returned as user info");
            assertEquals(AuthenticationResult.PossibleErrors.INVALID_CREDENTIALS, result.getErrorDetails(), "If the invalid credentials were provided, then AuthenticationResult.PossibleErrors.INVALID_CREDENTIALS error should be returned");
        }


        @Test
        @DisplayName("Test login endpoint with correct credentials for not activated user and expect 400 BAD REQUEST and JSON body as result")
        void loginEndpointWithCorrectCredentialsButForNotActivatedUser_andExpectOkAndJson() throws Exception {
            // given
            String email = "odeyalo";
            String rawPassword = "The world has not changed the people still suffer123";
            String encodedPassword = passwordEncoder.encode(rawPassword);
            User user = UserFaker.withEmail(email).overrideId(null).makeInactive().overridePassword(encodedPassword).get();

            ((UserRepository) userRepository).save(user);

            LoginCredentials credentials = LoginCredentials.of(email, rawPassword);
            String content = JsonTestUtils.convertToJson(credentials);
            // when
            MvcResult mvcResult = mockMvc.perform(post(LOGIN_ENDPOINT_NAME)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(content))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                    .andReturn();
            // then
            ExtendedAuthenticationResultResponse result = JsonTestUtils.convertToPojo(mvcResult, ExtendedAuthenticationResultResponse.class);
            assertFalse(result.isSuccess(), "If credentials are correct, then 'false' should be returned as result!");
            assertNull(result.getUserInfo(), "If user is inactive, then null user info should be returned!");
            assertEquals(AuthenticationResult.PossibleErrors.EMAIL_CONFIRMATION_REQUIRED, result.getErrorDetails(), "If the user is activated but not activated, then AuthenticationResult.PossibleErrors.EMAIL_CONFIRMATION_REQUIRED error should be returned");
        }
    }

    @AfterEach
    void clearDB() {
        confirmationCodeRepository.deleteAll();
        userRepository.deleteAll();
    }

    private AdvancedUserRegistrationInfo getValidUserRegistrationInfo() {
        String email = "odeyalo@gmail.com";
        String password = "mysupercoolpassword123";
        LocalDate birthdate = LocalDate.of(2002, 11, 23);
        String gender = "MALE";
        boolean notificationEnabled = true;

        return AdvancedUserRegistrationInfo.builder()
                .email(email)
                .password(password)
                .birthdate(birthdate)
                .gender(gender)
                .notificationEnabled(notificationEnabled)
                .build();
    }
}
