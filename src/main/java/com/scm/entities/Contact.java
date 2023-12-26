package com.scm.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
//import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name="contact")
public class Contact {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int cId;
	@NotBlank(message = "Name is required !")
	private String name;
	@NotBlank(message = "Nickname is required !")
	private String nickName;
	@NotBlank(message = "Email is required !")
	private String email;
	@NotBlank(message = "Occupation is required !")
	private String work;
	@NotBlank(message = "Phone is required !")
	private String phone;
	private String imageUrl;
	@Column(length = 5000)
	@NotBlank(message = "Description is required !")
	private String description;
	
	@ManyToOne()
	@JsonIgnore // will not serialize user
	private User user;
	
	public Contact() {
		
	}

	public int getcId() {
		return cId;
	}

	public void setcId(int cId) {
		this.cId = cId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getWork() {
		return work;
	}

	public void setWork(String work) {
		this.work = work;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

//	@Override
//	public String toString() {
//		return "Contact [cId=" + cId + ", name=" + name + ", nickName=" + nickName + ", email=" + email + ", work="
//				+ work + ", phone=" + phone + ", imageUrl=" + imageUrl + ", description=" + description + ", user="
//				+ user + "]";
//	}
	
	@Override
	public boolean equals(Object obj) {
		return this.cId == ((Contact)obj).getcId();
	}
	
	
	

}
