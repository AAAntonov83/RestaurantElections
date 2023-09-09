package com.restaurant_elections.web.user;

import com.restaurant_elections.model.User;
import com.restaurant_elections.to.UserTo;
import com.restaurant_elections.util.UsersUtil;
import com.restaurant_elections.web.AuthUser;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import static com.restaurant_elections.util.validation.ValidationUtil.assureIdConsistent;
import static com.restaurant_elections.util.validation.ValidationUtil.checkNew;

@RestController
@RequestMapping(ProfileController.REST_URL)
@Slf4j
@AllArgsConstructor
public class ProfileController extends AbstractUserController{
    static final String REST_URL = "/api/profile";

    @GetMapping
    @Cacheable(value = "users", key = "#authUser.user.email")
    public User get(@AuthenticationPrincipal AuthUser authUser) {
        log.info("get {}", authUser);
        return authUser.getUser();
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(value = "users", key = "#authUser.user.email")
    public void delete(@AuthenticationPrincipal AuthUser authUser) {
        super.delete(authUser.id());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @CachePut(value = "users", key = "#userTo.email")
    public User register(@Valid @RequestBody UserTo userTo) {
        log.info("register {}", userTo);
        checkNew(userTo);
        return repository.prepareAndSave(UsersUtil.createNewFromTo(userTo));
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    @CacheEvict(value = "users", key = "#authUser.user.email")
    public void update(@RequestBody @Valid UserTo userTo, @AuthenticationPrincipal AuthUser authUser) {
        log.info("update {} with id={}", userTo, authUser.id());
        assureIdConsistent(userTo, authUser.id());
        User user = authUser.getUser();
        repository.prepareAndSave(UsersUtil.updateFromTo(user, userTo));
    }
}
