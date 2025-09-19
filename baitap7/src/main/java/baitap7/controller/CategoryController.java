package baitap7.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import baitap7.entity.Category;
import baitap7.service.CategoryService;

import org.springframework.data.domain.*;

import jakarta.validation.Valid;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("admin/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/add")
    public String add(ModelMap model) {
        Category category = new Category();
        model.addAttribute("category", category);
        return "admin/categories/addOrEdit";  // Thymeleaf template
    }

    @PostMapping("/saveOrUpdate")
    public ModelAndView saveOrUpdate(
            ModelMap model,
            @Valid @ModelAttribute("category") Category cateModel,
            BindingResult result,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        if (result.hasErrors()) {
            return new ModelAndView("admin/categories/addOrEdit");
        }

        Category entity;

        if (cateModel.getId() != null) {
            // update: lấy từ DB rồi cập nhật
            entity = categoryService.findById(cateModel.getId()).orElse(new Category());
            entity.setName(cateModel.getName());
        } else {
            // add mới
            entity = new Category();
            entity.setName(cateModel.getName());
        }

        try {
            if (file != null && !file.isEmpty()) {
                String uploadDir = System.getProperty("user.dir") + "/uploads/";
                File dir = new File(uploadDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                File saveFile = new File(uploadDir + fileName);
                file.transferTo(saveFile);

                // set link ảnh
                entity.setImage("/uploads/" + fileName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message", "Lỗi khi upload file: " + e.getMessage());
            return new ModelAndView("admin/categories/addOrEdit", model);
        }

        categoryService.createCategory(entity);

        model.addAttribute("message", "Lưu Category thành công!");
        return new ModelAndView("redirect:/admin/categories/list");
    }



    @RequestMapping("list")
    public String list(ModelMap model) {
        List<Category> list = categoryService.findAll();
        model.addAttribute("categories", list);
        return "admin/categories/list";
    }

    @GetMapping("edit/{id}")
    public ModelAndView edit(ModelMap model, @PathVariable("id") Long id) {
        Optional<Category> opt = categoryService.findById(id);
        if (opt.isPresent()) {
            model.addAttribute("category", opt.get());
            return new ModelAndView("admin/categories/addOrEdit", model);
        }
        model.addAttribute("message", "Category is not existed!!!");
        return new ModelAndView("forward:/admin/categories", model);
    }


    @GetMapping("delete/{id}")
    public ModelAndView delete(ModelMap model, @PathVariable("id") Long id) {
        categoryService.deleteCategory(id);
        model.addAttribute("message", "Category is deleted!!!");
        return new ModelAndView("forward:/admin/categories/list", model);
    }


    @RequestMapping("searchpaginated")
    public String search(
            ModelMap model,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size
    ) {
        if (name == null) {
            name = "";
        }
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);

        Pageable pageable = PageRequest.of(currentPage - 1, pageSize, Sort.by("name"));
        Page<Category> resultPage;

        if (name != null && !name.isEmpty()) {
            resultPage = categoryService.findByNameContainingIgnoreCase(name, pageable);
            model.addAttribute("name", name);
        } else {
            resultPage = categoryService.findAll(pageable);
        }

        int totalPages = resultPage.getTotalPages();
        if (totalPages > 0) {
            int start = Math.max(1, currentPage - 2);
            int end = Math.min(currentPage + 2, totalPages);

            if (totalPages > 5) {
                if (end == totalPages) start = end - 5;
                else if (start == 1) end = start + 5;
            }

            List<Integer> pageNumbers = IntStream.rangeClosed(start, end)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        model.addAttribute("categoryPage", resultPage);

        return "admin/categories/searchpaginated";
    }
}
