package com.Smart.manager.controller;


import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.Smart.manager.Dao.UserRepository;
import com.Smart.manager.emailservice.EmailService;
import com.Smart.manager.entities.User;
import com.Smart.manager.helper.Message;

@Controller
public class ForgotController {
	Random random=new Random(1000);
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	private EmailService emailService;
	@Autowired
	private UserRepository repo;
	@GetMapping("/forgot")
	public String forgotfotm() {
		
		return"forgot_email_form";
	}
	@PostMapping("/send-otp")
	public String sendotp(@RequestParam("email") String email,HttpSession session) {
		System.out.println(email);
		int otp=random.nextInt(9999);
		
		System.out.println(otp);
		session.setAttribute("email", email);
		session.setAttribute("otp", otp);
		
		
		String subject="OTP from SCM";
		String message="<h1>OTP"+otp+"</h1>";
		String to=email;
		if(this.repo.getUserbyUserName(email)!=null) {
		 this.emailService.sendmail(to, message, subject);
			 return"Verify_otp";
		}
		else {
			session.setAttribute("message",
					new Message("your email id doesn't exit here!!", "alert-danger"));
			return"forgot_email_form";
		}
		
}
	@PostMapping("/verify-otp")
	public String verifyotp(@RequestParam("otp") int otp,HttpSession session) {
		  int myotp= (int) session.getAttribute("otp");
		  System.out.println(myotp);
		  if(otp==myotp) {
		
		return"change_password";
		  }
		  else {
			  session.setAttribute("message",
						new Message("enter correct otp!!", "alert-danger"));
			  return"Verify_otp";
		  }
	}
	@PostMapping("/change-password")
	public String changepassword(@RequestParam("changepassword") String changepassword,HttpSession session)
	{
		 String email=(String) session.getAttribute("email");
		User user=this.repo.getUserbyUserName(email);
		user.setPassword(bCryptPasswordEncoder.encode(changepassword));
		this.repo.save(user);
		 session.setAttribute("message",
					new Message("password changed successfully !!", "alert-success"));
         return"redirect:/signin";
	}
}