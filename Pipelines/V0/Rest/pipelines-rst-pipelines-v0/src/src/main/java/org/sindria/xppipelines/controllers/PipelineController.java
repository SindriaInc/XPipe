package org.sindria.xppipelines.controllers;

import org.sindria.xppipelines.models.Pipeline;
import org.sindria.xppipelines.models.PipelineUser;
import org.sindria.xppipelines.repositories.PipelineRepository;
import org.sindria.xppipelines.repositories.PipelineUserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import java.util.*;

@RestController
public class PipelineController extends ApiController {

    private final PipelineRepository pipelineRepository;

    private final PipelineUserRepository pipelineUserRepository;

    /**
     * PipelineController constructor
     */
    public PipelineController(PipelineRepository pipelineRepository, PipelineUserRepository pipelineUserRepository) {
        this.pipelineRepository = pipelineRepository;
        this.pipelineUserRepository = pipelineUserRepository;
    }

    @GetMapping("/api/v1/pipelines")
    public HashMap<String, Object> index(HttpServletResponse response) {

        try {
            Iterable<Pipeline> pipelines = this.pipelineRepository.findAll();

            HashMap<String, Object> data = new HashMap<>();
            data.put("pipelines", pipelines);
            return this.sendResponse(response,"ok", 200, data);
        } catch (Exception e) {
            HashMap<String, Object> data = new HashMap<>();
            return this.sendError(response, "Internal Server Error", 500, data);
        }
    }

    @GetMapping("/api/v1/pipelines/paginate")
    public HashMap<String, Object> indexPaginate(@RequestParam Integer off, @RequestParam Integer sze, HttpServletResponse response) {

        try {
            Page<Pipeline> pipelines = this.pipelineRepository.findAll(PageRequest.of(off, sze));

            HashMap<String, Object> data = new HashMap<>();
            data.put("pipelines", pipelines);
            return this.sendResponse(response,"ok", 200, data);
        } catch (Exception e) {
            HashMap<String, Object> data = new HashMap<>();
            return this.sendError(response, "Internal Server Error", 500, data);
        }
    }

    @GetMapping("/api/v1/pipelines/{pipelineId}")
    public HashMap<String, Object> show(@PathVariable Long pipelineId, HttpServletResponse response) {

        try {
            Optional<Pipeline> pipeline = this.pipelineRepository.findById(pipelineId);

            if (pipeline.isEmpty()) {
                return this.sendError(response, "Not Found", 404, new HashMap<String, Object>());
            }

            HashMap<String, Object> data = new HashMap<>();
            data.put("pipeline", pipeline);

            return this.sendResponse(response,"ok", 200, data);
        } catch (Exception e) {
            return this.sendError(response, "Internal Server Error", 500, new HashMap<String, Object>());
        }
    }


    @PostMapping("/api/v1/pipelines")
    public HashMap<String, Object> store(@Valid @RequestBody Pipeline newPipeline, HttpServletResponse response) {

        try {
            String name = newPipeline.getName();
            Iterable<Pipeline> pipelines = this.pipelineRepository.findAll();

            // TODO: implement findByName() on pipelineRepository
            for (var pipeline : pipelines) {
                if (pipeline.getName().equals(name)) {
                    return this.sendError(response, "Pipeline already exists with this name", 500, new HashMap<String, Object>());
                }
            }

            this.pipelineRepository.save(newPipeline);

            return this.sendResponse(response,"Pipeline added successfully", 201, new HashMap<String, Object>());
        } catch (Exception e) {
            return this.sendError(response, "Internal Server Error", 500, new HashMap<String, Object>());
        }
    }


    @PutMapping("/api/v1/pipelines/{pipelineId}")
    public HashMap<String, Object> edit(@PathVariable long pipelineId, @Valid @RequestBody Pipeline editPipeline, HttpServletResponse response) {

        try {
            Pipeline pipeline = this.pipelineRepository.findById(pipelineId).orElse(null);

            if (pipeline == null) {
                return this.sendError(response, "Not Found", 404, new HashMap<String, Object>());
            }

            pipeline.setName(editPipeline.getName());
            pipeline.setContent(editPipeline.getContent());
            this.pipelineRepository.save(pipeline);

            return this.sendResponse(response,"Pipeline edited successfully", 202, new HashMap<String, Object>());
        } catch (Exception e) {
            return this.sendError(response, "Internal Server Error", 500, new HashMap<String, Object>());
        }
    }


    @DeleteMapping("/api/v1/pipelines/{pipelineId}")
    public HashMap<String, Object> delete(@PathVariable long pipelineId, HttpServletResponse response) {

        try {
            Pipeline pipeline = this.pipelineRepository.findById(pipelineId).orElse(null);

            if (pipeline == null) {
                return this.sendError(response, "Not Found", 404, new HashMap<String, Object>());
            }

            this.pipelineRepository.delete(pipeline);

            return this.sendResponse(response,"Pipeline deleted successfully", 202, new HashMap<String, Object>());
        } catch (Exception e) {
            return this.sendError(response, "Internal Server Error", 500, new HashMap<String, Object>());
        }
    }


