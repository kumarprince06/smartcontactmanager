package com.scm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import com.scm.dao.UserRepository;
import com.scm.entities.User;

@Controller
public class HomeController {
	
	@Autowired
	private UserRepository userRepository;

	//Home Handler
	@RequestMapping("/")
	public String home(Model model) {
		model.addAttribute("title", "Home - Smart Contact Manager");
		return "home";
	}

	//About Handler
	@RequestMapping("/about")
	public String about(Model model) {
		model.addAttribute("title", "About - Smart Contact Manager");
		return "about";
	}
	
	//Contact Us Handler
	@RequestMapping("/contact")
	public String contact(Model model) {
		model.addAttribute("title", "Contact - Smart Contact Manager");
		return "contact";
	}

	//Sign In Handler
	@RequestMapping("/signin")
	public String signIn(Model model) {
		model.addAttribute("title", "Login - Smart Contact Manager");
		return "signin";
	}
	
	//Sign Up Handler
	@RequestMapping("/signup")
	public String signUp(Model model) {
		model.addAttribute("title", "SignUp - Smart Contact Manager");
		model.addAttribute("user", new User());
		return "signup";
	}


}
