package com.scm.controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.scm.dao.UserRepository;
import com.scm.entities.User;
import com.scm.helper.Message;
import com.scm.services.EmailService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ForgotPassword {

	Random random = new Random(1000);

	// Injecting Email Service for OTP Verification

	@Autowired
	private EmailService emailService;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	// Forgot email Handler
	@GetMapping("/forgotPassword")
	public String forgotPassword(Model model) {

		model.addAttribute("title", "Forgot Password");
		return "forgotPassword";
	}

	@PostMapping("/forgot")
	public String sendOTP(@RequestParam("email") String email, Model model, HttpSession session) {
		System.out.println("Email: " + email);

		// Generating 4 digit number for otp verification
		User user = this.userRepository.getUserByUserName(email);

		if (user == null) {
			session.setAttribute("message", new Message("No User with this email exist..!!", "alert-danger"));
			model.addAttribute("title", "Forgot Password");
			return "forgotPassword";
		}

		int otp = random.nextInt(9999);

		// Sending Otp to the email

		System.out.println("OTP: " + otp);
		String subject = "OTP from SCM";
		String message = "<h1>OTP : " + otp + "</h1>";
		String to = email;

		boolean flag = this.emailService.sendEmail(subject, message, to);

		if (flag) {

			session.setAttribute("myotp", otp);
			session.setAttribute("email", email);

			session.setAttribute("message", new Message("Check Your Email Id..!!", "alter-success"));
			model.addAttribute("title", "Verify OTP");
			return "verifyOTP";

		} else {
			session.setAttribute("message", new Message("Provide a valid mail id..!", "alert-danger"));
			model.addAttribute("title", "Forgot Password");
			return "forgotPassword";
		}

	}

	// Verify OTP
	@PostMapping("/verifyOtp")
	public String verifyOtp(@RequestParam("otp") int otp, HttpSession session, Model model) {

		int myOtp = (int) session.getAttribute("myotp");
		String email = (String) session.getAttribute("email");

		if (myOtp == otp) {

			User user = this.userRepository.getUserByUserName(email);

			if (user == null) {
				// Send Error message
				session.setAttribute("message", new Message("Provide a valid mail id..!", "alert-danger"));
				model.addAttribute("title", "Forgot Password..!!");
				return "forgotPassword";
			} else {
				// change password form
				model.addAttribute("title", "New Password");
				return "changePasswordForm";
			}

		} else {
			session.setAttribute("message", new Message("Wrong Otp..!! Please Enter correct Otp.", "alert-danger"));
			return "verifyOTP";
		}

	}

	//Handler for new Password chnage process
	@PostMapping("/newPassowrd")
	public String changeNewPassword(@RequestParam("password") String password,
			@RequestParam("password1") String newPassword, Model model, HttpSession session) {

		if (!password.equals(newPassword)) {
			model.addAttribute("title", "New Password");
			return "changePasswordForm";
		}else {
			String email = (String) session.getAttribute("email");
			User user = this.userRepository.getUserByUserName(email);
			
			user.setPassword(this.bCryptPasswordEncoder.encode(password));
			
			this.userRepository.save(user);
			
			return "redirect:/signin?change=Password change successfully..!!";
		}

		
	}

}
