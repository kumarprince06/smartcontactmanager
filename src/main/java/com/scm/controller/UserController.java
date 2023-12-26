package com.scm.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Arrays;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.scm.dao.ContactRepository;
import com.scm.dao.UserRepository;
import com.scm.entities.Contact;
import com.scm.entities.User;
import com.scm.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContactRepository contactRepository;

	// Common Data Method
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		String username = principal.getName();

		System.out.println("Username: " + username);
		User user = this.userRepository.getUserByUserName(username);
		System.out.println("User: " + user);

		model.addAttribute(user);
	}

	/* User DashBoard Hander */
	@RequestMapping("/index")
	public String dashboeard(Model model, Principal principal) {

		model.addAttribute("title", "Dashboard - Smart Contact Manager");

		return "normal/userDashboard";
	}

	/* Add Contact Form Handler */
	@GetMapping("/addContact")
	public String addContactForm(Model model) {
		model.addAttribute("title", "Add Contact - Smart Contact Manager");
		model.addAttribute("contact", new Contact());
		return "normal/addContact";
	}

	/*
	 * New Contact Save Handler
	 */
	@PostMapping("/processContact")
	public String newContactAdd(@Valid @ModelAttribute("contact") Contact contact, BindingResult result,
			@RequestParam("profileImage") MultipartFile profileImage, Principal principal, Model model,
			HttpSession session) {

		try {
			if (result.hasErrors()) {
				model.addAttribute("contact", contact);
				System.out.println("Error: " + result.toString());
				return "/normal/addContact";
			}
			// To add contact to the login user,
			// step 1: get the user first
			// step 2: add contact to user contact by calling user getcontact method
			// Set the user to add the contact in the logged in user list
			// Step 3: Save the contact to the user contact list
			String name = principal.getName();
			User user = this.userRepository.getUserByUserName(name);

			if (profileImage.isEmpty()) {
				// If true, return message
				contact.setImageUrl("default.png");
				System.out.println("Default name added for the image");

			} else {
				// If false, upload the image
				String fileName = profileImage.getOriginalFilename();
				String newFileName = generateUniqueFileName(fileName); // Method to generate unique file name
				contact.setImageUrl(newFileName);
				File file = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(file.getAbsolutePath() + file.separator + newFileName);
				Files.copy(profileImage.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("Image Uploaded..!!");

			}

			contact.setUser(user);
			user.getContact().add(contact);

			this.contactRepository.save(contact);
			model.addAttribute("title", "Add Contact - Smart Contact Manager");
			System.out.println(contact);
			System.out.println("Contact Saved.");

			session.setAttribute("message", new Message("New Contact Added ..!! Add more Contact.", "alert-success"));
			// session.removeAttribute("message");
			return "redirect:/user/addContact";

		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
			model.addAttribute("title", "Add Contact - Smart Contact Manager");
			session.setAttribute("message", new Message("Something went wrong..!!", "alert-danger"));
			// session.removeAttribute("message");
			return "redirect:/user/addContact";
		}
	}

	// Show Contact Handler
	// Per page = 7[n]
	// current page =0[page]
	@GetMapping("/showContact/{page}")
	public String viewContactS(@PathVariable("page") Integer page, Model model, Principal principal) {
		model.addAttribute("title", "Contacts - Smart Contact Manager");
		// Sending Contact list to the view
		/*
		 * This is can be done for small data but not for pagination type of thing
		 * String userName = principal.getName(); User user =
		 * this.userRepository.getUserByUserName(userName); List<Contact> list =
		 * user.getContact();
		 */

		String username = principal.getName();
		User user = this.userRepository.getUserByUserName(username);
		Pageable pageable = PageRequest.of(page, 6);
		Page<Contact> contacts = this.contactRepository.findContactByUser(user.getUserId(), pageable);

		model.addAttribute("contacts", contacts);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contacts.getTotalPages());

		return "/normal/showContact";
	}

	/* Showing Single COntact Details */
	@GetMapping("/{cId}/contact")
	public String showSingleContactDetails(@PathVariable("cId") Integer cId, Model model, Principal principal) {

		System.out.println("CId: " + cId);
		Optional<Contact> contactOptional = this.contactRepository.findById(cId);
		Contact contact = contactOptional.get();
		model.addAttribute("title", "Contact Detail - Smart Contact Manager");

		String username = principal.getName();
		User user = this.userRepository.getUserByUserName(username);
		int userId = user.getUserId();
		if (userId == contact.getUser().getUserId()) {
			model.addAttribute("title", contact.getName());
			model.addAttribute("contact", contact);
		}
		return "/normal/singleContact";
	}

	/* Deleting COntact Handler */
	@GetMapping("{cId}/deleteContact")
	/* Delete Contact Handler */
	public String deleteContact(@PathVariable("cId") Integer cId, Principal principal, Model model,
			HttpSession session) {

		Optional<Contact> contactOptional = this.contactRepository.findById(cId);
		Contact contact = contactOptional.get();
		// CHeck
		// String username = principal.getName();
		User user = this.userRepository.getUserByUserName(principal.getName());
		int userId = user.getUserId();
		if (userId == contact.getUser().getUserId()) {
			// contact.setUser(null);

			// To delete image from local directory
			String imageUrl = contact.getImageUrl();
			if (imageUrl != null && !imageUrl.isEmpty() && !imageUrl.equals("default.png")) {
				// Replace backslashes with forward slashes for Windows compatibility
				try {
					File path1 = new ClassPathResource("static/img").getFile();
					System.out.println("Image name: " + contact.getImageUrl());
					File file1 = new File(path1, contact.getImageUrl());
					file1.delete();
					System.out.println("Old Image Deleted..!" + contact.getImageUrl());
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}

			}
			user.getContact().remove(contact);
			this.userRepository.save(user);
			session.setAttribute("message", new Message("Contact deleted successfully..!!", "alert-success"));
		} else {
			session.setAttribute("message", new Message("Something went wrong..!!", "alert-danger"));
		}

		return "redirect:/user/showContact/0";
	}

	/* Update COntact Handler */
	@PostMapping("/{cId}/update")
	public String updateContact(@PathVariable("cId") Integer cId, Principal principal, Model model,
			HttpSession session) {

		Contact contact = this.contactRepository.findById(cId).get();
		model.addAttribute("contact", contact);
		model.addAttribute("title", "Update Contact - Smart Contact Manager");
		return "/normal/updateContact";
	}

	/* Process Update Handler */
	@PostMapping("/processUpdate")
	public String processUpdate(@Valid @ModelAttribute("contact") Contact contact,
			@RequestParam("profileImage") MultipartFile profileImage, BindingResult result, Principal principal,
			Model model, HttpSession session) {

		try {

			Optional<Contact> contactOptional = this.contactRepository.findById(contact.getcId());
			Contact oldContact = contactOptional.get();
			if (result.hasErrors()) {
				model.addAttribute("contact", contact);
				System.out.println("Error: " + result.toString());
				return "/normal/updateContact";
			}

			// Debugging statements
			System.out.println("Found contact with cId: " + oldContact.getcId());
			System.out.println("Contact name: " + oldContact.getName());

			// Upload FIle Processing
			if (!profileImage.isEmpty()) {

				// Delete Old File
				// Delete Old File if it's not the default image
				if (!oldContact.getImageUrl().equals("default.png")) {
					File path1 = new ClassPathResource("static/img").getFile();
					System.out.println("Image name: " + oldContact.getImageUrl());
					File file1 = new File(path1, oldContact.getImageUrl());
					file1.delete();
					System.out.println("Old Image Deleted..!" + oldContact.getImageUrl());
				}

				// Upload New File
				String fileName = profileImage.getOriginalFilename();
				String newFileName = generateUniqueFileName(fileName); // Method to generate unique file name
				contact.setImageUrl(newFileName);
				File file = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(file.getAbsolutePath() + file.separator + newFileName);
				Files.copy(profileImage.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("Image Uploaded..!!");

			} else {

				contact.setImageUrl(oldContact.getImageUrl());

			}

			User user = this.userRepository.getUserByUserName(principal.getName());

			contact.setUser(user);

			this.contactRepository.save(contact);
			System.out.println(contact.getName());
			System.out.println(contact.getcId());
			// System.out.println(contact);
			System.out.println("Contact Updated.");
			session.setAttribute("message", new Message("Contact Updated ..!!", "alert-success"));
			model.addAttribute("title", contact.getName());
			model.addAttribute(contact);

			return "redirect:/user/" + contact.getcId() + "/contact";
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error: " + e.getMessage());
			session.setAttribute("message", new Message("Something went wrong..!!", "alert-danger"));
			// session.removeAttribute("message");
			model.addAttribute(contact);
			return "redirect:/user/" + contact.getcId() + "/contact";
		}

	}

	/*
	 * Profile Handler
	 * 
	 */
	@RequestMapping("/profile")
	public String profileHandler(Model model) {

		model.addAttribute("title", "Profile");
		return "/normal/profile";
	}

	@RequestMapping("/updateProfile")
	public String profileUpdate(Model model, Principal principal) {
		// String email = principal.getName();
		// User user = this.userRepository.getUserByUserName(email);
		// System.out.println(user.getUserId());
		// System.out.println(user.getUserName());

		model.addAttribute("title", "Update Profile");
		return "/normal/updateProfile";
	}

	/*
	 * Process Profile Update Handler
	 * 
	 */
//	@PostMapping("/processProfile")
//	public String processProfileUpdate(@Valid @ModelAttribute("user") User user,
//			@RequestParam("image") MultipartFile imageFile, BindingResult result, Model model, HttpSession session,
//			Principal principal) {
//
//		return "";
//	}

//	Change Password Handler
	@GetMapping("/changePassword")
	public String changePassword(Model model) {

		model.addAttribute("title", "Change Password");
		return "normal/changePassword";
	}

	// Chnage Password Setting Handler
	@PostMapping("/changeSetting")
	public String changePasswordSetting(@RequestParam("oldPassword") String oldPassword,
			@RequestParam("newPassword") String newPassword, Model model, Principal principal, HttpSession session) {
		
		//Form Password
		System.out.println("Old Password: "+oldPassword);
		System.out.println("New Password: "+newPassword);
		
		// Database Password
//		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(principal.getName());
		
		String storePassword = user.getPassword();
		
		System.out.println("Database Password: "+storePassword);
		
		if (this.bCryptPasswordEncoder.matches(oldPassword, storePassword)) {
			//change the password
			user.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
			this.userRepository.save(user);
			
			session.setAttribute("message", new Message("Password successfully changed..!", "alert-success"));
			return "redirect:/user/index";
		}else {
			//error 
			
			model.addAttribute("title", "Change Password");
			session.setAttribute("message", new Message("Wrong Old Password..!", "alert-danger"));
			return "/normal/changePassword"; 
			
		}
		
//		model.addAttribute("title", "DashBoard");
		
	}

	// Method to generate unique name of the user

	private String generateUniqueFileName(String originalFileName) {
		// Remove spaces and replace with underscore or hyphen
		String sanitizedFileName = originalFileName.replaceAll("\\s", "_"); // or "-"

		String[] fileNameParts = sanitizedFileName.split("\\.");
		String extension = fileNameParts[fileNameParts.length - 1];
		String baseName = String.join(".", Arrays.copyOf(fileNameParts, fileNameParts.length - 1));

		// Append a unique identifier (timestamp) to the filename
		String uniqueFileName = baseName + "_" + System.currentTimeMillis() + "." + extension;

		return uniqueFileName;
	}

}
