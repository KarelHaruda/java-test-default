package com.etnetera.hr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

	@PostMapping("/add")  
    public ResponseEntity<?> addJavaSriptFrameword(@RequestBody JavaScriptFramework framework) {
		try {
			//Pokusime se ulozit predany objekt
			repository.save(framework);			
			return ResponseEntity.status(HttpStatus.CREATED).body(framework);
		} catch (InvalidObjectException e) {
			//pokud to selze na chybu odpoved bude BAD REQUEST a v body bude popis chyby tak jak to vyzaduje unit test
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getValidationResult());
		}
    }
 	
	@GetMapping("/frameworks")
	public Iterable<JavaScriptFramework> frameworks() {
		return repository.findAll();
	}

}
