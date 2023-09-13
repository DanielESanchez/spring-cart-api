package com.springapi.springapitechnicaltest;

import com.springapi.springapitechnicaltest.controllers.UserController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class SpringApiTechnicalTestApplicationTests {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	private WebApplicationContext context;

	@BeforeEach
	public void setup() {
		mockMvc = MockMvcBuilders
				.webAppContextSetup(context)
				.defaultRequest(get("/").with(user("user").roles("ADMIN")))
				.apply(springSecurity())
				.build();
	}

	@Test
	void test1() throws Exception {
		mockMvc.perform(get("/api/testing"))
				.andExpect(status().isOk());
	}

	@Test
	public void main() {
		SpringApiTechnicalTestApplication.main(new String[] {});
	}

}
