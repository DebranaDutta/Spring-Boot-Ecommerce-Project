package com.ecom.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Category;
import com.ecom.service.CategoryService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/category")
public class CategoryController {
	@Autowired
	private CategoryService categoryService;
	
	@PostMapping("/saveCategory")
	public String saveCategory(@ModelAttribute Category category, @RequestParam("isActive") String isActive, @RequestParam("file") MultipartFile file, HttpSession session) throws IOException {

		String imageName = file != null ? file.getOriginalFilename() : "default.jpg";
		category.setImage(imageName);
		if (isActive.equalsIgnoreCase("true")) {
			category.setActive(true);
		} else {
			category.setActive(false);
		}

		boolean isCategoryExist = categoryService.isCategoryExist(category);

		if (isCategoryExist) {
			session.setAttribute("errorMsg", "Category name already exist");
		} else {
			Category saveCategory = categoryService.saveCategory(category);
			if (ObjectUtils.isEmpty(saveCategory)) {
				session.setAttribute("errorMsg", "Not saved! Internal server error !");
			} else {
				/* Image Store */
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "category" + File.separator + imageName);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				/* Image Store */
				session.setAttribute("successMsg", "Saved successfully");
			}
		}
		return "redirect:/admin/loadAddCategory";
	}

	@GetMapping(path = "/loadEditCategory/{name}")
	public String loadEditCategory(@PathVariable("name") String name, Model model) {
		Category category = categoryService.getCategoryByName(name);
		model.addAttribute("category", category);
		return "Admin/EditCategory.html";
	}

	@PostMapping("/updateCategory")
	public String updateCategory(@ModelAttribute Category newCategory, @RequestParam("isActive") String isActive, @RequestParam("file") MultipartFile file, HttpSession session) throws IOException {

		Category oldCategory = categoryService.getCategoryById(newCategory.getId());

		// String fileName = file != null ? file.getOriginalFilename() :
		// oldCategory.getImage();

		String fileName = file.isEmpty() ? oldCategory.getImage() : file.getOriginalFilename();

		if (!ObjectUtils.isEmpty(oldCategory)) {
			oldCategory.setName(newCategory.getName());

			if (isActive.equalsIgnoreCase("true")) {
				oldCategory.setActive(true);
			} else {
				oldCategory.setActive(false);
			}

			oldCategory.setImage(fileName);
		}

		Category updatedCategory = categoryService.saveCategory(oldCategory);

		if (!ObjectUtils.isEmpty(updatedCategory)) {

			if (!file.isEmpty()) {
				/* Image Store */
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "category" + File.separator + fileName);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				/* Image Store */
			}

			session.setAttribute("successMsg", "Category updated successfully");
		} else {
			session.setAttribute("errorMsg", "Something went wrong!.....");
		}

		return "redirect:/category/loadEditCategory/" + newCategory.getName();
	}

	@GetMapping("/deleteCategory/{name}")
	public String deleteCategory(@PathVariable("name") String name, HttpSession session) {

		Integer count = categoryService.deleteCategory(name);

		if (count >= 1) {
			session.setAttribute("successMsg", "Category deleted successfully");
		} else {
			session.setAttribute("errorMsg", "Something went wrong!.....");
		}
		return "redirect:/admin/loadAddCategory";
	}

}
