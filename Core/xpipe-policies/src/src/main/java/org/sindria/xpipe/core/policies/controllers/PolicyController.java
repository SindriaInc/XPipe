package org.sindria.xpipe.core.policies.controllers;

import org.json.JSONObject;
import org.sindria.xpipe.core.policies.helpers.PolicyHelper;
import org.sindria.xpipe.core.policies.models.*;

import org.sindria.xpipe.core.policies.repositories.*;
import org.sindria.xpipe.core.policies.validators.PolicyVerifyValidator;

import org.sindria.xpipe.core.policies.validators.ResourceValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.lang.reflect.Array;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.springframework.transaction.annotation.Transactional;

@RestController
public class PolicyController extends ApiController {

    private final PolicyRepository policyRepository;

    private final TypeRepository typeRepository;

    private final PolicyUserRepository policyUserRepository;

    private final ActionRepository actionRepository;

    private final CapabilityRepository capabilityRepository;

    private final ActionCapabilityRepository actionCapabilityRepository;

    private final ResourceRepository resourceRepository;

    private final PolicyVerifyValidator policyVerifyValidator;

    private final ResourceValidator resourceValidator;

    /**
     * PolicyController constructor
     */
    public PolicyController(PolicyRepository policyRepository, TypeRepository typeRepository, PolicyUserRepository policyUserRepository, ActionRepository actionRepository, CapabilityRepository capabilityRepository, ActionCapabilityRepository actionCapabilityRepository, ResourceRepository resourceRepository, PolicyVerifyValidator policyVerifyValidator, ResourceValidator resourceValidator) {
        this.policyRepository = policyRepository;
        this.typeRepository = typeRepository;
        this.policyUserRepository = policyUserRepository;
        this.actionRepository = actionRepository;
        this.capabilityRepository = capabilityRepository;
        this.actionCapabilityRepository = actionCapabilityRepository;
        this.resourceRepository = resourceRepository;

        this.policyVerifyValidator = policyVerifyValidator;
        this.resourceValidator = resourceValidator;
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

            HashMap<String, Object> data = new HashMap<>();
            data.put("policy", newPolicy);

            this.policyRepository.save(newPolicy);

            return this.sendResponse(response,"Policy added successfully", 201, data);
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
                return this.sendResponse(response, "User not attached to any policy", 404, new HashMap<String, Object>());
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

            String userId = attachPolicy.getUserId();
            Long policyId = attachPolicy.getPolicyId();

            List<PolicyUser> policiesAttached = this.policyUserRepository.findByUserId(userId);

            if (! policiesAttached.isEmpty()) {

                for (var entry : policiesAttached) {
                    Long entryPolicyId = entry.getPolicyId();

                    if (entryPolicyId.equals(policyId)) {
                        return this.sendResponse(response,"Policy already attached to a user", 200, new HashMap<String, Object>());
                    }
                }
            }

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

    @GetMapping("/api/v1/policies/default/attach/{userId}")
    public HashMap<String, Object> attachDefaultPolicy(@PathVariable String userId, HttpServletResponse response) {

        try {

            Long defaultPolicyId = 1L;

            // TODO: Validate if userId exists by service-to-service REST request

            List<PolicyUser> policiesAttached = this.policyUserRepository.findByUserId(userId);

            if (! policiesAttached.isEmpty()) {

                for (var entry : policiesAttached) {
                    Long entryPolicyId = entry.getPolicyId();

                    if (entryPolicyId.equals(defaultPolicyId)) {
                        return this.sendResponse(response,"Default policy already attached", 200, new HashMap<String, Object>());
                    }
                }
            }

            PolicyUser attachPolicy = new PolicyUser();
            attachPolicy.setPolicyId(defaultPolicyId);
            attachPolicy.setUserId(userId);
            this.policyUserRepository.save(attachPolicy);

            return this.sendResponse(response,"Default policy attached successfully", 201, new HashMap<String, Object>());
        } catch (Exception e) {
            return this.sendError(response, "Internal Server Error", 500, new HashMap<String, Object>());
        }
    }

    @GetMapping("/api/v1/policies/verify")
    public HashMap<String, Object> verify(@RequestParam String uid, @RequestParam String uri, @RequestParam String mtd, @RequestParam String rid, HttpServletResponse response) {

        try {

            List<PolicyUser> policiesIds = this.policyUserRepository.findByUserId(uid);

            if (policiesIds.isEmpty()) {
                return this.sendError(response, "User not attached to any policy", 404, new HashMap<String, Object>());
            }

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
                        // TODO: change Method key with Resource
                        //JsonArray policyStatementEntryMethods = policyStatementEntry.getAsJsonArray("Method");
                        //String policyStatementEntryFirstMethod = policyStatementEntryMethods.get(0).getAsString();

                        JsonArray policyStatementEntryActions = policyStatementEntry.getAsJsonArray("Action");
                        String policyStatementEntryFirstAction = policyStatementEntryActions.get(0).getAsString();

                        // Super Admin policy only
                        if (policyStatementEntryFirstAction.equals("Any")) {
                            HashMap<String, Boolean> hasAccess = new HashMap<>();
                            hasAccess.put("hasAccess", true);

                            HashMap<String, Object> data = new HashMap<>();
                            data.put("response", hasAccess);

                            return this.sendResponse(response,"Access granted", 200, data);
                        }

                        for (var action : policyStatementEntryActions) {
                            String policyStatementEntryAction = action.getAsString();

                            List<Action> actionPersisted = actionRepository.findByName(policyStatementEntryAction);

                            // Exception
                            if (actionPersisted.isEmpty()) {
                                return this.sendError(response, "Found action not persisted", 500, new HashMap<String, Object>());
                            }

                            Action actionPersistedEntry = actionPersisted.get(0);
                            String actionPersistedEntryUri = actionPersistedEntry.getUri();
                            String actionPersistedEntryMethod = actionPersistedEntry.getMethod();

                            // Clean uri with query string or path variable
                            String cleanedUri = uri;

                            Boolean hasId = this.policyVerifyValidator.hasId(uri);

                            if (hasId) {
                                cleanedUri = PolicyHelper.cleanUri(uri, "id");
                            }

                            Boolean hasUuid = this.policyVerifyValidator.hasUuid(uri);

                            if (hasUuid) {
                                cleanedUri = PolicyHelper.cleanUri(uri, "uuid");
                            }

                            Boolean hasQueryString = this.policyVerifyValidator.hasQueryString(uri);

                            if (hasQueryString) {
                                cleanedUri = PolicyHelper.cleanUri(uri, "query");
                            }

                            // Match uri with related persisted uri action and method
                            if (actionPersistedEntryUri.equals(cleanedUri) && actionPersistedEntryMethod.equals(mtd)) {

                                JsonArray policyStatementEntryResources = policyStatementEntry.getAsJsonArray("Resource");
                                String policyStatementEntryFirstResource = policyStatementEntryResources.get(0).getAsString();

                                if (policyStatementEntryFirstResource.equals("Any")) {
                                    HashMap<String, Boolean> hasAccess = new HashMap<>();
                                    hasAccess.put("hasAccess", true);

                                    HashMap<String, Object> data = new HashMap<>();
                                    data.put("response", hasAccess);

                                    return this.sendResponse(response,"Access granted", 200, data);
                                }

                                for (var resource : policyStatementEntryResources) {

                                    // resource from policy
                                    String resourceEntry = resource.getAsString();
                                    String[] resourceParts = resourceEntry.split(":");
                                    String resourceType = resourceParts[5];
                                    String resourceId = resourceParts[6];

                                    // Case 2
                                    if (resourceId.equals("any") && rid.equals("any") && resourceRepository.existsByType(resourceType)) {
                                        HashMap<String, Boolean> hasAccess = new HashMap<>();
                                        hasAccess.put("hasAccess", true);

                                        HashMap<String, Object> data = new HashMap<>();
                                        data.put("response", hasAccess);

                                        return this.sendResponse(response, "Access granted", 200, data);
                                    }

                                    // Case 3
                                    if (resourceId.equals(rid) && resourceRepository.existsByResourceId(rid)) {
                                        HashMap<String, Boolean> hasAccess = new HashMap<>();
                                        hasAccess.put("hasAccess", true);

                                        HashMap<String, Object> data = new HashMap<>();
                                        data.put("response", hasAccess);

                                        return this.sendResponse(response, "Access granted", 200, data);
                                    }


                                }

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


    @GetMapping("/api/v1/policies/capabilities/user/{userId}")
    public HashMap<String, Object> capabilitiesUser(@PathVariable String userId, HttpServletResponse response) {

        try {

            List<PolicyUser> policiesIds = this.policyUserRepository.findByUserId(userId);

            if (policiesIds.isEmpty()) {
                return this.sendError(response, "User not attached to any policy", 404, new HashMap<String, Object>());
            }

            List<String> capabilities = new ArrayList<String>();

            for (var pol : policiesIds) {
                Long policyId = pol.getPolicyId();
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

                        JsonArray policyStatementEntryActions = policyStatementEntry.getAsJsonArray("Action");
                        String policyStatementEntryFirstAction = policyStatementEntryActions.get(0).getAsString();

                        for (var action : policyStatementEntryActions) {
                            String policyStatementEntryAction = action.getAsString();

                            List<Action> actionPersisted = actionRepository.findByName(policyStatementEntryAction);

                            // Exception
                            if (actionPersisted.isEmpty()) {
                                return this.sendError(response, "Found action not persisted", 500, new HashMap<String, Object>());
                            }

                            Action actionPersistedEntry = actionPersisted.get(0);
                            Long actionPersistedEntryId = actionPersistedEntry.getId();

                            List<ActionCapability> capabilitiesIds = this.actionCapabilityRepository.findByActionId(actionPersistedEntryId);

                            // Exception
                            if (capabilitiesIds.isEmpty()) {
                                return this.sendError(response, "Fatal error: found action without capability relation", 500, new HashMap<String, Object>());
                            }

                            for (var cap : capabilitiesIds) {
                                Long capabilityId = cap.getCapabilityId();
                                Capability capability = this.capabilityRepository.findById(capabilityId).orElse(null);

                                // Exception
                                if (capability == null) {
                                    return this.sendError(response, "Error during extract capability related action", 500, new HashMap<String, Object>());
                                }

                                String capabilityName = capability.getName();

                                // Remove duplicates
                                if (capabilities.contains(capabilityName)) {
                                    continue;
                                } else {
                                    capabilities.add(capabilityName);
                                }

                            }

                        }

                    }

                }

            }

            HashMap<String, Object> data = new HashMap<>();
            data.put("capabilities", capabilities);

            return this.sendResponse(response, "ok", 200, data);
        } catch (Exception e) {
            e.printStackTrace();
            return this.sendError(response, "Internal Server Error", 500, new HashMap<String, Object>());
        }
    }


    @GetMapping("/api/v1/policies/resources")
    public HashMap<String, Object> listResources(HttpServletResponse response) {

        try {
            Iterable<Resource> resources = this.resourceRepository.findAll();

            HashMap<String, Object> data = new HashMap<>();
            data.put("resources", resources);
            return this.sendResponse(response,"ok", 200, data);
        } catch (Exception e) {
            HashMap<String, Object> data = new HashMap<>();
            return this.sendError(response, "Internal Server Error", 500, data);
        }
    }

    @PutMapping("/api/v1/policies/resources/update")
    public HashMap<String, Object> updateResources(@RequestParam String resource, HttpServletResponse response) {

        if (!this.resourceValidator.isValid(resource)) {
            return this.sendError(response, "Invalid resource, SRN needs to match this pattern: " + ResourceValidator.SRN_REGEX, 400, new HashMap<String, Object>());
        }

        try {

            String[] resourceFields = resource.split(":");
            // TODO: move to validator ?
            if (resourceFields.length > 7) {
                return this.sendError(response, "Invalid resource length", 400, new HashMap<String, Object>());
            }

            String name = resourceFields[0];
            String product = resourceFields[1];
            String service = resourceFields[2];
            String region = resourceFields[3];
            Long accountId = Long.parseLong(resourceFields[4]);
            String resourceType = resourceFields[5];
            String resourceId = resourceFields[6];


            if (resourceId.equals("void") || resourceId.equals("any")) {

                Resource newResource = new Resource();
                newResource.setName(name);
                newResource.setProduct(product);
                newResource.setService(service);
                newResource.setRegion(region);
                newResource.setAccountId(accountId);
                newResource.setResourceType(resourceType);

                newResource.setResourceId(resourceId.equals("void") ? UUID.randomUUID().toString() : "any");

                HashMap<String, Object> data = new HashMap<>();
                data.put("resource", newResource);

                this.resourceRepository.save(newResource);

                return this.sendResponse(response, "Resource added successfully", 201, data);
            } else {

                Resource storedResource = this.resourceRepository.findOneByResourceId(resourceId);

                if (storedResource == null) {
                    return this.sendError(response, "Invalid resource", 500, new HashMap<String, Object>());
                }

                HashMap<String, Object> data = new HashMap<>();
                data.put("resource", storedResource);

                this.resourceRepository.delete(storedResource);

                return this.sendResponse(response,"Resource deleted successfully", 200, data);

            }
        } catch (Exception e) {
            return this.sendError(response, "Internal Server Error", 500, new HashMap<String, Object>());
        }
    }

}
