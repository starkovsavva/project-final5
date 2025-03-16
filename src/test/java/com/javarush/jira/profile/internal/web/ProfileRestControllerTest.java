package com.javarush.jira.profile.internal.web;

import com.javarush.jira.AbstractControllerTest;
import com.javarush.jira.common.BaseHandler;
import com.javarush.jira.common.util.JsonUtil;
import com.javarush.jira.profile.ProfileTo;
import com.javarush.jira.profile.internal.ProfileMapper;
import com.javarush.jira.profile.internal.ProfileRepository;
import com.javarush.jira.profile.internal.model.Profile;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.javarush.jira.login.internal.web.UserTestData.*;
import static com.javarush.jira.profile.internal.web.ProfileTestData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class ProfileRestControllerTest extends AbstractControllerTest {

    private static final String REST_URL_PROFILE = BaseHandler.REST_URL + "/profile";



    @Autowired
    private ProfileRepository profileRepository;

    @Test
    @WithUserDetails(value = USER_MAIL)
    void getWithProfileCorrectDetails_andExpectSuccess() throws Exception {
        ProfileTo profileTest = USER_PROFILE_TO;
        profileTest.setId(USER_ID);
        perform(MockMvcRequestBuilders.get(REST_URL_PROFILE))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(PROFILE_TO_MATCHER.contentJson(profileTest));
    }



    @Test
    @WithUserDetails(GUEST_MAIL)
    void getWithGuestDetails_andExpectThatOk() throws Exception {
        ProfileTo profileTest = GUEST_PROFILE_EMPTY_TO;
        profileTest.setId(GUEST_ID);
        perform(MockMvcRequestBuilders.get(REST_URL_PROFILE))
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(PROFILE_TO_MATCHER.contentJson(GUEST_PROFILE_EMPTY_TO));
        ;



    }

    @Test
    void getWithInvalidDetails_andExpectThatOk() throws Exception {
        ProfileTo profileTest = ProfileTestData.getInvalidTo();
        profileTest.setId(NOT_FOUND);
        perform(MockMvcRequestBuilders.get(REST_URL_PROFILE).content(profileTest.toString()))
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
        ;
    }



    // put

    @Test
    @WithUserDetails(value = USER_MAIL)
    void updateProfileValidDetails_andExpectOk() throws Exception {
        perform(MockMvcRequestBuilders.put(REST_URL_PROFILE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(ProfileTestData.getUpdatedTo())))
                .andDo(print())
                .andExpect(status().isNoContent());

        Profile dbProfile = profileRepository.getExisted(USER_ID);
        Profile updatedProfileBefore = ProfileTestData.getUpdated(USER_ID);

        PROFILE_MATCHER.assertMatch(dbProfile, updatedProfileBefore);
    }


    @Test
    @WithUserDetails(value = USER_MAIL)
    void updateProfileInvalidProfile_andExpectIsUprocessableEntity() throws Exception {
        perform(MockMvcRequestBuilders.put(REST_URL_PROFILE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(ProfileTestData.getInvalidTo())))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }



    @Test
    @WithUserDetails(value = USER_MAIL)
    void updateProfileInvalidDetailsThatIsUnknownContact() throws Exception {
        perform(MockMvcRequestBuilders.put(REST_URL_PROFILE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(ProfileTestData.getWithUnknownContactTo())))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }
    @Test
    @WithUserDetails(value = USER_MAIL)
    void updateProfileInvalidDetailsThatIsHtmlUnsafeTest() throws Exception {
        perform(MockMvcRequestBuilders.put(REST_URL_PROFILE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(ProfileTestData.getWithContactHtmlUnsafeTo())))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }



}