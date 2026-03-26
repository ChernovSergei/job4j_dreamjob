import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ui.ConcurrentModel;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import ru.job4j.dreamjob.controller.UserController;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.service.UserService;

public class UserControllerTest {

    private UserService userService;
    private UserController userController;

    @BeforeEach
    public void initServices() {
        userService = mock(UserService.class);
        userController = new UserController(userService);

    }

    @Test
    public void whenUserRegisterPageThenUserRegister() {
        var view = userController.getRegistrationPage();

        assertThat(view).isEqualTo("users/register");
    }

    @Test   
    public void whenUserRegisteredThenRedirectToVacanciesPage() throws Exception {
        var model = new ConcurrentModel();
        var user = new User(1, "testEmail", "testName", "testPassword");
        var userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        when(userService.save(userArgumentCaptor.capture())).thenReturn(Optional.of(user));

        var view = userController.register(user, model);
        var actualUser = userArgumentCaptor.getValue();

        assertThat(view).isEqualTo("redirect:/vacancies");
        assertThat(actualUser).isEqualTo(user);
    }

    @Test   
    public void whenUserRegisterFailThenGetErrorMessages() throws Exception {
        var model = new ConcurrentModel();
        var user = new User(1, "testEmail", "testName", "testPassword");
        var userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        String errorMessage = "The user with the same email exists";
        when(userService.save(userArgumentCaptor.capture())).thenReturn(Optional.empty());

        var view = userController.register(user, model);
        var actualUser = userArgumentCaptor.getValue();

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualUser).isEqualTo(user);
        assertThat(errorMessage).isEqualTo(model.get("message"));
    }

    @Test
    public void whenLoginUserSuccessThenGetVacanciesPageAndSetupSession() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        var model = new ConcurrentModel();
        var user = new User(1, "testEmail", "testName", "testPassword");
        var nameUserCaptor = ArgumentCaptor.forClass(String.class);
        var passwordUserCaptor = ArgumentCaptor.forClass(String.class);
        var sessionKey = ArgumentCaptor.forClass(String.class);
        var userCaptor = ArgumentCaptor.forClass(User.class);
        when(userService.findByEmailAndPassword(nameUserCaptor.capture(), passwordUserCaptor.capture())).thenReturn(Optional.of(user));
        when(request.getSession()).thenReturn(session);

        var view = userController.loginUser(user, model, request);
        verify(session).setAttribute(sessionKey.capture(), userCaptor.capture());
        String actualUserName = nameUserCaptor.getValue();
        String actualUserPassword = passwordUserCaptor.getValue();
        String actualSessionKey = sessionKey.getValue();
        User actualUser = userCaptor.getValue();

        assertThat(view).isEqualTo("redirect:/vacancies");
        assertThat(actualUserName).isEqualTo(user.getEmail());
        assertThat(actualUserPassword).isEqualTo(user.getPassword());
        assertThat(actualUser).isEqualTo(user);
        assertThat(actualSessionKey).isEqualTo("user");
        assertThat(user).isEqualTo(userCaptor.getValue());

    }

    @Test
    public void whenLoginUserSuccessThenErrorMessages() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        var model = new ConcurrentModel();
        var user = new User(1, "testEmail", "testName", "testPassword");
        var nameUserCaptor = ArgumentCaptor.forClass(String.class);
        var passwordUserCaptor = ArgumentCaptor.forClass(String.class);
        when(userService.findByEmailAndPassword(nameUserCaptor.capture(), passwordUserCaptor.capture())).thenReturn(Optional.empty());

        var view = userController.loginUser(user, model, request);
        String actualUserName = nameUserCaptor.getValue();
        String actualUserPassword = passwordUserCaptor.getValue();
        String errorMessage = "Email or password are not correct";

        assertThat(view).isEqualTo("users/login");
        assertThat(actualUserName).isEqualTo(user.getEmail());
        assertThat(actualUserPassword).isEqualTo(user.getPassword());
        assertThat(errorMessage).isEqualTo(model.get("error"));
    }
}
