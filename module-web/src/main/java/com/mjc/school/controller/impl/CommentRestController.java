package com.mjc.school.controller.impl;

import com.mjc.school.controller.BaseController;
import com.mjc.school.service.implementation.CommentService;
import com.mjc.school.service.implementation.dto.request.full.CommentDtoFullRequest;
import com.mjc.school.service.implementation.dto.request.CommentDtoRequest;
import com.mjc.school.service.implementation.dto.response.CommentDtoResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Validated
@RestController
@RequestMapping(value = "/comments", produces = "application/json")
@Api(produces = "application/json", value = "Operations for creating, updating, retrieving and deleting comments in the application")
public class CommentRestController implements BaseController<CommentDtoRequest,
        CommentDtoFullRequest, CommentDtoResponse, Long> {
    @Autowired
    private CommentService service;

    @ApiOperation(value = "View comments", response = CommentDtoResponse.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved comments by page"),
            @ApiResponse(code = 400, message = "Bad request, incorrect parameters"),
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public CollectionModel<CommentDtoResponse> getAll(
            @Min(0) @RequestParam(required = false, defaultValue = "0") int page,
            @Min(1) @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "id") String sortedBy) {
        CollectionModel<CommentDtoResponse> collectionModel = CollectionModel.of(service.getAll(PageRequest.of(page, size, Sort.by(sortedBy))));

        collectionModel.forEach(dto ->
                dto.add(linkTo(CommentRestController.class).slash(dto.getId()).withSelfRel())
        );

        collectionModel.add(linkTo(CommentRestController.class).withSelfRel());
        collectionModel.add(linkTo(methodOn(CommentRestController.class)
                .getAll(page, size, sortedBy)).withRel("page"));
        return collectionModel;
    }

    @ApiOperation(value = "View comment by id", response = CommentDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved comment by id"),
            @ApiResponse(code = 400, message = "Bad request, incorrect parameters"),
            @ApiResponse(code = 404, message = "Comment not found"),
    })
    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDtoResponse getById(@PathVariable Long id) {
        CommentDtoResponse commentDtoResponse = service.getById(id);
        return commentDtoResponse
                .add(linkTo(CommentRestController.class).slash(id).withSelfRel());
    }

    @ApiOperation(value = "View comments by news id", response = CommentDtoResponse.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved comments by news id"),
            @ApiResponse(code = 400, message = "Bad request, incorrect parameters"),
            @ApiResponse(code = 404, message = "News not found"),
    })
    @GetMapping(value = "/news/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CollectionModel<CommentDtoResponse> getAllByNewsId(@PathVariable Long id) {
        CollectionModel<CommentDtoResponse> collectionModel = CollectionModel.of(service.getAllByNewsId(id));

        collectionModel.forEach(dto ->
                dto.add(linkTo(CommentRestController.class).slash(dto.getId()).withSelfRel())
        );

        collectionModel.add(linkTo(methodOn(CommentRestController.class).getAllByNewsId(id)).withSelfRel());
        return collectionModel;
    }

    @ApiOperation(value = "Create comment", response = CommentDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully created comment"),
            @ApiResponse(code = 400, message = "Illegal input for this model(dependency not found or illegal field input)")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDtoResponse create(@Valid @RequestBody CommentDtoFullRequest fullDtoRequest) {
        CommentDtoResponse createdComment = service.create(fullDtoRequest);
        return createdComment
                .add(linkTo(CommentRestController.class).slash(createdComment.getId()).withSelfRel());
    }

    @ApiOperation(value = "Update comment", response = CommentDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated comment"),
            @ApiResponse(code = 400, message = "Illegal input for this model(dependency not found or illegal field input)"),
            @ApiResponse(code = 404, message = "Comment not found")
    })
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDtoResponse update(@Valid @RequestBody CommentDtoFullRequest fullDtoRequest, @PathVariable Long id) {
        return service.updateById(id, fullDtoRequest)
                .add(linkTo(CommentRestController.class).slash(id).withSelfRel());
    }

    @ApiOperation(value = "Patch comment", response = CommentDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully patched comment"),
            @ApiResponse(code = 400, message = "Illegal input for this model(dependency not found or illegal field input)"),
            @ApiResponse(code = 404, message = "Comment not found")
    })
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDtoResponse patch(@Valid @RequestBody CommentDtoRequest dtoRequest, @PathVariable Long id) {
        return service.patchById(id, dtoRequest)
                .add(linkTo(CommentRestController.class).slash(id).withSelfRel());
    }

    @ApiOperation(value = "Delete comment")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successfully deleted"),
            @ApiResponse(code = 404, message = "Comment not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.removeById(id);
    }
}