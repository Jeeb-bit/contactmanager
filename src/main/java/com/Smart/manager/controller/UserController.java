package com.Smart.manager.controller;



import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.Smart.manager.Dao.ContactRepository;

import com.Smart.manager.Dao.UserRepository;
import com.Smart.manager.entities.Contact;
import com.Smart.manager.entities.User;
import com.Smart.manager.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	 private UserRepository repo;
	@Autowired
	private ContactRepository contactrepo;
	@ModelAttribute
	public void Addcommandata(Model m ,Principal principal) {
		String Username=principal.getName();
		 User user=repo.getUserbyUserName(Username);
		 m.addAttribute("user",user);
		
	}
	@GetMapping("/search/{query}")
	public ResponseEntity<?> searchhandler(@PathVariable("query") String query,Principal p)
	{
		User user=this.repo.getUserbyUserName(p.getName());

List<Contact> contacts=this.contactrepo.findByNameContainingAndUser(query, user);
		return ResponseEntity.ok(contacts);
	}
	@RequestMapping("/index")
	public String dashboard()
	
	{
		
		return"normal/Dashboard";
	}
	//add contact
	@GetMapping("/Add_contact")
	public  String addcontact() {
		return"normal/Add_contact";
	}
	@PostMapping("/Contact_process")
public String process(@ModelAttribute("contact") Contact contact,
		@RequestParam("imagefile") MultipartFile file,
		 Principal principal,HttpSession session) 

	{ 
		try {
		
	String Name=principal.getName();
	
	
	User user=this.repo.getUserbyUserName(Name);
	if(file.isEmpty()) {
		System.out.println("file is empty");
		contact.setImage("/image/contact.jpg");
	}
	else {
		contact.setImage(file.getOriginalFilename());
		File fileName=new ClassPathResource("static/image").getFile();
		Path path= Paths.get(fileName.getAbsolutePath()+File.separator+file.getOriginalFilename());
		Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
		System.out.println("image uploaded");
		
		
	}
	contact.setUser(user);
	user.getContacts().add(contact);
	this.repo.save(user);
	session.setAttribute("message",
			new Message("contact added !!", "alert-success"));
	return"normal/Add_contact";
		}catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message",
					new Message("Something wents wrong!!" + e.getMessage(), "alert-danger"));
			return"normal/Add_contact";
		}
		
				
}
	
	@GetMapping("/show_contact/{page}")
	public String show_contacts(@PathVariable("page") int page,Model m1,Principal principal) {
		
		String Username=principal.getName();
		User user=this.repo.getUserbyUserName(Username);
		   Pageable pageable=PageRequest.of(page, 5);
		
	Page<Contact> contacts=this.contactrepo.findContactsByUser(user.getId(),pageable);
		m1.addAttribute("contacts", contacts);
		m1.addAttribute("currentpage",page);
		
		m1.addAttribute("totalpage", contacts.getTotalPages());
		
		
		return"normal/show_contact";
		
	}
	@GetMapping("/contact/{cId}")
	public String contactdetails(@PathVariable("cId") Integer cId ,Model m) {
	      Optional<Contact> optional= this.contactrepo.findById(cId);
	     Contact contact= optional.get();
	     m.addAttribute("contacts", contact);
		return"normal/contactdetail";
	}
	@RequestMapping("/change-password-form")
	public String changepasswordform()
	
	{
		
		return"normal/settings";
	}
	@PostMapping("/change-password")
	public  String changepassword(@RequestParam("oldPassword") String oldPassword,
			@RequestParam("newPassword") String newPassword,Principal principal,HttpSession session
			)
	{
		System.out.println(oldPassword);
		String UserName=principal.getName();
		User user=repo.getUserbyUserName(UserName);
		if(this.bCryptPasswordEncoder.matches(oldPassword, user.getPassword()))
		{
			user.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
			this.repo.save(user);
			session.setAttribute("message",
					new Message("password is changed successfully", "alert-success"));
			
		}
		else {
			session.setAttribute("message",
					new Message("please correct enter oldPassword!!" , "alert-danger"));
			return"redirect:/user/change-password-form";
			
		}
		return"redirect:/user/index";
	}
	@GetMapping("/deletecontact/{cId}")
	public String deletecontact(@PathVariable("cId") int cId,Model m,HttpSession session,Principal principal) {
	String username=principal.getName();
	User user=this.repo.getUserbyUserName(username);
	
		   Optional<Contact> op=this.contactrepo.findById(cId);
		    Contact contact=op.get();
		    contact.setUser(null);
		    //if(user.getId()==contact.getcId()) {
		   this.contactrepo.delete(contact);
		   session.setAttribute("message",
					new Message("Delete is  successfully", "alert-success"));
		    //}
		return"redirect:/user/show_contact/0";
	}
	@PostMapping("/updateform/{cId}")
	public String updateform(@PathVariable("cId") int cId,Model m) {
		 Contact contact=this.contactrepo.findById(cId).get();
		 m.addAttribute("contacts", contact);
		
		return"normal/update_form";
	}
	@PostMapping("/contact_update")
	public String updatedata(@ModelAttribute Contact contacts,
			Model m,@RequestParam("imagefile") MultipartFile file,HttpSession session,Principal p) {
		
		try {
			User user=this.repo.getUserbyUserName(p.getName());
			contacts.setUser(user);
			if(!file.isEmpty()) {
				File fileName=new ClassPathResource("static/image").getFile();
				Path path= Paths.get(fileName.getAbsolutePath()+File.separator+file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				contacts.setImage(file.getOriginalFilename());
				
	
			}
			
			
			System.out.println(contacts.getcId());
			System.out.println(contacts.getName());
			this.contactrepo.save(contacts);
			session.setAttribute("message",
					new Message("update is  successfully", "alert-success"));
			
		} catch (Exception e) {
			
		}
		
		
		return "redirect:/user"+"/contact/"+contacts.getcId();
	}
}
