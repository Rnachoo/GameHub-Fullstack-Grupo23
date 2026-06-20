package com.GameHub.controllers;

import com.GameHub.assemblers.UserModelAssembler;
import com.GameHub.models.dtos.UserDetalleDTO;
import com.GameHub.models.dtos.UserSaveDTO;
import com.GameHub.models.dtos.UserUpdateDirectionDTO;
import com.GameHub.models.dtos.UserUpdateTelefonoDTO;
import com.GameHub.services.UserService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.CollectionModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/users")
@Validated
@Tag(name = "Usuarios V1", description = "Metodos Crud para la gestión del gestor de usuarios y credenciales")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserModelAssembler userModelAssembler;

    //Encontrar a todos los usuarios registrados
    @GetMapping
    @Operation(
            summary = "Listado de todos los usuarios con sus direcciones",
            description = "Devuelve la información de todos los usuarios con sus direcciones que esten registrados en el sistema")
    @ApiResponse(responseCode = "200", description = "Operación exitosa"
    )
    public ResponseEntity<CollectionModel<EntityModel<UserDetalleDTO>>> findAll(){
        List<EntityModel<UserDetalleDTO>> entityModels = this.userService.findAll()
                .stream()
                .map(userModelAssembler::toModel)
                .toList();
        CollectionModel<EntityModel<UserDetalleDTO>> collectionModel = CollectionModel.of(
                entityModels,
                linkTo(methodOn(UserController.class).findAll()).withSelfRel()
        );
        return ResponseEntity.status(HttpStatus.OK).body(collectionModel);
    }

    //Buscar un usuario por el ID
    @GetMapping("/{id}")
    @Operation(
            summary = "Busqueda de un Usuario por ID",
            description = "Devuelve los detalles de un usuario especifica. Lanza una excepción si no la encuentra"
    )
    @ApiResponses(value={
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuario encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDetalleDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado en la base de datos")
    })
    public ResponseEntity<EntityModel<UserDetalleDTO>> findById(
            @Parameter(description = "ID del Usuario a buscar", required = true, example = "1")
            @PathVariable Long id
    ){
        EntityModel<UserDetalleDTO> entityModel = this.userModelAssembler.toModel(userService.findById(id));
        return ResponseEntity.status(HttpStatus.OK).body(entityModel);
    }

    //Buscar un usuario por el email
    @GetMapping("/email/{email}")
    @Operation(summary = "Busqueda de un usuario por el Email", description = "Devuelve los detalles de un usuario basándose en su Email")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuario encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDetalleDTO.class))),
            @ApiResponse(
                    responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<EntityModel<UserDetalleDTO>> findByEmail(
            @Parameter(description = "Email del usuario a buscar", required = true, example = "correoelectronico@gmail.com")
            @PathVariable String email
    ){
        EntityModel<UserDetalleDTO> entityModel = this.userModelAssembler.toModel(userService.findByEmail(email));
        return ResponseEntity.ok(entityModel);
    }

    //Buscar un usuario por el estado
    @GetMapping("/estado/{estado}")
    @Operation(summary = "Búsqueda de un usuario por el Estado", description = "Devuelve una lista de usuarios basándose en su Estado")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuario encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDetalleDTO.class))),
            @ApiResponse(
                    responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<CollectionModel<EntityModel<UserDetalleDTO>>> findByEstado(
            @Parameter(description = "Estado de los usuarios a buscar", required = true, example = "Active")
            @PathVariable String estado
    ){
        List<EntityModel<UserDetalleDTO>> entityModels = this.userService.findByEstado(estado)
                .stream()
                .map(userModelAssembler::toModel)
                .toList();
        CollectionModel<EntityModel<UserDetalleDTO>> collectionModel = CollectionModel.of(entityModels);

        return ResponseEntity.status(HttpStatus.OK).body(collectionModel);
    }
    //Crear un usuario en el sistema
    @PostMapping
    @Operation(summary = "Crear usuarios",description = "Registra un usuario nuevo")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Usuario a crear", required = true,
            content = @Content(schema = @Schema(implementation = UserSaveDTO.class)))
    public ResponseEntity<EntityModel<UserDetalleDTO>> save(@Valid @RequestBody UserSaveDTO userSaveDTO){
        UserDetalleDTO userCreate = this.userService.save(userSaveDTO);
        EntityModel<UserDetalleDTO> entityModel = this.userModelAssembler.toModel(userCreate);
        return ResponseEntity.status(HttpStatus.CREATED).body(entityModel);

    }

    //Desactivar (No eliminar) por el ID
    @PatchMapping ("/{id}")
    @Operation(summary = "Desactivación de un Usuario", description = "Desactiva un Usuario registrado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario desactivado con exito"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity <EntityModel<UserDetalleDTO>> desactiveById(
            @Parameter(description = "ID del Usuario a desactivar", required = true, example = "1")
            @PathVariable Long id
    ){
        UserDetalleDTO userDesactive = userService.desactiveById(id);
        EntityModel<UserDetalleDTO> entityModel = this.userModelAssembler.toModel(userDesactive);
        return ResponseEntity.ok(entityModel);

    }

    //Actualizar el telefono de una cuenta
    @PatchMapping("/{id}/telefono")
    @Operation(summary = "Actualizar teléfono de un Usuario", description = "Actualiza el teléfono de un Usuario registrada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Teléfono del Usuario actualizado con éxito"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<EntityModel<UserDetalleDTO>> updateTelefono(
            @Parameter(description = "ID del Usuario a actualizar", required = true, example = "1")
            @PathVariable Long id, @Valid @RequestBody UserUpdateTelefonoDTO telefonoDTO
    ){
        UserDetalleDTO userUpdate = userService.updateTelefono(id, telefonoDTO);
        EntityModel<UserDetalleDTO> entityModel = this.userModelAssembler.toModel(userUpdate);
        return ResponseEntity.status(HttpStatus.OK).body(entityModel);
    }

    //Acutalizar la dirección de un usuario
    @PatchMapping("/{id}/directions")
    @Operation(summary = "Actualizar dirección de un Usuario", description = "Actualiza la dirección de un Usuario registrada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "dirección del Usuario actualizado con éxito"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<EntityModel<UserDetalleDTO>> updateDirection (
            @Parameter(description = "ID del Usuario a actualizar", required = true, example = "1")
            @PathVariable Long id, @Valid @RequestBody UserUpdateDirectionDTO directionDTO
    ){
        UserDetalleDTO userUpdate = userService.updateDirection(id, directionDTO);
        EntityModel<UserDetalleDTO> entityModel = this.userModelAssembler.toModel(userUpdate);
        return ResponseEntity.status(HttpStatus.OK).body(entityModel);
    }
}
