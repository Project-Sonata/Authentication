package com.odeyalo.sonata.authentication.controller.support;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import static org.assertj.core.api.Assertions.assertThat;

class HttpSessionDataRequestAssociationServiceTest {

    @Test
    @DisplayName("Associate the data with session and expect data to be saved in HttpSession")
    void associateDataWithSession_andExpectDataToBeSaved() {
        // given
        HttpSessionDataRequestAssociationService associationService = new HttpSessionDataRequestAssociationService();
        String key = "waifu", value = "Miku Nakano";
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        // when
        associationService.associateData(key, value, request, response);
        // then
        HttpSession session = request.getSession();

        assertThat(session)
                .as("Session must be not null!")
                .isNotNull();

        assertThat(session.getAttribute(key))
                .as("The element must be presented in session if associateData() method was called")
                .isNotNull()
                .isEqualTo(value);
    }

    @Test
    @DisplayName("Get existing value and expect this value to return")
    void getExistingValue_andExpectValueToBeReturned() {
        // given
        String key = "mood", value = "melanchole";
        HttpSessionDataRequestAssociationService associationService = new HttpSessionDataRequestAssociationService();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(key, value);

        request.setSession(session);

        Object o = associationService.get(request, key);

        assertThat(o)
                .as("If key was presented in session, then value must be not null!")
                .isNotNull()
                .as("The class must be exactly the same as was saved before")
                .isInstanceOf(String.class);

        String actualValue = (String) o;

        assertThat(actualValue)
                .as("The value must exactly equal")
                .isEqualTo(value);
    }


    @Test
    @DisplayName("Get not existing value and expect null to return")
    void getNonExistingValue_andExpectNull() {
        // given
        String key = "mood", value = "melanchole";
        HttpSessionDataRequestAssociationService associationService = new HttpSessionDataRequestAssociationService();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSession(new MockHttpSession());

        Object o = associationService.get(request, key);

        assertThat(o)
                .as("If key was not presented in session, then null must be returned!")
                .isNull();
    }
}
