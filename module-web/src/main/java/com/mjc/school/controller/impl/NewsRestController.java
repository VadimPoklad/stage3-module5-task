package com.mjc.school.controller.impl;

import com.mjc.school.controller.BaseController;
import com.mjc.school.service.implementation.dto.request.full.NewsDtoFullRequest;
import com.mjc.school.service.implementation.dto.response.NewsDtoResponse;
import com.mjc.school.service.implementation.NewsService;
import com.mjc.school.service.implementation.dto.request.NewsDtoRequest;
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
@RequestMapping(value = "/news", produces = "application/json")
@Api(produces = "application/json", value = "Operations for creating, updating, retrieving and deleting news in the application")
public class NewsRestController implements BaseController<NewsDtoRequest,
        NewsDtoFullRequest, NewsDtoResponse, Long> {
    @Autowired
    private NewsService newsService;

    @ApiOperation(value = "View news", response = NewsDtoResponse.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved news by page"),
            @ApiResponse(code = 400, message = "Bad request, incorrect parameters"),
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public CollectionModel<NewsDtoResponse> getAll(
            @Min(0) @RequestParam(required = false, defaultValue = "0") int page,
            @Min(1) @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "id") String sortedBy) {
        CollectionModel<NewsDtoResponse> collectionModel = CollectionModel.of(newsService.getAll(PageRequest.of(page, size, Sort.by(sortedBy))));

        for (NewsDtoResponse news : collectionModel) {
            news
                    .add(linkTo(methodOn(NewsRestController.class).getById(news.getId())).withSelfRel())
                    .add(linkTo(methodOn(AuthorRestController.class).getByNewsId(news.getId())).withRel("author"))
                    .add(linkTo(methodOn(TagRestController.class).getAllByNewsId(news.getId())).withRel("tags"))
                    .add(linkTo(methodOn(CommentRestController.class).getAllByNewsId(news.getId())).withRel("comments"));
        }
        collectionModel.add(linkTo(NewsRestController.class).withSelfRel());
        collectionModel.add(linkTo(methodOn(NewsRestController.class)
                .getAll(page, size, sortedBy)).withRel("page"));
        return collectionModel;
    }

    @ApiOperation(value = "View news by id", response = NewsDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved news by id"),
            @ApiResponse(code = 400, message = "Bad request, incorrect parameters"),
            @ApiResponse(code = 404, message = "News not found"),
    })
    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public NewsDtoResponse getById(@PathVariable Long id) {
        NewsDtoResponse newsDtoResponse = newsService.getById(id);
        newsDtoResponse
                .add(linkTo(methodOn(NewsRestController.class).getById(id)).withSelfRel())
                .add(linkTo(methodOn(AuthorRestController.class).getByNewsId(id)).withRel("author"))
                .add(linkTo(methodOn(TagRestController.class).getAllByNewsId(id)).withRel("tags"))
                .add(linkTo(methodOn(CommentRestController.class).getAllByNewsId(id)).withRel("comments"));
        return newsDtoResponse;

    }

    @ApiOperation(value = "Create news", response = NewsDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully created news"),
            @ApiResponse(code = 400, message = "Illegal input for this model(dependency not found or illegal field input)")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NewsDtoResponse create(@Valid @RequestBody NewsDtoFullRequest dtoRequest) {
        NewsDtoResponse news = newsService.create(dtoRequest);
        news
                .add(linkTo(methodOn(NewsRestController.class).getById(news.getId())).withSelfRel())
                .add(linkTo(methodOn(AuthorRestController.class).getByNewsId(news.getId())).withRel("author"))
                .add(linkTo(methodOn(TagRestController.class).getAllByNewsId(news.getId())).withRel("tags"))
                .add(linkTo(methodOn(CommentRestController.class).getAllByNewsId(news.getId())).withRel("comments"));
        return news;
    }

    @ApiOperation(value = "Update news", response = NewsDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated news"),
            @ApiResponse(code = 400, message = "Illegal input for this model(dependency not found or illegal field input)"),
            @ApiResponse(code = 404, message = "News not found"),
    })
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public NewsDtoResponse update(@Valid @RequestBody NewsDtoFullRequest dtoRequest, @PathVariable Long id) {
        NewsDtoResponse news = newsService.updateById(id, dtoRequest);
        news
                .add(linkTo(methodOn(NewsRestController.class).getById(id)).withSelfRel())
                .add(linkTo(methodOn(AuthorRestController.class).getByNewsId(news.getId())).withRel("author"))
                .add(linkTo(methodOn(TagRestController.class).getAllByNewsId(news.getId())).withRel("tags"))
                .add(linkTo(methodOn(CommentRestController.class).getAllByNewsId(news.getId())).withRel("comments"));
        return news;
    }

    @ApiOperation(value = "Patch news", response = NewsDtoResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully patched news"),
            @ApiResponse(code = 400, message = "Illegal input for this model(dependency not found or illegal field input)"),
            @ApiResponse(code = 404, message = "News not found"),
    })
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public NewsDtoResponse patch(@Valid @RequestBody NewsDtoRequest dtoRequest, @PathVariable Long id) {
        NewsDtoResponse news = newsService.patchById(id, dtoRequest);
        news
                .add(linkTo(methodOn(NewsRestController.class).getById(id)).withSelfRel())
                .add(linkTo(methodOn(AuthorRestController.class).getByNewsId(id)).withRel("author"))
                .add(linkTo(methodOn(TagRestController.class).getAllByNewsId(id)).withRel("tags"))
                .add(linkTo(methodOn(CommentRestController.class).getAllByNewsId(id)).withRel("comments"));
        return news;
    }

    @ApiOperation(value = "Delete news")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successfully deleted"),
            @ApiResponse(code = 404, message = "News not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        newsService.removeById(id);
    }
}