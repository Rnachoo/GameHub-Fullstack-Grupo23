package com.GameHub.controllers;

import com.GameHub.assemblers.AuthModelAssembler;
import com.GameHub.models.dtos.*;
import com.GameHub.services.AuthService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.hateoas.CollectionModel;import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
@RequestMapping("/api/v1/auths")
@Validated
@Tag(name = "Autenticacion V1", description = "Metodos Crud para la gestión del gestor de cuentas y credenciales")
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private AuthModelAssembler authModelAssembler;


    //Listar todas Las cuetnas registradas
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar todas las cuentas registradas", description = "Devuelve una lista de las cuentas registeradas (Solo para Admins)")
    @ApiResponse(responseCode = "200", description = "Operación Extiosa")
    public ResponseEntity<CollectionModel<EntityModel<AuthDetalleDTO>>> findAll(){
        List<EntityModel<AuthDetalleDTO>> entityModels = this.authService.findAll()
                .stream()
                .map(authModelAssembler::toModel)
                .toList();
        CollectionModel<EntityModel<AuthDetalleDTO>> collectionModel = CollectionModel.of(
                entityModels,
                linkTo(methodOn(AuthController.class).findAll()).withSelfRel()
        );
        return ResponseEntity.status(HttpStatus.OK).body(collectionModel);
    }


    //Buscar por el ID de una cuenta
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Búscar cuenta con el ID", description = "Devuelve los detalles de una cuenta coincidente con el ID registrado")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Cuenta encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthDetalleDTO.class))),
            @ApiResponse(
                    responseCode = "404", description = "Cuenta no encontrada")
    })
    public ResponseEntity<EntityModel<AuthDetalleDTO>> findById(
            @Parameter(description = "ID de la cuenta a buscar", required = true, example = "1")
            @PathVariable Long id
    ){
        EntityModel<AuthDetalleDTO> entityModel = this.authModelAssembler.toModel(authService.findById(id));
        return ResponseEntity.ok(entityModel);
    }

    //Buscar una cuenta por el email
    @GetMapping("/email/{email}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Busqueda de una cuenta por el Email", description = "Devuelve los detalles de una cuenta basándose en su Email")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Cuenta encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthDetalleDTO.class))),
            @ApiResponse(
                    responseCode = "404", description = "Cuenta no encontrada")
    })
    public ResponseEntity<EntityModel<AuthDetalleDTO>> findByEmail(
            @Parameter(description = "Email de la cuenta a encontrar", required = true, example = "correoelectronico@gmail.com")
            @PathVariable String email
    ){
        EntityModel<AuthDetalleDTO> entityModel = this.authModelAssembler.toModel(authService.findByEmail(email));
        return ResponseEntity.ok(entityModel);
    }

    //Registrar Cuentas
    @PostMapping
    @Operation(summary = "Crear cuentas",description = "Registra una cuenta cuenta nueva")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Cuenta a crear", required = true,
            content = @Content(schema = @Schema(implementation = AuthSaveDTO.class)))
    public ResponseEntity<EntityModel<AuthDetalleDTO>> save(@Valid @RequestBody AuthSaveDTO authSaveDTO){
        AuthDetalleDTO authCreate = this.authService.save(authSaveDTO);
        EntityModel<AuthDetalleDTO> entityModel = this.authModelAssembler.toModel(authCreate);
        return ResponseEntity.status(HttpStatus.CREATED).body(entityModel);
    }

    //Desactivar (No eliminar) por el ID
    @PatchMapping ("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Desactivación de una cuenta", description = "Desactiva una cuenta registrada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cuenta desactivada con exito"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    public ResponseEntity <EntityModel<AuthDetalleDTO>> desactiveById(
            @Parameter(description = "ID de la cuenta a desactivar", required = true, example = "1")
            @PathVariable Long id
    ){
        AuthDetalleDTO authDesactive = authService.desactiveById(id);
        EntityModel<AuthDetalleDTO> entityModel = this.authModelAssembler.toModel(authDesactive);
        return ResponseEntity.ok(entityModel);
    }

    //Actualizar la contraseña de una cuenta
    @PatchMapping("/{id}/password")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Actualizar contraseña de una cuenta", description = "Actualiza la clave una cuenta registrada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contraseña de la cuenta actualizada con exito"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    public ResponseEntity<EntityModel<AuthDetalleDTO>> updatePassword  (
            @Parameter(description = "ID de la cuenta a actualizar", required = true, example = "1")
            @PathVariable Long id, @Valid @RequestBody AuthUpdatePasswordDTO passwordDTO
    ){
        AuthDetalleDTO authUpdate = authService.updatePassword(id, passwordDTO);
        EntityModel<AuthDetalleDTO> entityModel = this.authModelAssembler.toModel(authUpdate);
        return ResponseEntity.status(HttpStatus.OK).body(entityModel);

    }

    //Actualizar el rol de una cuenta
    @PatchMapping("/{id}/rol")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar Rol de una cuenta", description = "Actualiza el Rol de  una cuenta registrada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rol de la cuenta actualizada con exito"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    public ResponseEntity<EntityModel<AuthDetalleDTO>> updateRol (
            @Parameter(description = "ID de la cuenta a actualizar", required = true, example = "1")
            @PathVariable Long id, @Valid @RequestBody AuthUpdateRolDTO rolDTO){
        AuthDetalleDTO authUpdate = authService.updateRol(id, rolDTO);
        EntityModel<AuthDetalleDTO> entityModel = this.authModelAssembler.toModel(authUpdate);
        return ResponseEntity.status(HttpStatus.OK).body(entityModel);
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Valida las credenciales y devuelve los datos de la cuenta")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Login exitoso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthDetalleDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Petición mal formada (ej: email o contraseña en blanco)"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Credenciales inválidas o cuenta desactivada"
            )
    })
    public ResponseEntity<EntityModel<AuthDetalleDTO>> login(@RequestBody AuthLoginDTO authLoginDTO) {
        AuthDetalleDTO response = authService.login(authLoginDTO);
        EntityModel<AuthDetalleDTO> entityModel = this.authModelAssembler.toModel(response);
        return ResponseEntity.ok(entityModel);
    }
}
