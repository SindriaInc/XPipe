package org.sindria.xpxdev.controllers;

import org.sindria.xpxdev.models.Policy;
import org.sindria.xpxdev.models.Type;
import org.sindria.xpxdev.models.PolicyUser;
import org.sindria.xpxdev.repositories.PolicyRepository;
import org.sindria.xpxdev.repositories.TypeRepository;
import org.sindria.xpxdev.repositories.PolicyUserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@RestController
public class PolicyController extends ApiController {

    private final PolicyRepository policyRepository;

    private final TypeRepository typeRepository;

    private final PolicyUserRepository policyUserRepository;

    /**
     * PolicyController constructor
     */
    public PolicyController(PolicyRepository policyRepository, TypeRepository typeRepository, PolicyUserRepository policyUserRepository) {
        this.policyRepository = policyRepository;
        this.typeRepository = typeRepository;
        this.policyUserRepository = policyUserRepository;
    }

    @GetMapping("/api/v1/policies")
    public HashMap<String, Object> index(HttpServletResponse response) {

        try {
            Iterable<Policy> policies = this.policyRepository.findAll();

            HashMap<String, Object> data = new HashMap<>();
            data.put("policies", policies);
            return this.sendResponse(response,"ok", 200, data);
        } catch (Exception e) {
            HashMap<String, Object> data = new HashMap<>();
            return this.sendError(response, "Internal Server Error", 500, data);
        }
    }

    @GetMapping("/api/v1/policies/types")
    public HashMap<String, Object> policyTypes(HttpServletResponse response) {

        try {
            Iterable<Type> types = this.typeRepository.findAll();

            HashMap<String, Object> data = new HashMap<>();
            data.put("types", types);
            return this.sendResponse(response,"ok", 200, data);
        } catch (Exception e) {
            HashMap<String, Object> data = new HashMap<>();
            return this.sendError(response, "Internal Server Error", 500, data);
        }
    }

    @GetMapping("/api/v1/policies/paginate")
    public HashMap<String, Object> indexPaginate(@RequestParam Integer off, @RequestParam Integer sze, HttpServletResponse response) {

        try {
            Page<Policy> policies = this.policyRepository.findAll(PageRequest.of(off, sze));

            HashMap<String, Object> data = new HashMap<>();
            data.put("policies", policies);
            return this.sendResponse(response,"ok", 200, data);
        } catch (Exception e) {
            HashMap<String, Object> data = new HashMap<>();
            return this.sendError(response, "Internal Server Error", 500, data);
        }
    }

    @GetMapping("/api/v1/policies/{policyId}")
    public HashMap<String, Object> show(@PathVariable Long policyId, HttpServletResponse response) {

        try {
            Optional<Policy> policy = this.policyRepository.findById(policyId);

            if (policy.isEmpty()) {
                return this.sendError(response, "Not Found", 404, new HashMap<String, Object>());
            }

            HashMap<String, Object> data = new HashMap<>();
            data.put("policy", policy);

            return this.sendResponse(response,"ok", 200, data);
        } catch (Exception e) {
            return this.sendError(response, "Internal Server Error", 500, new HashMap<String, Object>());
        }
    }

    @GetMapping("/api/v1/policies/types/{typeId}")
    public HashMap<String, Object> showPoliciesType(@PathVariable Long typeId, HttpServletResponse response) {

        try {
            Optional<Type> type = this.typeRepository.findById(typeId);

            if (type.isEmpty()) {
                return this.sendError(response, "Not Found", 404, new HashMap<String, Object>());
            }

            HashMap<String, Object> data = new HashMap<>();
            data.put("type", type);

            return this.sendResponse(response,"ok", 200, data);
        } catch (Exception e) {
            return this.sendError(response, "Internal Server Error", 500, new HashMap<String, Object>());
        }
    }


    @PostMapping("/api/v1/policies")
    public HashMap<String, Object> store(@Valid @RequestBody Policy newPolicy, HttpServletResponse response) {

        try {
            String name = newPolicy.getName();
            Iterable<Policy> policies = this.policyRepository.findAll();

            // TODO: implement findByName() on policyRepository
            for (var policy : policies) {
                if (policy.getName().equals(name)) {
                    return this.sendError(response, "Policy already exists with this name", 500, new HashMap<String, Object>());
                }
            }

            this.policyRepository.save(newPolicy);

            return this.sendResponse(response,"Policy added successfully", 201, new HashMap<String, Object>());
        } catch (Exception e) {
            return this.sendError(response, "Internal Server Error", 500, new HashMap<String, Object>());
        }
    }


