package com.mjc.school.controller.impl;

import com.mjc.school.controller.BaseController;
import com.mjc.school.service.implementation.dto.request.TagDtoRequest;
import com.mjc.school.service.implementation.dto.request.full.TagDtoFullRequest;
import com.mjc.school.service.implementation.dto.response.TagDtoResponse;
import com.mjc.school.service.implementation.TagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(value = "/tags", produces = "application/json")
@Api(produces = "application/json", value = "Operations for creating, updating, retrieving and deleting tags in the application")
public class TagRestController implements BaseController<TagDtoRequest, TagDtoFullRequest, TagDtoResponse, Long> {
    @Autowired
    private TagService service;

    @ApiOperation(value = "View tags", response = TagDtoResponse.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved tags by page"),
            @ApiResponse(code = 400, message = "Bad request, incorrect parameters"),
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public CollectionModel<TagDtoResponse> getAll(
            @Min(0) @RequestParam(required = false, defaultValue = "0") int page,
            @Min(1) @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "id") String sortedBy) {
        CollectionModel<TagDtoResponse> collectionModel = CollectionModel.of(service.getAll(PageRequest.of(page, size, Sort.by(sortedBy))));
        collectionModel.forEach(dto ->
                dto.add(linkTo(TagRestController.class).slash(dto.getId()).withSelfRel()));
        collectionModel.add(linkTo(TagRestController.class).withSelfRel());
        collectionModel.add(linkTo(methodOn(TagRestController.class)
                .getAll(page, size, sortedBy)).withRel("page"));
        return collectionModel;
    }

    @ApiOperation(value = "View tags by news id", response = TagDtoResponse.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved tags by news id"),
            @ApiResponse(code = 400, message = "Bad request, incorrect parameters"),
            @ApiResponse(code = 404, message = "News not found"),
    })
    @GetMapping(value = "/news/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CollectionModel<TagDtoResponse> getAllByNewsId(@PathVariable Long id) {
        CollectionModel<TagDtoResponse> collectionModel = CollectionModel.of(service.getAllByNewsId(id));

        collectionModel.forEach(dto ->
                dto.add(linkTo(TagRestController.class).slash(dto.getId()).withSelfRel()));
        collectionModel
                .add(linkTo(methodOn(TagRestController.class).getAllByNewsId(id)).withSelfRel());
        return collectionModel;
    }

    @ApiOperation(value = "View tag by id", response = TagDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved tag by id"),
            @ApiResponse(code = 400, message = "Bad request, incorrect parameters"),
            @ApiResponse(code = 404, message = "Tag not found"),
    })
    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TagDtoResponse getById(@PathVariable Long id) {
        return service.getById(id)
                .add(linkTo(TagRestController.class).slash(id).withSelfRel());
    }

    @ApiOperation(value = "Create tag", response = TagDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully created tag"),
            @ApiResponse(code = 400, message = "Illegal input for this model")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TagDtoResponse create(@RequestBody TagDtoFullRequest dtoFullRequest) {
        TagDtoResponse createdTag = service.create(dtoFullRequest);
        return createdTag
                .add(linkTo(TagRestController.class).slash(createdTag.getId()).withSelfRel());
    }

    @ApiOperation(value = "Update tag", response = TagDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully updated tag"),
            @ApiResponse(code = 400, message = "Illegal input for this model"),
            @ApiResponse(code = 404, message = "Tag not found"),
    })
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TagDtoResponse update(@RequestBody TagDtoFullRequest dtoFullRequest, @PathVariable Long id) {
        TagDtoResponse response = service.updateById(id, dtoFullRequest);
        return response
                .add(linkTo(TagRestController.class).slash(id).withSelfRel());
    }

    @ApiOperation(value = "Patch tag", response = TagDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully patched tag"),
            @ApiResponse(code = 400, message = "Illegal input for this model"),
            @ApiResponse(code = 404, message = "Tag not found"),
    })
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TagDtoResponse patch(@RequestBody TagDtoRequest dtoRequest, @PathVariable Long id) {
        TagDtoResponse response = service.patchById(id, dtoRequest);
        return response
                .add(linkTo(TagRestController.class).slash(id).withSelfRel());
    }

    @ApiOperation(value = "Delete tag")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successfully deleted"),
            @ApiResponse(code = 404, message = "Tag not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.removeById(id);
    }
}