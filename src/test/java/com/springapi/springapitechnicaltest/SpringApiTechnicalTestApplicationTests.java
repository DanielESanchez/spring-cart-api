package com.springapi.springapitechnicaltest;

import com.springapi.springapitechnicaltest.configuration.SecurityConfiguration;
import com.springapi.springapitechnicaltest.services.JwtServiceImpl;

import com.springapi.springapitechnicaltest.services.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;


import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfiguration.class)
class SpringApiTechnicalTestApplicationTests {



	@Autowired
	MockMvc mockMvc;

	@Autowired
	private WebApplicationContext context;

	@MockBean
	JwtServiceImpl jwtService;

	@MockBean
	UserServiceImpl userService;

//	@Mock
//	private UserDetailsService userDetailsService;


	@BeforeEach
	public void setup() {
		mockMvc = MockMvcBuilders
				.webAppContextSetup(context)
				.apply(springSecurity())
				.build();
	}

	@Test
	//@WithMockUser(username = "asdas", roles = { "ADMIN" })
	void test1() throws Exception {

		mockMvc.perform(get("/api/v1/products"))
				.andExpect(status().isOk());
	}

}
