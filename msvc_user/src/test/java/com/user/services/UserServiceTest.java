package com.user.services;

import com.user.exceptions.UserException;
import com.user.models.Direction;
import com.user.models.User;
import com.user.models.dtos.*;
import com.user.repositories.UserRepository;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User usuarioPrueba;
    private Direction direccionPrueba;
    private List<User> listaUsuarios = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        Faker faker = new Faker(Locale.of("es", "CL"));

        // Configuración de Dirección de prueba
        this.direccionPrueba = new Direction();
        this.direccionPrueba.setId(1L);
        this.direccionPrueba.setComuna("Providencia");
        this.direccionPrueba.setCiudad("Santiago");
        this.direccionPrueba.setCalle("Avenida Los Leones");
        this.direccionPrueba.setNumero("1234");

        // Configuración de Usuario de prueba
        this.usuarioPrueba = new User();
        this.usuarioPrueba.setId(1L);
        this.usuarioPrueba.setNombreUser("Juan Perez");
        this.usuarioPrueba.setEmail("juan.perez@gamehub.com");
        this.usuarioPrueba.setTelefono("+56912345678");
        this.usuarioPrueba.setEstado("Active");

        // Relación bidireccional
        this.direccionPrueba.setUser(this.usuarioPrueba);
        this.usuarioPrueba.getDirections().add(this.direccionPrueba);

        this.listaUsuarios.add(this.usuarioPrueba);

        // Generar lista masiva de usuarios
        for (int i = 0; i < 3; i++) {
            User u = new User();
            u.setId((long) (i + 2));
            u.setNombreUser(faker.name().fullName());
            u.setEmail(faker.internet().emailAddress());
            u.setTelefono(faker.phoneNumber().cellPhone());
            u.setEstado("Active");
            this.listaUsuarios.add(u);
        }
    }

    @Test
    @DisplayName("Debe listar todos los usuarios registrados")
    public void shouldFindAll() {
        when(this.userRepository.findAll()).thenReturn(this.listaUsuarios);

        List<UserDetalleDTO> result = this.userService.findAll();

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(4);
        assertThat(result.get(0).getDirectionsDTO().size()).isEqualTo(1);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe buscar un usuario por su email exitosamente")
    public void shouldFindByEmail() {
        String email = "juan.perez@gamehub.com";
        when(this.userRepository.findByEmail(email)).thenReturn(Optional.of(this.usuarioPrueba));

        UserDetalleDTO result = this.userService.findByEmail(email);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(email);
        assertThat(result.getNombreUser()).isEqualTo("Juan Perez");
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar un email inexistente")
    public void shouldThrowExceptionWhenFindByEmailNotFound() {
        String email = "noexiste@gamehub.com";
        when(this.userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.userService.findByEmail(email))
                .isInstanceOf(UserException.class)
                .hasMessage("Usuario con email " + email + " no encontrado");

        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("Debe listar usuarios filtrados por estado")
    public void shouldFindByEstado() {
        String estado = "Active";
        when(this.userRepository.findByEstado(estado)).thenReturn(this.listaUsuarios);

        List<UserDetalleDTO> result = this.userService.findByEstado(estado);

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(4);
        assertThat(result.get(0).getEstado()).isEqualTo(estado);
        verify(userRepository, times(1)).findByEstado(estado);
    }

    @Test
    @DisplayName("Debe buscar un usuario por su ID exitosamente")
    public void shouldFindById() {
        Long id = 1L;
        when(this.userRepository.findById(id)).thenReturn(Optional.of(this.usuarioPrueba));

        UserDetalleDTO result = this.userService.findById(id);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(userRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar por ID inexistente")
    public void shouldThrowExceptionWhenFindByIdNotFound() {
        Long id = 999L;
        when(this.userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.userService.findById(id))
                .isInstanceOf(UserException.class)
                .hasMessage("User con ID " + id + " no encontrado");

        verify(userRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Debe guardar un nuevo usuario con sus direcciones exitosamente")
    public void shouldSaveUserSuccessfully() {
        UserSaveDTO saveDTO = new UserSaveDTO();
        saveDTO.setNombreUser("Nuevo Usuario");
        saveDTO.setEmail("nuevo@gamehub.com");
        saveDTO.setTelefono("+56987654321");

        DirectionDetalleDTO dirDTO = new DirectionDetalleDTO();
        dirDTO.setComuna("Las Condes");
        dirDTO.setCiudad("Santiago");
        dirDTO.setCalle("Apoquindo");
        dirDTO.setNumero("5000");
        saveDTO.setDirectionsDTO(List.of(dirDTO));

        when(this.userRepository.existsByEmail(saveDTO.getEmail())).thenReturn(false);
        when(this.userRepository.save(any(User.class))).thenAnswer(i -> {
            User u = i.getArgument(0);
            u.setId(2L);
            return u;
        });

        UserDetalleDTO result = this.userService.save(saveDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getEmail()).isEqualTo("nuevo@gamehub.com");
        assertThat(result.getDirectionsDTO().size()).isEqualTo(1);
        assertThat(result.getDirectionsDTO().get(0).getComuna()).isEqualTo("Las Condes");

        verify(userRepository, times(1)).existsByEmail(saveDTO.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al intentar guardar un email ya registrado")
    public void shouldThrowExceptionWhenSaveDuplicateEmail() {
        UserSaveDTO saveDTO = new UserSaveDTO();
        saveDTO.setEmail("juan.perez@gamehub.com");

        when(this.userRepository.existsByEmail(saveDTO.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> this.userService.save(saveDTO))
                .isInstanceOf(UserException.class)
                .hasMessage("El correo electronico ya esta registrado");

        verify(userRepository, times(1)).existsByEmail(saveDTO.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Debe desactivar un usuario por su ID")
    public void shouldDesactiveById() {
        Long id = 1L;
        when(this.userRepository.findById(id)).thenReturn(Optional.of(this.usuarioPrueba));
        when(this.userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        UserDetalleDTO result = this.userService.desactiveById(id);

        assertThat(result.getEstado()).isEqualTo("Inactive");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Debe actualizar el teléfono de un usuario exitosamente")
    public void shouldUpdateTelefono() {
        Long id = 1L;
        UserUpdateTelefonoDTO telefonoDTO = new UserUpdateTelefonoDTO();
        telefonoDTO.setTelefono("+56911112222");

        when(this.userRepository.findById(id)).thenReturn(Optional.of(this.usuarioPrueba));
        when(this.userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        UserDetalleDTO result = this.userService.updateTelefono(id, telefonoDTO);

        assertThat(result.getTelefono()).isEqualTo("+56911112222");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Debe agregar una nueva dirección a un usuario exitosamente")
    public void shouldUpdateDirection() {
        Long id = 1L;
        UserUpdateDirectionDTO directionDTO = new UserUpdateDirectionDTO();
        directionDTO.setComuna("Maipú");
        directionDTO.setCiudad("Santiago");
        directionDTO.setCalle("Pajaritos");
        directionDTO.setNumero("9000");

        when(this.userRepository.findById(id)).thenReturn(Optional.of(this.usuarioPrueba));
        when(this.userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        UserDetalleDTO result = this.userService.updateDirection(id, directionDTO);

        assertThat(result.getDirectionsDTO().size()).isEqualTo(2); // Tenía 1, se agregó 1 nueva
        assertThat(result.getDirectionsDTO().get(1).getComuna()).isEqualTo("Maipú");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar dirección de un usuario inexistente")
    public void shouldThrowExceptionWhenUpdateDirectionNotFound() {
        Long id = 999L;
        UserUpdateDirectionDTO directionDTO = new UserUpdateDirectionDTO();

        when(this.userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.userService.updateDirection(id, directionDTO))
                .isInstanceOf(UserException.class)
                .hasMessage("Cuenta no encontrada, no se puede actualizar la dirección");

        verify(userRepository, never()).save(any(User.class));
    }
}