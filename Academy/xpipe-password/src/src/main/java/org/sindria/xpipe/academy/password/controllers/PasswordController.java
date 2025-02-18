package org.sindria.xpipe.academy.password.controllers;

import org.sindria.xpipe.academy.password.model.Credential;
import org.sindria.xpipe.academy.password.repository.PasswordRepository;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;

public class PasswordController extends ApiController {

    private final PasswordRepository passwordRepository;


    public PasswordController(PasswordRepository passwordRepository) {
        this.passwordRepository = passwordRepository;
    }


    //Put pw
    //Get pw
    //Post pw
    //Delete pw

    // Read
    //very secure indeed, but i guess it's fine for this program's purpose
    @GetMapping("/api/v1/credentials")
    public HashMap<String, Object> index(HttpServletResponse response) {

        try {
            Iterable<Credential> policies = this.passwordRepository.findAll();

            HashMap<String, Object> data = new HashMap<>();
            data.put("policies", policies);
            return this.sendResponse(response,"ok", 200, data);
        } catch (Exception e) {
            HashMap<String, Object> data = new HashMap<>();
            return this.sendError(response, "Internal Server Error", 500, data);
        }
    }

    //TODO: add find credential by id

    // Create
    @PostMapping("/api/v1/passwords/add")
    public HashMap<String, Object> store(@Valid @RequestBody Credential credential, HttpServletResponse response) {

        try {
            String name = credential.getName();
            Iterable<Credential> policies = this.passwordRepository.findAll();

//            for (var policy : policies) {
//                if (policy.getName().equals(name)) {
//                    return this.sendError(response, "Policy already exists with this name", 500, new HashMap<String, Object>());
//                }
//            }

            HashMap<String, Object> data = new HashMap<>();
            data.put("policy", credential);

            this.passwordRepository.save(credential);

            return this.sendResponse(response,"Credential added successfully", 201, data);
        } catch (Exception e) {
            return this.sendError(response, "Internal Server Error", 500, new HashMap<String, Object>());
        }
    }


    // Delete
    @DeleteMapping("/api/v1/passwords/delete/{credentialId}")
    public HashMap<String, Object> delete(@PathVariable long credentialId, HttpServletResponse response) {

        try {
            Credential credential = this.passwordRepository.findById(credentialId).orElse(null);

            if (credential == null) {
                return this.sendError(response, "Not Found", 404, new HashMap<String, Object>());
            }

            this.passwordRepository.delete(credential);

            return this.sendResponse(response,"Credential deleted successfully", 202, new HashMap<String, Object>());
        } catch (Exception e) {
            return this.sendError(response, "Internal Server Error", 500, new HashMap<String, Object>());
        }
    }



}
