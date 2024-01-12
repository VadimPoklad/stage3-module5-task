package com.mjc.school.controller.impl;

import com.mjc.school.controller.BaseController;
import com.mjc.school.service.implementation.AuthorService;
import com.mjc.school.service.implementation.dto.request.AuthorDtoRequest;
import com.mjc.school.service.implementation.dto.request.full.AuthorDtoFullRequest;
import com.mjc.school.service.implementation.dto.response.AuthorDtoResponse;
import io.swagger.annotations.*;
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
@RequestMapping(value = "/authors", produces = "application/json")
@Api(produces = "application/json", value = "Operations for creating, updating, retrieving and deleting authors in the application")
public class AuthorRestController implements BaseController<AuthorDtoRequest, AuthorDtoFullRequest, AuthorDtoResponse, Long> {
    @Autowired
    private AuthorService authorService;

    @ApiOperation(value = "View authors", response = AuthorDtoResponse.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved authors by page"),
            @ApiResponse(code = 400, message = "Bad request, incorrect parameters"),
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public CollectionModel<AuthorDtoResponse> getAll(
            @Min(0) @RequestParam(required = false, defaultValue = "0") int page,
            @Min(1) @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "id") String sortedBy
    ) {
        CollectionModel<AuthorDtoResponse> collectionModel =
                CollectionModel.of(authorService.getAll(
                        PageRequest.of(page, size, Sort.by(sortedBy))));
        collectionModel.forEach(dto ->
                dto.add(linkTo(AuthorRestController.class).slash(dto.getId()).withSelfRel()));

        collectionModel.add(linkTo(AuthorRestController.class).withSelfRel());
        collectionModel.add(linkTo(methodOn(AuthorRestController.class)
                .getAll(page, size, sortedBy)).withRel("page"));
        return collectionModel;
    }

    @ApiOperation(value = "View author by id", response = AuthorDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved author by id"),
            @ApiResponse(code = 400, message = "Bad request, incorrect parameters"),
            @ApiResponse(code = 404, message = "Author not found"),
    })
    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AuthorDtoResponse getById(@PathVariable Long id) {
        return authorService.getById(id)
                .add(linkTo(AuthorRestController.class).slash(id).withSelfRel());
    }

    @ApiOperation(value = "View author by news id", response = AuthorDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved author by news id"),
            @ApiResponse(code = 400, message = "Bad request, incorrect parameters"),
            @ApiResponse(code = 404, message = "News not found"),
    })
    @GetMapping(value = "/news/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AuthorDtoResponse getByNewsId(@PathVariable Long id) {
        return authorService.getByNewsId(id)
                .add(linkTo(methodOn(AuthorRestController.class).getByNewsId(id)).withSelfRel());
    }

    @ApiOperation(value = "Create author", response = AuthorDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully created author"),
            @ApiResponse(code = 400, message = "Illegal input for this model")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AuthorDtoResponse create(@Valid @RequestBody AuthorDtoFullRequest dtoFullRequest) {
        AuthorDtoResponse response = authorService.create(dtoFullRequest);
        return response
                .add(linkTo(AuthorRestController.class).slash(response.getId()).withSelfRel());
    }
    @ApiOperation(value = "Put author", response = AuthorDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated author"),
            @ApiResponse(code = 400, message = "Illegal input for this model"),
            @ApiResponse(code = 404, message = "Author not found")
    })
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AuthorDtoResponse update(@Valid @RequestBody AuthorDtoFullRequest dtoFullRequest, @PathVariable Long id) {
        return authorService.updateById(id, dtoFullRequest)
                .add(linkTo(AuthorRestController.class).slash(id).withSelfRel());
    }

    @ApiOperation(value = "Patch author", response = AuthorDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully patched author"),
            @ApiResponse(code = 400, message = "Illegal input for this model"),
            @ApiResponse(code = 404, message = "Author not found")
    })
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AuthorDtoResponse patch(@Valid @RequestBody AuthorDtoRequest dtoRequest, @PathVariable Long id) {
        return authorService.patchById(id, dtoRequest)
                .add(linkTo(AuthorRestController.class).slash(id).withSelfRel());
    }

    @ApiOperation(value = "Delete author")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successfully deleted"),
            @ApiResponse(code = 404, message = "Author not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        authorService.removeById(id);
    }
}
