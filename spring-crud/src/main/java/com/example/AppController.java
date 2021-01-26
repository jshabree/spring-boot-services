package com.example;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import java.io.IOException;
import java.util.stream.Collectors;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.uploadingfiles.storage.StorageFileNotFoundException;
import com.example.uploadingfiles.storage.StorageService;

@Controller
public class AppController {
	@Autowired
	private ProductService service;

	@RequestMapping("/")
	public String viewHomePage(Model model) {
		List<Product> listProducts = service.listAll();
		model.addAttribute("listProducts", listProducts);

		return "index";
	}

	@RequestMapping("/new")
	public String showNewProductForm(Model model) {
		Product product = new Product();
		model.addAttribute("product", product);

		return "new_product";
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String saveProduct(@ModelAttribute("product") Product product) {
		service.save(product);

		return "redirect:/";
	}

	@RequestMapping("/edit/{id}")
	public ModelAndView showEditProductForm(@PathVariable(name = "id") Integer id) {
		ModelAndView mav = new ModelAndView("edit_product");

		Product product = service.get(id);
		mav.addObject("product", product);

		return mav;
	}

	@RequestMapping("/delete/{id}")
	public String deleteProduct(@PathVariable(name = "id") Integer id) {
		service.delete(id);

		return "redirect:/";
	}

	public class FileUploadController {

		private final StorageService storageService;

		@Autowired
		public FileUploadController(StorageService storageService) {
			this.storageService = storageService;
		}

		@GetMapping("/")
		public String listUploadedFiles(Model model) throws IOException {

			model.addAttribute("files",
					storageService.loadAll().map(path -> MvcUriComponentsBuilder
							.fromMethodName(FileUploadController.class, "serveFile", path.getFileName().toString())
							.build().toUri().toString()).collect(Collectors.toList()));

			return "uploadForm";
		}

		@GetMapping("/files/{filename:.+}")
		@ResponseBody
		public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

			Resource file = storageService.loadAsResource(filename);
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
					.body(file);
		}

		@PostMapping("/")
		public String handleFileUpload(@RequestParam("file") MultipartFile file,
				RedirectAttributes redirectAttributes) {

			storageService.store(file);
			redirectAttributes.addFlashAttribute("message",
					"You successfully uploaded " + file.getOriginalFilename() + "!");

			return "redirect:/";
		}

		@ExceptionHandler(StorageFileNotFoundException.class)
		public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
			return ResponseEntity.notFound().build();
		}

	}

}