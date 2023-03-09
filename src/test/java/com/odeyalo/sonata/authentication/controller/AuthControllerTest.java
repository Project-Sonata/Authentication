package com.odeyalo.sonata.authentication.controller;

import com.odeyalo.sonata.authentication.JsonTestUtils;
import com.odeyalo.sonata.authentication.common.ErrorDetails;
import com.odeyalo.sonata.authentication.dto.error.ApiErrorDetailsInfo;
import com.odeyalo.sonata.authentication.dto.request.ConfirmationCodeData;
import com.odeyalo.sonata.authentication.dto.request.UserRegistrationInfo;
import com.odeyalo.sonata.authentication.dto.response.TokensResponse;
import com.odeyalo.sonata.authentication.dto.response.UserRegistrationConfirmationResponseDto;
import com.odeyalo.sonata.authentication.entity.User;
import com.odeyalo.sonata.authentication.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * The class contains the tests for the {@link AuthController}
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    public static final String SIGNUP_ENDPOINT_NAME = "/auth/signup";
    public static final String EMAIL_CONFIRMATION_ENDPOINT_NAME = "/auth/confirm/email";

    @Nested
    class RegistrationAuthControllerTests {
        public static final String ALREADY_TAKEN_EMAIL = "alreadytaken@gmail.com";
        public static final String CONFIRMATION_URL_RELATION_NAME = "confirmation_url";
        public static final String SELF_RELATION_NAME = "self";
        public static final String INVALID_PASSWORD = "invalid";

        /**
         * The test case tests the '/auth/signup endpoint', the test purpose is to register the user with valid JSON body
         * and expect valid response from controller.
         */
        @Test
        @DisplayName("Should register the user in the system and return HTTP 200 OK with JSON body")
        void shouldRegisterUser_andExceptHttp200() throws Exception {
            // given
            UserRegistrationInfo registrationInfo = getValidUserRegistrationInfo();

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
            UserRegistrationInfo info = getValidUserRegistrationInfo();
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
            assertEquals(HttpStatus.BAD_REQUEST,httpStatus, "If the user entered the wrong registration info, then HTTP 400 must be returned");
            assertNotNull(errorInfo.getErrorDetails(), "Error details must be not null and contain the detailed info about error");
            assertEquals(errorInfo.getErrorDetails(), ErrorDetails.INVALID_EMAIL, "If email is incorrect, then invalid_email error message must be returned");

        }

        @Test
        @DisplayName("Register the user with invalid registration info that contains already used email and expect HTTP 400 with EMAIL_ALREADY_TAKEN error")
        void registerUserWithTakenEmail_andExceptHttp400WithEmailAlreadyTakenError() throws Exception {
            // given
            UserRegistrationInfo info = getValidUserRegistrationInfo();
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
            assertEquals(HttpStatus.BAD_REQUEST,httpStatus, "If the user entered the wrong registration info, then HTTP 400 must be returned");
            assertNotNull(errorInfo.getErrorDetails(), "Error details must be not null and contain the detailed info about error");
            assertEquals(errorInfo.getErrorDetails(), ErrorDetails.EMAIL_ALREADY_TAKEN, "If email is already taken by other user, then email_already_taken error message must be returned");
        }

        @Test
        @DisplayName("Register the user with invalid registration info that contains invalid email and expect HTTP 400 with INVALID_PASSWORD error")
        void registerUserWithInvalidPassword_andExceptHttp400WithInvalidPasswordError() throws Exception {
            // given
            UserRegistrationInfo info = getValidUserRegistrationInfo();
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
            assertEquals(HttpStatus.BAD_REQUEST,httpStatus, "If the user entered the wrong registration info, then HTTP 400 must be returned");
            assertNotNull(errorInfo.getErrorDetails(), "Error details must be not null and contain the detailed info about error");
            assertEquals(errorInfo.getErrorDetails(), ErrorDetails.INVALID_PASSWORD, "If the password is incorrect, then invalid_password error must be returned");
        }
    }

    @Nested
    class EmailConfirmationEndpointsAuthControllerTests {

        @Test
        @DisplayName("Send valid email confirmation code and expect HTTP 200 OK with JSON body that contains access and refresh tokens")
        void sendValidCode_andExpectOKWithTokens() throws Exception {
            ConfirmationCodeData data = new ConfirmationCodeData("123456");
            String json = JsonTestUtils.convertToJson(data);

            MvcResult mvcResult = mockMvc.perform(
                            post(EMAIL_CONFIRMATION_ENDPOINT_NAME)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(json))
                    .andDo(print())
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                    .andReturn();

            TokensResponse tokensResponse = JsonTestUtils.convertToPojo(mvcResult, TokensResponse.class);

            TokensResponse.Tokens tokens = tokensResponse.getTokens();

            assertNotNull(tokens, "Response MUST NOT BE NULL and contain access and refresh tokens");

            TokensResponse.Token accessToken = tokens.getAccessToken();
            TokensResponse.Token refreshToken = tokens.getRefreshToken();

            assertNotNull(accessToken, "Access token must be presented in response!");
            assertNotNull(refreshToken, "Refresh token must be presented in response!");

            assertNotNull(accessToken.getBody(), "Body of the access token must be not null and must contain token!");
            assertNotNull(accessToken.getBody(), "Body of the refresh token must be not null and must contain token!");

            assertTrue(accessToken.getExpiresIn() > 0, "Access token expire time must be greater than 0!");
            assertTrue(refreshToken.getExpiresIn() > 0, "Refresh token expire time must be greater than 0!");
            assertTrue(refreshToken.getExpiresIn() > accessToken.getExpiresIn(), "The expire time of the refresh token must be greater than the expire time of the access token");
        }
    }

    private UserRegistrationInfo getValidUserRegistrationInfo() {
        String email = "odeyalo@gmail.com";
        String password = "mysupercoolpassword123";
        LocalDate birthdate = LocalDate.of(2002, 11, 23);
        String gender = "MALE";
        boolean notificationEnabled = true;

        return UserRegistrationInfo.builder()
                .email(email)
                .password(password)
                .birthdate(birthdate)
                .gender(gender)
                .notificationEnabled(notificationEnabled)
                .build();
    }
}
