package com.ecom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecom.model.Category;

import jakarta.transaction.Transactional;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
	public boolean existsByName(String name);

	public Category findByName(String name);

	@Modifying
	@Transactional
	@Query("Delete from Category where id= :uuid")
	public Integer deleteCategoryByUUID(@Param("uuid") String uuid);

	public Category findById(String id);

	public List<Category> findByIsActiveTrue();
}