    @GetMapping("/api/v1/pipelines/search")
    public HashMap<String, Object> search(@RequestParam String q, HttpServletResponse response) {

        try {

            int page = 0;
            int size = 20;

            Pageable pageable = PageRequest.of(page, size);

            Page<Pipeline> pipelines = this.pipelineRepository.findBySearchTerm(q, pageable);

            if (pipelines.isEmpty()) {
                return this.sendError(response, "Not Found", 404, new HashMap<String, Object>());
            }

            HashMap<String, Object> data = new HashMap<>();
            data.put("pipelines", pipelines);

            return this.sendResponse(response,"ok", 200, data);
        } catch (Exception e) {
            return this.sendError(response, "Internal Server Error", 500, new HashMap<String, Object>());
        }
    }


    @GetMapping("/api/v1/pipelines/user/{userId}")
    public HashMap<String, Object> showUserPolicies(@PathVariable String userId, HttpServletResponse response) {

        try {
            List<PipelineUser> pipelinesIds = this.pipelineUserRepository.findByUserId(userId);

            if (pipelinesIds.isEmpty()) {
                return this.sendError(response, "User not attached to any pipeline", 404, new HashMap<String, Object>());
            }

            ArrayList<Pipeline> pipelines = new ArrayList<>();

            for (var entry : pipelinesIds) {
                Long pipelineId = entry.getPipelineId();
                Pipeline pipeline = this.pipelineRepository.findById(pipelineId).orElse(null);

                // Exception
                if (pipeline == null) {
                    return this.sendError(response, "Error during extract user related pipeline", 500, new HashMap<String, Object>());
                }

                pipelines.add(pipeline);
            }

            if (pipelines.isEmpty()) {
                return this.sendError(response, "Not Found", 404, new HashMap<String, Object>());
            }

            HashMap<String, Object> data = new HashMap<>();
            data.put("pipelines", pipelines);

            return this.sendResponse(response,"ok", 200, data);
        } catch (Exception e) {
            return this.sendError(response, "Internal Server Error", 500, new HashMap<String, Object>());
        }
    }


    @GetMapping("/api/v1/pipelines/users/{pipelineId}")
    public HashMap<String, Object> showPipelineUsers(@PathVariable long pipelineId, HttpServletResponse response) {

        try {
            List<PipelineUser> usersIds = this.pipelineUserRepository.findByPipelineId(pipelineId);

            if (usersIds.isEmpty()) {
                return this.sendError(response, "Pipeline not attached to any user", 404, new HashMap<String, Object>());
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


    @PostMapping("/api/v1/pipelines/attach")
    public HashMap<String, Object> attachPipeline(@Valid @RequestBody PipelineUser attachPipeline, HttpServletResponse response) {

        try {
            // TODO: Validate if userId exists by service-to-service REST request

            this.pipelineUserRepository.save(attachPipeline);
            return this.sendResponse(response,"Pipeline attached successfully", 201, new HashMap<String, Object>());
        } catch (Exception e) {
            return this.sendError(response, "Internal Server Error", 500, new HashMap<String, Object>());
        }
    }


    @PostMapping("/api/v1/pipelines/detach")
    public HashMap<String, Object> detachPipeline(@Valid @RequestBody PipelineUser detachPipeline, HttpServletResponse response) {

        try {

            String userId = detachPipeline.getUserId();
            Long pipelineId = detachPipeline.getPipelineId();

            List<PipelineUser> pipelinesAttached = this.pipelineUserRepository.findByUserId(userId);

            if (pipelinesAttached.isEmpty()) {
                return this.sendError(response, "User not attached to any pipeline", 404, new HashMap<String, Object>());
            }

            Long id = null;

            for (var entry : pipelinesAttached) {
                Long entryPipelineId = entry.getPipelineId();

                if (entryPipelineId.equals(pipelineId)) {
                    id = entry.getId();
                }
            }

            // Exception
            if (id == null) {
                return this.sendError(response, "Error during extract entry id", 500, new HashMap<String, Object>());
            }

            PipelineUser pipelineToDetach = this.pipelineUserRepository.findById(id).orElse(null);

            if (pipelineToDetach == null) {
                return this.sendError(response, "Not Found", 404, new HashMap<String, Object>());
            }

            this.pipelineUserRepository.delete(pipelineToDetach);

            return this.sendResponse(response,"Pipeline detached successfully", 202, new HashMap<String, Object>());
        } catch (Exception e) {
            return this.sendError(response, "Internal Server Error", 500, new HashMap<String, Object>());
        }
    }


}
