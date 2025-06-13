package com.example.medjool.controller;

import com.example.medjool.dto.NewPasswordDto;
import com.example.medjool.dto.NewUserDto;
import com.example.medjool.dto.UserDetailsDto;
import com.example.medjool.services.implementation.UserManagementServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * Controller for managing user accounts, including activation, holding, updating details, resetting passwords, and deleting accounts.
 */


@RestController
@RequestMapping("/api/user")
public class UserManagementController {

    private final UserManagementServiceImpl userManagementService;

    @Autowired
    public UserManagementController(UserManagementServiceImpl userManagementService) {
        this.userManagementService = userManagementService;
    }
    /** * Retrieves all user accounts.
     *
     * @return ResponseEntity containing a list of UserDetailsDto objects representing all users.
     */
    @GetMapping("/get_all")
    public ResponseEntity<List<UserDetailsDto>> getAllUsers() {
        return userManagementService.getAllUsers();
    }


    /**     * Activates a user account by user ID.
     *
     * @param userId the ID of the user to activate
     * @return ResponseEntity indicating the result of the activation operation
     */
    @PutMapping("/account/activate/{userId}")
    public ResponseEntity<Object> activateUserAccount(@PathVariable long userId) {
        return userManagementService.activateUserAccount(userId);
    }

    /**     * Holds a user account by user ID.
     *
     * @param userId the ID of the user to hold
     * @return ResponseEntity indicating the result of the hold operation
     */
    @PutMapping("/account/hold/{userId}")
    public ResponseEntity<Object> holdUserAccount(@PathVariable long userId) {
        return userManagementService.holdUserAccount(userId);
    }

    /**     * Updates user details for a specific user by user ID.
     *
     * @param userId the ID of the user to update
     * @param userDetailsDto the DTO containing updated user details
     * @return ResponseEntity indicating the result of the update operation
     */
    @PutMapping("/account/update/{userId}")
    public ResponseEntity<Object> updateUserDetails(@PathVariable long userId, @RequestBody UserDetailsDto userDetailsDto) {
        return userManagementService.updateUserDetails(userId, userDetailsDto);
    }


    /**     * Resets the password for a specific user by user ID.
     *
     * @param userId the ID of the user whose password is to be reset
     * @param newPassword the DTO containing the new password details
     * @return ResponseEntity indicating the result of the password reset operation
     */
    @PutMapping("/account/password-reset/{userId}")
    public ResponseEntity<Object> resetUserPassword(@PathVariable long userId, @RequestBody NewPasswordDto newPassword) {
        return userManagementService.resetUserPassword(userId, newPassword);
    }

    /**     * Deletes a user account by user ID.
     *
     * @param userId the ID of the user to delete
     * @return ResponseEntity indicating the result of the deletion operation
     */
    @DeleteMapping("/account/delete/{userId}")
    public ResponseEntity<Object> deleteUserAccount(@PathVariable long userId) {
        return userManagementService.deleteUserAccount(userId);
    }

    /**     * Creates a new user account with the provided details.
     *
     * @param userDetailsDto the DTO containing new user details
     * @return ResponseEntity indicating the result of the account creation operation
     */
    @PostMapping("/account/new")
    public ResponseEntity<Object> createNewUser(@RequestBody NewUserDto userDetailsDto) {
        return userManagementService.createUserAccount(userDetailsDto);
    }
}
