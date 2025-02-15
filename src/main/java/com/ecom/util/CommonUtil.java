package com.ecom.util;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.ecom.model.Product;
import com.ecom.service.ProductService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class CommonUtil {

	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	private ProductService productService;

	public static String randomUUIDGenerator() {
		return UUID.randomUUID().toString();
	}

	public Boolean sendMail(String url, String email) throws UnsupportedEncodingException, MessagingException {
		MimeMessage message = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);

		helper.setFrom("debfaltu420@gmail.com", "Shopping Kart OTP");
		helper.setTo(email);
		String content = "<p>Hello,</p>" + "<p>You have requested to reset your password.</p>" + "<p>Click the link below to change your password:</p>" + "<p><a href=\"" + url + "\">Change my password</a></p>";
		helper.setSubject("Password Reset Link ");
		helper.setText(content, true);

		javaMailSender.send(message);

		return true;
	}

	public String generateURL(HttpServletRequest request) {
		String url = request.getRequestURL().toString();
		return url.replace(request.getServletPath(), "");
	}

	public double calculateProductPrice(String productId) {
		Product product = productService.getProductById(productId);
		double price = product.getPrice() - ((product.getPrice() * product.getDiscount()) / 100);
		return price;
	}

}
