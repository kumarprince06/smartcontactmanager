package com.scm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.scm.dao.UserRepository;
import com.scm.entities.User;
import com.scm.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class FormController {

	@Autowired()
	private BCryptPasswordEncoder passwordEncode;

	@Autowired
	private UserRepository userRepository;

	@PostMapping("/do_register")
	public String userRegister(@Valid @ModelAttribute("user") User user, BindingResult result,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model,
			HttpSession session) {

		try {

			if (!agreement) {
				System.out.println("Error: You must agree to the terms and conditions.");
				throw new Exception("You must agree to the terms and conditions.");
			}

			if (result.hasErrors()) {
				model.addAttribute("user", user);
				System.out.println("Error: " + result.toString());
				return "signup";
			}

			// Check if the email already exists
			if (userRepository.findByEmail(user.getEmail()) != null) {
				model.addAttribute("user", user);
				session.setAttribute("message",
						new Message("Email already exists. Please use a different email.", "alert-danger"));
				return "signup";
			}

			user.setRole("ROLE_USER");
			user.setActiveStatus(true);
			user.setImage("default.png");
			// Password will get Encrypted here only
			user.setPassword(passwordEncode.encode(user.getPassword()));

			User result1 = this.userRepository.save(user);

			System.out.println("Agreement : " + agreement);
			System.out.println("User : " + user);

			System.out.println("Result : " + result1);

			model.addAttribute("user", new User());
			session.setAttribute("message", new Message("Successfully Registered ..!!", "alert-success"));
			return "signup";

		} catch (Exception e) {
			// TODO: handle exception
			model.addAttribute("user", user);
			session.setAttribute("message", new Message("Something went wrong..!! " + e.getMessage(), "alert-danger"));
			e.printStackTrace();
			return "signup";
		}

	}

}
