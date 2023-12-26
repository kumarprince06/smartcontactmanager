package com.scm.dao;

import java.util.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.scm.entities.Contact;
import com.scm.entities.User;


public interface ContactRepository extends JpaRepository<Contact, Integer>{
	
	//Pagination Method
	@Query("from Contact as c where c.user.userId =:userId")
	public Page<Contact> findContactByUser(@Param("userId") int userId, Pageable pageable);
	//Pageable object contain two page : current page and record per page

	//Search Method
	public List<Contact> findByNameContainingAndUser(String name, User user);

}