    @PutMapping("/api/v1/policies/{policyId}")
    public HashMap<String, Object> edit(@PathVariable long policyId, @Valid @RequestBody Policy editPolicy, HttpServletResponse response) {

        try {
            Policy policy = this.policyRepository.findById(policyId).orElse(null);

            if (policy == null) {
                return this.sendError(response, "Not Found", 404, new HashMap<String, Object>());
            }

            policy.setName(editPolicy.getName());
            policy.setContent(editPolicy.getContent());
            this.policyRepository.save(policy);

            return this.sendResponse(response,"Policy edited successfully", 202, new HashMap<String, Object>());
        } catch (Exception e) {
            return this.sendError(response, "Internal Server Error", 500, new HashMap<String, Object>());
        }
    }


    @DeleteMapping("/api/v1/policies/{policyId}")
    public HashMap<String, Object> delete(@PathVariable long policyId, HttpServletResponse response) {

        try {
            Policy policy = this.policyRepository.findById(policyId).orElse(null);

            if (policy == null) {
                return this.sendError(response, "Not Found", 404, new HashMap<String, Object>());
            }

            this.policyRepository.delete(policy);

            return this.sendResponse(response,"Policy deleted successfully", 202, new HashMap<String, Object>());
        } catch (Exception e) {
            return this.sendError(response, "Internal Server Error", 500, new HashMap<String, Object>());
        }
    }


    @GetMapping("/api/v1/policies/search")
    public HashMap<String, Object> search(@RequestParam String q, HttpServletResponse response) {

        try {

            int page = 0;
            int size = 20;

            Pageable pageable = PageRequest.of(page, size);

            Page<Policy> policies = this.policyRepository.findBySearchTerm(q, pageable);

            if (policies.isEmpty()) {
                return this.sendError(response, "Not Found", 404, new HashMap<String, Object>());
            }

            HashMap<String, Object> data = new HashMap<>();
            data.put("policies", policies);

            return this.sendResponse(response,"ok", 200, data);
        } catch (Exception e) {
            return this.sendError(response, "Internal Server Error", 500, new HashMap<String, Object>());
        }
    }


    @GetMapping("/api/v1/policies/user/{userId}")
    public HashMap<String, Object> showUserPolicies(@PathVariable String userId, HttpServletResponse response) {

        try {
            List<PolicyUser> policiesIds = this.policyUserRepository.findByUserId(userId);

            if (policiesIds.isEmpty()) {
                return this.sendError(response, "User not attached to any policy", 404, new HashMap<String, Object>());
            }

            ArrayList<Policy> policies = new ArrayList<>();

            for (var entry : policiesIds) {
                Long policyId = entry.getPolicyId();
                Policy policy = this.policyRepository.findById(policyId).orElse(null);

                // Exception
                if (policy == null) {
                    return this.sendError(response, "Error during extract user related policy", 500, new HashMap<String, Object>());
                }

                policies.add(policy);
            }

            if (policies.isEmpty()) {
                return this.sendError(response, "Not Found", 404, new HashMap<String, Object>());
            }

            HashMap<String, Object> data = new HashMap<>();
            data.put("policies", policies);

            return this.sendResponse(response,"ok", 200, data);
        } catch (Exception e) {
            return this.sendError(response, "Internal Server Error", 500, new HashMap<String, Object>());
        }
    }


    @GetMapping("/api/v1/policies/users/{policyId}")
    public HashMap<String, Object> showPolicyUsers(@PathVariable long policyId, HttpServletResponse response) {

        try {
            List<PolicyUser> usersIds = this.policyUserRepository.findByPolicyId(policyId);

            if (usersIds.isEmpty()) {
                return this.sendError(response, "Policy not attached to any user", 404, new HashMap<String, Object>());
            }

            ArrayList<String> users = new ArrayList<>();

            for (var entry : usersIds) {
                String userId = entry.getUserId();
                users.add(userId);
            }

            if (users.isEmpty()) {
                return this.sendError(response, "Not Found", 404, new HashMap<String, Object>());
            }

            HashMap<String, Object> data = new HashMap<>();
            data.put("users", users);

            return this.sendResponse(response,"ok", 200, data);
        } catch (Exception e) {
            return this.sendError(response, "Internal Server Error", 500, new HashMap<String, Object>());
        }
    }


    @PostMapping("/api/v1/policies/attach")
    public HashMap<String, Object> attachPolicy(@Valid @RequestBody PolicyUser attachPolicy, HttpServletResponse response) {

        try {
            // TODO: Validate if userId exists by service-to-service REST request

            this.policyUserRepository.save(attachPolicy);
            return this.sendResponse(response,"Policy attached successfully", 201, new HashMap<String, Object>());
        } catch (Exception e) {
            return this.sendError(response, "Internal Server Error", 500, new HashMap<String, Object>());
        }
    }


