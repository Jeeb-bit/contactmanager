package com.Smart.manager.Dao;




import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.Smart.manager.entities.Contact;
import com.Smart.manager.entities.User;

public interface ContactRepository extends JpaRepository<Contact,Integer>{
	@Query("from Contact as c where c.user.id=:userId")
	public Page<Contact> findContactsByUser(@Param("userId")int userId,Pageable pageable);
	public List<Contact> findByNameContainingAndUser(String Keywords,User user);
}
