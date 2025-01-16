package com.ecom.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.ecom.model.User;
import com.ecom.repository.UserRepository;
import com.ecom.service.UserService;
import com.ecom.util.AppConstant;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthFailureHandlerImpl extends SimpleUrlAuthenticationFailureHandler {
	@Autowired
	UserRepository userRepository;

	@Autowired
	UserService userService;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
		String email = request.getParameter("username");
		User user = userRepository.findByEmail(email);
		if (user.isEnabled()) {

			if (user.isAccountNonLocked()) {

				if (user.getFailedAttempt() < AppConstant.ATTEMPT_TIME) {
					userService.increaseFailedAttempt(user);
				} else {
					userService.userAccountLocked(user);
					exception = new LockedException("Your account is locked !! failed Attempt 3 ");
				}
			} else {
				if (userService.unlockAccountTimeExpired(user)) {
					exception = new LockedException(" Your account is unlocked !! Please try to login");
				} else {
					exception = new LockedException(" Your account is locked !! please try after sometimes");
				}
			}
		} else {
			exception = new LockedException(" Your account is inactive");
		}
		super.setDefaultFailureUrl("/signin?error");
		super.onAuthenticationFailure(request, response, exception);
	}

}