    @PostMapping("/api/v1/policies/detach")
    public HashMap<String, Object> detachPolicy(@Valid @RequestBody PolicyUser detachPolicy, HttpServletResponse response) {

        try {

            String userId = detachPolicy.getUserId();
            Long policyId = detachPolicy.getPolicyId();

            List<PolicyUser> policiesAttached = this.policyUserRepository.findByUserId(userId);

            if (policiesAttached.isEmpty()) {
                return this.sendError(response, "User not attached to any policy", 404, new HashMap<String, Object>());
            }

            Long id = null;

            for (var entry : policiesAttached) {
                Long entryPolicyId = entry.getPolicyId();

                if (entryPolicyId.equals(policyId)) {
                    id = entry.getId();
                }
            }

            // Exception
            if (id == null) {
                return this.sendError(response, "Error during extract entry id", 500, new HashMap<String, Object>());
            }

            PolicyUser policyToDetach = this.policyUserRepository.findById(id).orElse(null);

            if (policyToDetach == null) {
                return this.sendError(response, "Not Found", 404, new HashMap<String, Object>());
            }

            this.policyUserRepository.delete(policyToDetach);

            return this.sendResponse(response,"Policy detached successfully", 202, new HashMap<String, Object>());
        } catch (Exception e) {
            return this.sendError(response, "Internal Server Error", 500, new HashMap<String, Object>());
        }
    }


    @GetMapping("/api/v1/policies/verify")
    public HashMap<String, Object> verify(@RequestParam String uid, @RequestParam String uri, @RequestParam String mtd, HttpServletResponse response) {

        try {

            List<PolicyUser> policiesIds = this.policyUserRepository.findByUserId(uid);

            if (policiesIds.isEmpty()) {
                return this.sendError(response, "User not attached to any policy", 404, new HashMap<String, Object>());
            }

            ArrayList<Policy> policies = new ArrayList<>();

            for (var entry : policiesIds) {
                Long policyId = entry.getPolicyId();
                Policy policy = this.policyRepository.findById(policyId).orElse(null);

                // Exception
                if (policy == null) {
                    return this.sendError(response, "Error during extract user related policy", 500, new HashMap<String, Object>());
                }

                JsonObject policyContent = new Gson().fromJson(policy.getContent(), JsonObject.class);

                JsonArray policyStatements = policyContent.getAsJsonArray("Statement");

                for (var statement : policyStatements) {
                    JsonObject policyStatementEntry = statement.getAsJsonObject();

                    HashMap policyStatementEntryMap = new Gson().fromJson(policyStatementEntry, HashMap.class);

                    String policyStatementEntryEffect = (String) policyStatementEntryMap.get("Effect");

                    if (policyStatementEntryEffect.equals("Deny")) {
                        continue;
                    } else {
                        JsonArray policyStatementEntryMethods = policyStatementEntry.getAsJsonArray("Method");
                        String policyStatementEntryFirstMethod = policyStatementEntryMethods.get(0).getAsString();

                        JsonArray policyStatementEntryActions = policyStatementEntry.getAsJsonArray("Action");
                        String policyStatementEntryFirstAction = policyStatementEntryActions.get(0).getAsString();

                        if (policyStatementEntryFirstAction.equals("*")) {
                            HashMap<String, Boolean> hasAccess = new HashMap<>();
                            hasAccess.put("hasAccess", true);

                            HashMap<String, Object> data = new HashMap<>();
                            data.put("response", hasAccess);

                            return this.sendResponse(response,"Access granted", 200, data);
                        }

                        for (var action : policyStatementEntryActions) {
                            String policyStatementEntryAction = action.getAsString();

                            if (policyStatementEntryAction.equals(uri)) {

                                if (! policyStatementEntryFirstMethod.equals("*")) {
                                    for (var method : policyStatementEntryMethods) {
                                        String policyStatementEntryMethod = method.getAsString();

                                        if (policyStatementEntryMethod.equals(mtd)) {

                                            HashMap<String, Boolean> hasAccess = new HashMap<>();
                                            hasAccess.put("hasAccess", true);

                                            HashMap<String, Object> data = new HashMap<>();
                                            data.put("response", hasAccess);

                                            return this.sendResponse(response,"Access granted", 200, data);
                                        }
                                    }
                                }

                                HashMap<String, Boolean> hasAccess = new HashMap<>();
                                hasAccess.put("hasAccess", true);

                                HashMap<String, Object> data = new HashMap<>();
                                data.put("response", hasAccess);

                                return this.sendResponse(response,"Access granted", 200, data);

                            }

                        }

                    }


                }


            }


            HashMap<String, Boolean> hasAccess = new HashMap<>();
            hasAccess.put("hasAccess", false);

            HashMap<String, Object> data = new HashMap<>();
            data.put("response", hasAccess);

            return this.sendResponse(response,"Access denied", 403, data);
        } catch (Exception e) {
            e.printStackTrace();
            return this.sendError(response, "Internal Server Error", 500, new HashMap<String, Object>());
        }
    }


}
