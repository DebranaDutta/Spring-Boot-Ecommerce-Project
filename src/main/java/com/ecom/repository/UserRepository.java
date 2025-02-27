package com.ecom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.ecom.model.User;

import jakarta.transaction.Transactional;
import java.util.List;


@Repository
public interface UserRepository extends JpaRepository<User, String> {
	public User findByEmail(String email);
	
	public User findByUserId(String userId);

	@Modifying
	@Transactional
	@Query(value = "DELETE FROM User WHERE userId = :userId ")
	public int deleteByUserId(@Param("userId") String userId);

	public User findByResetToken(String token);
	
	public List<User> findByRole(String role);
}
