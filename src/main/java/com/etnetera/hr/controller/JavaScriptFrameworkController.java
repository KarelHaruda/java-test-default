package com.etnetera.hr.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.etnetera.hr.data.JavaScriptFramework;
import com.etnetera.hr.exceptions.InvalidObjectException;
import com.etnetera.hr.repository.JavaScriptFrameworkRepository;

/**
 * Simple REST controller for accessing application logic.
 * 
 * @author Etnetera
 *
 */
@RestController
public class JavaScriptFrameworkController extends EtnRestController {

	private final JavaScriptFrameworkRepository repository;

	@Autowired
	public JavaScriptFrameworkController(JavaScriptFrameworkRepository repository) {
		this.repository = repository;
	}

	@GetMapping("/frameworks")
	public Iterable<JavaScriptFramework> frameworks() {
		return repository.findAll();
	}

	@GetMapping("/frameworks/{ID}")
	public ResponseEntity<?> getFramework(@PathVariable(value = "ID") String id) {
		try {
			if (!repository.existsById(Long.valueOf(id))) {
				throw new RuntimeException(String.format("Framework with ID %s not found!", id));
			}
			JavaScriptFramework framework = repository.findById(Long.valueOf(id)).get();
			return ResponseEntity.status(HttpStatus.OK).body(framework);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getLocalizedMessage());
		}
	}

	@DeleteMapping("/frameworks/{ID}")
	public ResponseEntity<?> deleteFramework(@PathVariable(value = "ID") String id) {
		try {
			repository.deleteById(Long.valueOf(id));
			return ResponseEntity.status(HttpStatus.OK).body(null);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getLocalizedMessage());
		}
	}

	@PutMapping("/frameworks/{ID}")
	public ResponseEntity<?> updateJavaSriptFrameword(@PathVariable(value = "ID") String id,
			@RequestBody JavaScriptFramework framework) {
		try {
			JavaScriptFramework actualFramework = repository.findById(Long.valueOf(id)).get();
			if (actualFramework == null) {
				throw new RuntimeException(String.format("Framework with ID %s not found!", id));
			}
			actualFramework.assing(framework);
			repository.save(actualFramework);
			return ResponseEntity.status(HttpStatus.OK).body(actualFramework);
		} catch (InvalidObjectException e) {
			// pokud to selze na chybu odpoved bude BAD REQUEST a v body bude popis chyby
			// tak jak to vyzaduje unit test
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getValidationResult());
		} catch (Exception e) {
			// pokud to selze na chybu odpoved bude BAD REQUEST a v body bude popis chyby
			// tak jak to vyzaduje unit test
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getLocalizedMessage());
		}
	}

	@PostMapping("/add")
	public ResponseEntity<?> addJavaSriptFrameword(@RequestBody JavaScriptFramework framework) {
		try {
			// Pokusime se ulozit predany objekt
			repository.save(framework);
			return ResponseEntity.status(HttpStatus.CREATED).body(framework);
		} catch (InvalidObjectException e) {
			// pokud to selze na chybu odpoved bude BAD REQUEST a v body bude popis chyby
			// tak jak to vyzaduje unit test
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getValidationResult());
		}
	}

	@GetMapping("/search/{NAME}")
	public Iterable<JavaScriptFramework> searchFrameworks(@PathVariable(value = "NAME") String name) {
		List<JavaScriptFramework> result = new ArrayList<>();
		for (JavaScriptFramework framework : repository.findAll()) {
			if (framework.getName().contains(name)) {
				result.add(framework);
			}
		}
		return result;
	}
}