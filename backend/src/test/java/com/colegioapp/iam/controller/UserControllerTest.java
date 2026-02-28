package com.colegioapp.iam.controller;

import com.colegioapp.iam.security.JwtAuthFilter;
import com.colegioapp.iam.security.SecurityConfig;
import com.colegioapp.iam.service.JwtService;
import com.colegioapp.shared.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {UserController.class, UserControllerTest.SecuredStub.class})
@Import({SecurityConfig.class, JwtAuthFilter.class, GlobalExceptionHandler.class, UserControllerTest.SecuredStub.class})
class UserControllerTest {

    @RestController
    @RequestMapping("/api/test")
    static class SecuredStub {

        @GetMapping("/admin-only")
        @PreAuthorize("hasRole('ADMIN')")
        public String adminOnly() {
            return "ok";
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @Test
    @WithMockUser(username = "123e4567-e89b-12d3-a456-426614174000", roles = "ADMIN")
    void me_withAuthenticatedUser_returns200() throws Exception {
        mockMvc.perform(get("/api/users/me"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value("123e4567-e89b-12d3-a456-426614174000"))
            .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    @WithMockUser(roles = "DOCENTE")
    void adminOnly_withDocente_returns403() throws Exception {
        mockMvc.perform(get("/api/test/admin-only"))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.status").value(403))
            .andExpect(jsonPath("$.error").value("Forbidden"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminOnly_withAdmin_returns200() throws Exception {
        mockMvc.perform(get("/api/test/admin-only"))
            .andExpect(status().isOk());
    }
}
